import React, { useEffect, useState } from 'react';
import { gameService } from '../services/api';
import { Game } from '../types';
import GameDetails from './GameDetails';
import GameCard from './GameCard';
import SearchBar from './SearchBar';
import Pagination from './Pagination';
import AuthModal from './AuthModal';
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
  const [isAuthModalOpen, setIsAuthModalOpen] = useState<boolean>(false);
  const [username, setUsername] = useState<string | null>(localStorage.getItem('username'));

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

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setUsername(null);
  };

  const handleAuthSuccess = (_token: string, newUsername: string) => {
    setUsername(newUsername);
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
          <div className="auth-section">
            {username ? (
              <div className="user-info">
                <span>Welcome, {username}!</span>
                <button onClick={handleLogout} className="logout-button">Logout</button>
              </div>
            ) : (
              <button onClick={() => setIsAuthModalOpen(true)} className="login-button">Login / Register</button>
            )}
          </div>
        </div>
        <SearchBar
          searchQuery={searchQuery}
          setSearchQuery={setSearchQuery}
          onSearch={handleSearch}
          onShowPopular={fetchGames}
          limit={limit}
          setLimit={(newLimit) => {
            setLimit(newLimit);
            setOffset(0);
          }}
        />
      </header>

      <div className="all-games-section">
        <h2>📚 Games ({games.length})</h2>
        <div className="games-grid">
          {games.map((game) => (
            <GameCard
              key={game.id}
              game={game}
              onClick={setSelectedGameId}
            />
          ))}
        </div>

        <Pagination
          offset={offset}
          limit={limit}
          totalVisible={games.length}
          loading={loading}
          onPrevious={handleLoadPrevious}
          onNext={handleLoadMore}
        />
      </div>

      {selectedGameId && (
        <GameDetails
          productId={selectedGameId}
          onClose={() => setSelectedGameId(null)}
        />
      )}

      <AuthModal
        isOpen={isAuthModalOpen}
        onClose={() => setIsAuthModalOpen(false)}
        onSuccess={handleAuthSuccess}
      />
    </div>
  );
};

export default GameList;
