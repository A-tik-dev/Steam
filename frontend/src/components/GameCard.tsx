import React from 'react';
import { Game } from '../types';

interface GameCardProps {
  game: Game;
  onClick: (id: number) => void;
}

const GameCard: React.FC<GameCardProps> = ({ game, onClick }) => {
  return (
    <div
      className="game-card"
      onClick={() => onClick(game.id)}
      style={{ cursor: 'pointer' }}
    >
      <div className="game-image">
        {game.cover?.url ? (
          <img src={game.cover.url} alt={game.name} />
        ) : (
          <div className="no-image">No Image</div>
        )}
      </div>
      <div className="game-info">
        <h3 title={game.name}>{game.name}</h3>
        <p className="game-description">
          {game.summary?.substring(0, 100)}
          {game.summary && game.summary.length > 100 ? '...' : ''}
        </p>
        {game.genres && game.genres.length > 0 && (
          <div className="game-categories">
            {game.genres.slice(0, 3).map((genre, index) => (
              <span key={index} className="category-tag">
                {genre.name}
              </span>
            ))}
          </div>
        )}
        {game.rating && (
          <div className="game-footer">
            <span className="rating">⭐ {game.rating.toFixed(1)}</span>
          </div>
        )}
      </div>
    </div>
  );
};

export default GameCard;
