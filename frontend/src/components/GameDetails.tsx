import React, { useCallback, useEffect, useState } from 'react';
import { AUTH_CHANGED_EVENT, authService, commentService, productService } from '../services/api';
import { Comment, ProductWithComments } from '../types';
import './GameDetails.css';

interface GameDetailsProps {
  productId: number;
  onClose: () => void;
}

const GameDetails: React.FC<GameDetailsProps> = ({ productId, onClose }) => {
  const [data, setData] = useState<ProductWithComments | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [newComment, setNewComment] = useState<string>('');
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [currentUsername, setCurrentUsername] = useState<string | null>(null);
  const [hasToken, setHasToken] = useState<boolean>(() => Boolean(localStorage.getItem('token')));

  const fetchGameDetails = useCallback(async () => {
    try {
      setLoading(true);
      const result = await productService.getProductWithComments(productId);
      setData(result);
      setComments(result.comments);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch game details');
    } finally {
      setLoading(false);
    }
  }, [productId]);

  const fetchCurrentUser = useCallback(async () => {
    const token = localStorage.getItem('token');
    setHasToken(Boolean(token));

    if (!token) {
      setCurrentUserId(null);
      setCurrentUsername(null);
      return;
    }

    const user = await authService.getCurrentUser();
    if (user) {
      setCurrentUserId(user.id);
      setCurrentUsername(user.username);
      return;
    }

    setHasToken(false);
    setCurrentUserId(null);
    setCurrentUsername(null);
  }, []);

  useEffect(() => {
    fetchGameDetails();
    fetchCurrentUser();
  }, [fetchGameDetails, fetchCurrentUser]);

  useEffect(() => {
    const handleAuthChange = () => {
      fetchCurrentUser();
    };

    window.addEventListener(AUTH_CHANGED_EVENT, handleAuthChange);
    window.addEventListener('storage', handleAuthChange);

    return () => {
      window.removeEventListener(AUTH_CHANGED_EVENT, handleAuthChange);
      window.removeEventListener('storage', handleAuthChange);
    };
  }, [fetchCurrentUser]);

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) return 'Today';
    if (diffDays === 1) return 'Yesterday';
    if (diffDays < 7) return `${diffDays} days ago`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)} weeks ago`;
    if (diffDays < 365) return `${Math.floor(diffDays / 30)} months ago`;
    return date.toLocaleDateString();
  };

  const handleSubmitComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    const productRecordId = data?.product.id;
    if (!productRecordId) {
      alert('Failed to post comment. Product is not loaded yet.');
      return;
    }

    setSubmitting(true);
    try {
      const created = await commentService.createComment(productRecordId, newComment.trim());
      setComments(prev => [...prev, created]);
      setNewComment('');
      await fetchCurrentUser();
    } catch {
      alert('Failed to post comment. Make sure you are logged in.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteComment = async (commentId: number) => {
    if (!window.confirm('Delete your comment?')) return;

    try {
      await commentService.deleteComment(commentId);
      setComments(prev => prev.filter(comment => comment.id !== commentId));
    } catch {
      alert('Failed to delete comment.');
    }
  };

  if (loading) {
    return (
      <div className="game-details-overlay">
        <div className="game-details-modal">
          <div className="loading">Loading game details...</div>
        </div>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="game-details-overlay">
        <div className="game-details-modal">
          <div className="error">Error: {error || 'Game not found'}</div>
          <button onClick={onClose} className="close-button">Close</button>
        </div>
      </div>
    );
  }

  const { product } = data;
  const canComment = hasToken && currentUserId !== null;

  return (
    <div className="game-details-overlay" onClick={onClose}>
      <div className="game-details-modal" onClick={(e) => e.stopPropagation()}>
        <button onClick={onClose} className="close-button-top" aria-label="Close details">
          x
        </button>

        <div className="game-header">
          {product.imageUrl && (
            <img src={product.imageUrl} alt={product.title} className="game-cover" />
          )}
          <div className="game-header-info">
            <h1>{product.title}</h1>
            <p className="game-description">{product.description}</p>
          </div>
        </div>

        <div className="reviews-section">
          <h2>User Reviews ({comments.length})</h2>

          {canComment ? (
            <form className="comment-form" onSubmit={handleSubmitComment}>
              <p className="comment-form-label">
                Commenting as <strong>{currentUsername}</strong>
              </p>
              <textarea
                className="comment-textarea"
                placeholder="Write your review..."
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                rows={3}
                required
              />
              <button type="submit" className="comment-submit-btn" disabled={submitting}>
                {submitting ? 'Posting...' : 'Post Review'}
              </button>
            </form>
          ) : (
            <p className="login-prompt">
              <strong>Log in</strong> to leave a review.
            </p>
          )}

          {comments.length === 0 ? (
            <p className="no-reviews">No reviews yet. Be the first to review!</p>
          ) : (
            <div className="reviews-list">
              {comments.map((comment) => {
                const isOwner = currentUserId !== null && Number(comment.creatorUserId) === Number(currentUserId);
                return (
                  <div key={comment.id} className={`review-card ${isOwner ? 'own-review' : ''}`}>
                    <div className="review-header">
                      <div className="reviewer-info">
                        <div className="reviewer-avatar">
                          {(comment.creatorUsername || 'S').charAt(0).toUpperCase()}
                        </div>
                        <div className="reviewer-details">
                          <span className="reviewer-name">
                            {comment.creatorUsername || 'System'}
                            {isOwner && <span className="you-badge"> (You)</span>}
                          </span>
                          <span className="review-date">{formatDate(comment.creationDate)}</span>
                        </div>
                      </div>
                      {isOwner && (
                        <button
                          className="delete-comment-btn"
                          onClick={() => handleDeleteComment(comment.id)}
                          title="Delete your comment"
                        >
                          Delete
                        </button>
                      )}
                    </div>
                    <p className="review-text">{comment.description}</p>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default GameDetails;
