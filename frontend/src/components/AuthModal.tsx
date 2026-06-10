import React, { useEffect, useState } from 'react';
import { authService } from '../services/api';
import './AuthModal.css';

interface AuthModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: (token: string, username: string) => void;
}

// EN: Login/register modal; talks to authService and stores the returned JWT for future API calls.
// RU: Модальное окно входа/регистрации; обращается к authService и сохраняет JWT для следующих API-запросов.
const AuthModal: React.FC<AuthModalProps> = ({ isOpen, onClose, onSuccess }) => {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!isOpen) return;
    setUsername('');
    setPassword('');
    setError('');
    setLoading(false);
  }, [isOpen]);

  if (!isOpen) return null;

  // EN: Converts backend/API errors into short messages suitable for the form.
  // RU: Преобразует ошибки бэка/API в короткие сообщения для формы.
  const getAuthErrorMessage = (err: any) => {
    const status = err.response?.status;
    const responseData = err.response?.data;

    if (typeof responseData === 'string' && responseData.trim()) {
      return responseData;
    }

    if (responseData?.message) {
      return responseData.message;
    }

    if (status === 401 || status === 403) {
      return 'Wrong username or password.';
    }

    if (status === 409) {
      return 'This username is already taken.';
    }

    return isLogin ? 'Could not log in. Try again.' : 'Could not create account. Try again.';
  };

  // EN: Sends credentials to either login or register endpoint based on the current mode.
  // RU: Отправляет логин/пароль в endpoint входа или регистрации в зависимости от режима.
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    const trimmedUsername = username.trim();
    if (!trimmedUsername || !password) {
      setError('Enter username and password.');
      return;
    }

    setLoading(true);
    try {
      const response = isLogin
        ? await authService.login(trimmedUsername, password)
        : await authService.register(trimmedUsername, password);

      localStorage.setItem('token', response.token);
      localStorage.setItem('username', response.username);
      onSuccess(response.token, response.username);
      onClose();
    } catch (err: any) {
      setError(getAuthErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  // EN: Switches between login and account creation while keeping stale errors out of the UI.
  // RU: Переключает вход и создание аккаунта, убирая устаревшие ошибки из UI.
  const toggleMode = () => {
    setIsLogin(prev => !prev);
    setError('');
    setPassword('');
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose} aria-label="Close auth dialog">
          x
        </button>
        <h2>{isLogin ? 'Login' : 'Create Account'}</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            autoFocus
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete={isLogin ? 'current-password' : 'new-password'}
            required
          />
          {error && <div className="error-message">{error}</div>}
          <button type="submit" disabled={loading}>
            {loading ? 'Please wait...' : isLogin ? 'Login' : 'Create Account'}
          </button>
        </form>
        <p className="toggle-auth">
          {isLogin ? "Don't have an account? " : 'Already have an account? '}
          <button type="button" onClick={toggleMode}>
            {isLogin ? 'Create one' : 'Login'}
          </button>
        </p>
      </div>
    </div>
  );
};

export default AuthModal;
