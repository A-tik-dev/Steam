import React, { useEffect, useState } from 'react';
import { productService } from '../services/api';
import { ProductWithComments } from '../types';
import './GameDetails.css';

// EN: Input contract for details modal.
// RU: Входной контракт для модального окна деталей.
interface GameDetailsProps {
  productId: number;
  onClose: () => void;
}

// EN: Modal that shows local product details and related comments.
// RU: Модалка, показывающая локальные детали продукта и связанные комментарии.
const GameDetails: React.FC<GameDetailsProps> = ({ productId, onClose }) => {
  const [data, setData] = useState<ProductWithComments | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // EN: Loads aggregate backend payload: product metadata + comments list.
  // RU: Загружает агрегированный payload backend: метаданные продукта + список комментариев.
  useEffect(() => {
    const fetchGameDetails = async () => {
      try {
        setLoading(true);
        const result = await productService.getProductWithComments(productId);
        setData(result);
        setError(null);
      } catch (err: any) {
        setError(err.message || 'Failed to fetch game details');
      } finally {
        setLoading(false);
      }
    };

    fetchGameDetails();
  }, [productId]);

  // EN: Converts ISO date into a user-friendly relative label (Today, 2 weeks ago, etc.).
  // RU: Преобразует ISO-дату в удобную относительную метку (Сегодня, 2 недели назад и т.д.).
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

  // EN: Temporary visual rating used only for UI presentation, not persisted in backend.
  // RU: Временный визуальный рейтинг только для отображения в UI, в backend не сохраняется.
  const getRandomRating = () => {
    return (7 + Math.random() * 3).toFixed(1);
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

  const { product, comments } = data;

  return (
    <div className="game-details-overlay" onClick={onClose}>
      <div className="game-details-modal" onClick={(e) => e.stopPropagation()}>
        <button onClick={onClose} className="close-button-top">✕</button>

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

          {comments.length === 0 ? (
            <p className="no-reviews">No reviews yet. Be the first to review!</p>
          ) : (
            <div className="reviews-list">
              {comments.map((comment) => (
                <div key={comment.id} className="review-card">
                  <div className="review-header">
                    <div className="reviewer-info">
                      <div className="reviewer-avatar">
                        {String.fromCharCode(65 + Math.floor(Math.random() * 26))}
                      </div>
                      <div className="reviewer-details">
                        <span className="reviewer-name">Gamer{comment.creatorUserId}</span>
                        <span className="review-date">{formatDate(comment.creationDate)}</span>
                      </div>
                    </div>
                    <div className="review-rating">
                      <span className="rating-number">{getRandomRating()}</span>
                      <span className="rating-max">/10</span>
                    </div>
                  </div>
                  <p className="review-text">{comment.description}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default GameDetails;
