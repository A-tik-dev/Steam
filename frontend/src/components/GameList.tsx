import React, { useEffect, useState } from 'react';
import { AUTH_CHANGED_EVENT, authService, gameService } from '../services/api';
import { Game } from '../types';
import GameDetails from './GameDetails';
import GameCard from './GameCard';
import SearchBar from './SearchBar';
import Pagination from './Pagination';
import AuthModal from './AuthModal';
import './GameList.css';

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
  const [authChecking, setAuthChecking] = useState<boolean>(true);

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

  useEffect(() => {
    fetchGames();
  }, [limit, offset]);

  useEffect(() => {
    const syncCurrentUser = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        setUsername(null);
        setAuthChecking(false);
        return;
      }

      const user = await authService.getCurrentUser();
      if (user) {
        localStorage.setItem('username', user.username);
        setUsername(user.username);
      } else {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        setUsername(null);
      }
      setAuthChecking(false);
    };

    const handleAuthChange = () => {
      const token = localStorage.getItem('token');
      setUsername(token ? localStorage.getItem('username') : null);
    };

    syncCurrentUser();
    window.addEventListener(AUTH_CHANGED_EVENT, handleAuthChange);
    window.addEventListener('storage', handleAuthChange);

    return () => {
      window.removeEventListener(AUTH_CHANGED_EVENT, handleAuthChange);
      window.removeEventListener('storage', handleAuthChange);
    };
  }, []);

  const handleLoadMore = () => {
    setOffset(offset + limit);
  };

  const handleLoadPrevious = () => {
    setOffset(Math.max(0, offset - limit));
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setUsername(null);
    window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
  };

  const handleAuthSuccess = (_token: string, newUsername: string) => {
    setUsername(newUsername);
    window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
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
          <div className="header-title">
            <span className="header-kicker">IGDB Catalog</span>
            <h1>Popular Games</h1>
          </div>
          <div className="auth-section">
            {authChecking ? (
              <span className="auth-status">Checking session...</span>
            ) : username ? (
              <div className="user-info">
                <span>Signed in as <strong>{username}</strong></span>
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
        <h2>Games ({games.length})</h2>
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
