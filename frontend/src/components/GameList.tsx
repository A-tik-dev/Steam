import React, { useEffect, useMemo, useState } from 'react';
import { AUTH_CHANGED_EVENT, authService, gameService } from '../services/api';
import { Game, Genre } from '../types';
import GameDetails from './GameDetails';
import GameCard from './GameCard';
import SearchBar from './SearchBar';
import Pagination from './Pagination';
import AuthModal from './AuthModal';
import ProfilePanel from './ProfilePanel';
import CategoryFilter from './CategoryFilter';
import './GameList.css';

// EN: Main catalog screen; owns game loading, search, pagination, auth session, and open modals.
// RU: Главный экран каталога; отвечает за загрузку игр, поиск, пагинацию, сессию и открытые модальные окна.
const GameList: React.FC = () => {
  const [games, setGames] = useState<Game[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [genres, setGenres] = useState<Genre[]>([]);
  const [selectedGenreId, setSelectedGenreId] = useState<string>('');
  const [isSearchMode, setIsSearchMode] = useState<boolean>(false);
  const [limit, setLimit] = useState<number>(50);
  const [offset, setOffset] = useState<number>(0);
  const [selectedGameId, setSelectedGameId] = useState<number | null>(null);
  const [isAuthModalOpen, setIsAuthModalOpen] = useState<boolean>(false);
  const [isProfileOpen, setIsProfileOpen] = useState<boolean>(false);
  const [username, setUsername] = useState<string | null>(localStorage.getItem('username'));
  const [authChecking, setAuthChecking] = useState<boolean>(true);

  // EN: Loads the current page: popular games or a real backend page for the selected genre.
  // RU: Загружает текущую страницу: популярные игры или настоящую backend-страницу выбранного жанра.
  const fetchGames = async () => {
    try {
      setLoading(true);
      const data = selectedGenreId
        ? await gameService.getGamesByGenre(Number(selectedGenreId), limit, offset)
        : await gameService.getPopularGames(limit, offset);
      setGames(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch games');
    } finally {
      setLoading(false);
    }
  };

  // EN: Runs search when the query is filled; falls back to popular games for an empty query.
  // RU: Запускает поиск при заполненном запросе; при пустом запросе возвращает популярные игры.
  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      setIsSearchMode(false);
      fetchGames();
      return;
    }

    try {
      setLoading(true);
      setIsSearchMode(true);
      setSelectedGenreId('');
      setOffset(0);
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
    if (!isSearchMode) {
      fetchGames();
    }
  }, [limit, offset, selectedGenreId, isSearchMode]);

  // EN: Loads the complete genre list once; genre selection uses these ids for backend paging.
  // RU: Один раз загружает полный список жанров; выбор жанра использует эти id для backend-пагинации.
  useEffect(() => {
    const fetchGenres = async () => {
      try {
        setGenres(await gameService.getGenres());
      } catch {
        setGenres([]);
      }
    };

    fetchGenres();
  }, []);

  // EN: Verifies saved JWT on first render and keeps UI in sync with login/logout events.
  // RU: Проверяет сохранённый JWT при первом рендере и синхронизирует UI с событиями входа/выхода.
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

  // EN: Clears local auth state and notifies components that depend on the current user.
  // RU: Очищает локальное состояние авторизации и уведомляет компоненты, зависящие от текущего пользователя.
  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setUsername(null);
    setIsProfileOpen(false);
    window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
  };

  const handleAuthSuccess = (_token: string, newUsername: string) => {
    setUsername(newUsername);
    window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
  };

  // EN: Resets search and genre filters, then returns the catalog to the first popular page.
  // RU: Сбрасывает поиск и жанр, затем возвращает каталог на первую страницу популярных игр.
  const handleShowPopular = () => {
    setSearchQuery('');
    setSelectedGenreId('');
    setIsSearchMode(false);

    if (offset === 0 && !selectedGenreId) {
      fetchGames();
      return;
    }

    setOffset(0);
  };

  // EN: Switches to backend genre mode and starts from the first category page.
  // RU: Переключает каталог в backend-режим жанра и начинает с первой страницы категории.
  const handleGenreChange = (genreId: string) => {
    setSearchQuery('');
    setIsSearchMode(false);
    setSelectedGenreId(genreId);
    setOffset(0);
  };

  const selectedGenreName = useMemo(
    () => genres.find(genre => String(genre.id) === selectedGenreId)?.name,
    [genres, selectedGenreId]
  );

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
                <button onClick={() => setIsProfileOpen(true)} className="profile-button">Profile</button>
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
          onShowPopular={handleShowPopular}
          limit={limit}
          setLimit={(newLimit) => {
            setLimit(newLimit);
            setOffset(0);
          }}
        />
        <CategoryFilter
          genres={genres}
          selectedGenreId={selectedGenreId}
          onGenreChange={handleGenreChange}
        />
      </header>

      <div className="all-games-section">
        <div className="section-heading">
          <h2>
            {selectedGenreName ? `${selectedGenreName} Games` : 'Games'} ({games.length})
          </h2>
          {selectedGenreId && (
            <button className="clear-filter-button" onClick={() => handleGenreChange('')}>
              Clear genre
            </button>
          )}
        </div>
        <div className="games-grid">
          {games.map((game) => (
            <GameCard
              key={game.id}
              game={game}
              onClick={setSelectedGameId}
            />
          ))}
        </div>
        {games.length === 0 && (
          <p className="empty-filter-state">No games found for this page.</p>
        )}

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
      <ProfilePanel
        isOpen={isProfileOpen}
        onClose={() => setIsProfileOpen(false)}
      />
    </div>
  );
};

export default GameList;
