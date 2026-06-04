import React, { useState, useEffect } from 'react';
import { Product } from '../types';
import { commentService } from '../services/api';
import './GameModal.css';

// EN: Local comment shape used by this legacy modal path.
// RU: Локальная форма комментария для этого legacy-сценария модалки.
interface Comment {
  id: number;
  description: string;
  creationDate: string;
  creatorUserId: number;
}

interface GameModalProps {
  game: Product | null;
  isOpen: boolean;
  onClose: () => void;
  currentUser: string | null;
}

// EN: Legacy alternative modal with inline comment posting flow.
// RU: Legacy-альтернатива модалки со встроенным сценарием публикации комментариев.
const GameModal: React.FC<GameModalProps> = ({ game, isOpen, onClose, currentUser }) => {
  const [comments, setComments] = useState<Comment[]>([]);
  const [newComment, setNewComment] = useState('');
  const [loading, setLoading] = useState(false);

  // EN: Reloads comments each time modal becomes visible for current game.
  // RU: Перезагружает комментарии каждый раз, когда модалка открывается для текущей игры.
  useEffect(() => {
    if (game && isOpen) {
      loadComments();
    }
  }, [game, isOpen]);

  // EN: Fetches latest comments from backend and stores them in local component state.
  // RU: Получает актуальные комментарии с backend и сохраняет их в локальное состояние компонента.
  const loadComments = async () => {
    if (!game) return;
    try {
      const data = await commentService.getCommentsByProductId(game.id);
      setComments(data);
    } catch (err) {
      console.error('Failed to load comments:', err);
    }
  };

  // EN: Sends new comment, clears textarea, then refreshes comment list.
  // RU: Отправляет новый комментарий, очищает поле ввода и затем обновляет список комментариев.
  const handleAddComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newComment.trim() || !game) return;

    setLoading(true);
    try {
      await commentService.createComment(game.id, newComment, null);
      setNewComment('');
      loadComments();
    } catch (err) {
      alert('Failed to add comment');
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen || !game) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="game-modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>×</button>

        <div className="game-modal-header">
          <div className="game-modal-image">
            {game.imageUrl ? (
              <img src={game.imageUrl} alt={game.title} />
            ) : (
              <div className="no-image">No Image</div>
            )}
          </div>
          <div className="game-modal-info">
            <h2>{game.title}</h2>
            <div className="game-categories">
              {game.categories?.map((category: string, index: number) => (
                <span key={index} className="category-tag">
                  {category}
                </span>
              ))}
            </div>
            <p className="game-description">{game.description}</p>
          </div>
        </div>

        <div className="comments-section">
          <h3>💬 Comments ({comments.length})</h3>

          {currentUser ? (
            <form onSubmit={handleAddComment} className="comment-form">
              <textarea
                placeholder="Write a comment..."
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                rows={3}
              />
              <button type="submit" disabled={loading || !newComment.trim()}>
                {loading ? 'Posting...' : 'Post Comment'}
              </button>
            </form>
          ) : (
            <p className="login-prompt">Please login to leave a comment</p>
          )}

          <div className="comments-list">
            {comments.length === 0 ? (
              <p className="no-comments">No comments yet. Be the first to comment!</p>
            ) : (
              comments.map((comment) => (
                <div key={comment.id} className="comment-item">
                  <div className="comment-header">
                    <span className="comment-author">User #{comment.creatorUserId || 'Anonymous'}</span>
                    <span className="comment-date">
                      {new Date(comment.creationDate).toLocaleDateString()}
                    </span>
                  </div>
                  <p className="comment-text">{comment.description}</p>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default GameModal;
