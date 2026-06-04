import React, { useEffect, useState } from 'react';
import { gameService } from '../services/api';
import { Game } from '../types';
import GameDetails from './GameDetails';
import './GameList.css';

// EN: Main catalog container. Controls loading, searching, pagination, and details modal opening.
// RU: Главный контейнер каталога. Управляет загрузкой, поиском, пагинацией и открытием модалки деталей.
const GameList: React.FC = () => {
  const [games, setGames] = useState<Game[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [limit, setLimit] = useState<number>(50);
  const [offset, setOffset] = useState<number>(0);
  const [selectedGameId, setSelectedGameId] = useState<number | null>(null);

  // EN: Loads one page of popular games from backend and updates UI state atomically.
  // RU: Загружает одну страницу популярных игр с backend и атомарно обновляет состояние UI.
  const fetchGames = async () => {
    try {
      setLoading(true);
      const data = await gameService.getPopularGames(limit, offset);
      setGames(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch games');
    } finally {
      setLoading(false);
    }
  };

  // EN: Executes server-side search; if query is empty, resets to default popular feed.
  // RU: Выполняет серверный поиск; если строка пустая, возвращает стандартную ленту популярных игр.
  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      fetchGames();
      return;
    }

    try {
      setLoading(true);
      const data = await gameService.searchGames(searchQuery, limit);
      setGames(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to search games');
    } finally {
      setLoading(false);
    }
  };

  // EN: Re-fetches list whenever page size or current offset changes.
  // RU: Перезагружает список при изменении размера страницы или текущего смещения.
  useEffect(() => {
    fetchGames();
  }, [limit, offset]);

  // EN: Moves cursor forward by current page size.
  // RU: Сдвигает курсор вперед на размер текущей страницы.
  const handleLoadMore = () => {
    setOffset(offset + limit);
  };

  // EN: Moves cursor backward but clamps value to zero to avoid negative offsets.
  // RU: Сдвигает курсор назад, но ограничивает значение нулем, чтобы не уйти в отрицательное смещение.
  const handleLoadPrevious = () => {
    setOffset(Math.max(0, offset - limit));
  };

  if (loading && games.length === 0) {
    return <div className="loading">Loading games...</div>;
  }

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  return (
    <div className="game-list-container">
      <header className="header">
        <div className="header-top">
          <h1>🎮 Popular Games from IGDB</h1>
        </div>
        <div className="search-bar">
          <input
            type="text"
            placeholder="Search games..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
          <button onClick={handleSearch}>Search</button>
          <button onClick={fetchGames}>Show Popular</button>
          <select
            value={limit}
            onChange={(e) => {
              setLimit(Number(e.target.value));
              setOffset(0);
            }}
            className="limit-select"
          >
            <option value="20">20 games</option>
            <option value="50">50 games</option>
            <option value="100">100 games</option>
            <option value="200">200 games</option>
          </select>
        </div>
      </header>

      <div className="all-games-section">
        <h2>📚 Games ({games.length})</h2>
        <div className="games-grid">
          {games.map((game) => (
            <div
              key={game.id}
              className="game-card"
              onClick={() => setSelectedGameId(game.id)}
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
                    <span className="rating">
                      ⭐ {game.rating.toFixed(1)}
                    </span>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>

        <div className="pagination">
          <button
            onClick={handleLoadPrevious}
            disabled={offset === 0 || loading}
          >
            Previous
          </button>
          <span className="page-info">
            Showing {offset + 1} - {offset + games.length}
          </span>
          <button
            onClick={handleLoadMore}
            disabled={loading || games.length < limit}
          >
            Next
          </button>
        </div>
      </div>

      {selectedGameId && (
        <GameDetails
          productId={selectedGameId}
          onClose={() => setSelectedGameId(null)}
        />
      )}
    </div>
  );
};

export default GameList;
