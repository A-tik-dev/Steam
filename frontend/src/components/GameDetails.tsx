import React, { useCallback, useEffect, useState } from 'react';
import { AUTH_CHANGED_EVENT, authService, commentService, favoriteService, productService } from '../services/api';
import { Comment, ProductWithComments } from '../types';
import './GameDetails.css';

interface GameDetailsProps {
  productId: number;
  onClose: () => void;
}

// EN: Details modal; loads one saved product, its reviews, current user, and favorite state.
// RU: Модальное окно деталей; загружает один сохранённый продукт, отзывы, текущего пользователя и избранное.
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
  const [isFavorite, setIsFavorite] = useState<boolean>(false);
  const [favoriteLoading, setFavoriteLoading] = useState<boolean>(false);

  // EN: Converts the IGDB id selected in the catalog into the local product + comments payload.
  // RU: Превращает выбранный в каталоге IGDB id в локальный продукт с комментариями.
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

  // EN: Reads the current authenticated user so the UI can show comment/favorite actions safely.
  // RU: Получает текущего авторизованного пользователя, чтобы безопасно показывать действия с отзывами и избранным.
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

  // EN: Favorite status is checked against the local product id, not the external IGDB id.
  // RU: Статус избранного проверяется по локальному id продукта, а не по внешнему IGDB id.
  const fetchFavoriteStatus = useCallback(async (productRecordId: number) => {
    if (!localStorage.getItem('token')) {
      setIsFavorite(false);
      return;
    }

    const status = await favoriteService.getStatus(productRecordId);
    setIsFavorite(Boolean(status?.favorite));
  }, []);

  useEffect(() => {
    fetchGameDetails();
    fetchCurrentUser();
  }, [fetchGameDetails, fetchCurrentUser]);

  useEffect(() => {
    const productRecordId = data?.product.id;
    if (productRecordId) {
      fetchFavoriteStatus(productRecordId);
    }
  }, [data?.product.id, fetchFavoriteStatus, currentUserId]);

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

  // EN: Keeps review dates readable without sending formatting rules from the backend.
  // RU: Делает даты отзывов читаемыми без передачи правил форматирования с бэка.
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

  // EN: Posts a review as the current user, then appends the created DTO to local state.
  // RU: Публикует отзыв от текущего пользователя и добавляет созданный DTO в локальное состояние.
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

  // EN: Deletes only comments owned by the current user; backend enforces the same rule.
  // RU: Удаляет только отзывы текущего пользователя; бэкенд проверяет то же правило.
  const handleDeleteComment = async (commentId: number) => {
    if (!window.confirm('Delete your comment?')) return;

    try {
      await commentService.deleteComment(commentId);
      setComments(prev => prev.filter(comment => comment.id !== commentId));
    } catch {
      alert('Failed to delete comment.');
    }
  };

  // EN: Toggles favorite state optimistically after the backend confirms the change.
  // RU: Переключает избранное после подтверждения изменения бэкендом.
  const handleToggleFavorite = async () => {
    const productRecordId = data?.product.id;
    if (!productRecordId || !canComment) return;

    setFavoriteLoading(true);
    try {
      const status = isFavorite
        ? await favoriteService.removeFavorite(productRecordId)
        : await favoriteService.addFavorite(productRecordId);
      setIsFavorite(status.favorite);
    } catch {
      alert('Failed to update favorites. Make sure you are logged in.');
    } finally {
      setFavoriteLoading(false);
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
            {canComment && (
              <button
                className={`favorite-toggle ${isFavorite ? 'active' : ''}`}
                onClick={handleToggleFavorite}
                disabled={favoriteLoading}
              >
                {favoriteLoading ? 'Saving...' : isFavorite ? 'Remove Favorite' : 'Add Favorite'}
              </button>
            )}
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
