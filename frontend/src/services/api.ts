import axios from 'axios';
import { CurrentUser, FavoriteStatus, Game, Genre, ProductWithComments, UserProfile } from '../types';

interface AuthResponse {
  token: string;
  username: string;
}

export const AUTH_CHANGED_EVENT = 'auth-changed';

// EN: Removes stale auth data and broadcasts a local event for components in the same tab.
// RU: Удаляет устаревшие данные авторизации и рассылает локальное событие компонентам в этой вкладке.
const clearStoredAuth = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
};

// EN: Central API base URL for all frontend requests to the Spring backend.
// RU: Базовый URL API для всех запросов фронтенда к Spring backend.
const API_BASE_URL = 'http://localhost:8080/api';

// EN: Shared axios client keeps common config (base URL + JSON headers) in one place.
// RU: Общий axios-клиент хранит общую конфигурацию (base URL + JSON-заголовки) в одном месте.
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// EN: Clears stored token if the server returns 401 (token expired or invalid).
// RU: Очищает сохранённый токен если сервер вернул 401 (токен истёк или невалиден).
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearStoredAuth();
    }
    return Promise.reject(error);
  }
);

// EN: Game discovery API group. These endpoints proxy IGDB data through our backend.
// RU: Группа API для поиска игр. Эти endpoints отдают данные IGDB через наш backend.
export const gameService = {
  // EN: Loads a page of popular games; used by the main catalog screen.
  // RU: Загружает страницу популярных игр; используется на главном экране каталога.
  getPopularGames: async (limit: number = 50, offset: number = 0): Promise<Game[]> => {
    const response = await api.get<Game[]>('/games/popular', {
      params: { limit, offset }
    });
    return response.data;
  },

  // EN: Performs server-side search by text query and returns matched games.
  // RU: Выполняет серверный поиск по текстовому запросу и возвращает найденные игры.
  searchGames: async (query: string, limit: number = 20): Promise<Game[]> => {
    const response = await api.get<Game[]>('/games/search', {
      params: { query, limit }
    });
    return response.data;
  },

  // EN: Loads all IGDB genres for the category selector.
  // RU: Загружает все жанры IGDB для селектора категорий.
  getGenres: async (): Promise<Genre[]> => {
    const response = await api.get<Genre[]>('/games/genres');
    return response.data;
  },

  // EN: Loads a real backend page of games for one selected genre.
  // RU: Загружает настоящую backend-страницу игр для выбранного жанра.
  getGamesByGenre: async (genreId: number, limit: number = 50, offset: number = 0): Promise<Game[]> => {
    const response = await api.get<Game[]>(`/games/genre/${genreId}`, {
      params: { limit, offset }
    });
    return response.data;
  },
};

// EN: Product API group. Product is a locally persisted mirror of an IGDB game.
// RU: Группа API продуктов. Продукт - это локально сохраненное зеркало игры из IGDB.
export const productService = {
  // EN: Returns combined payload with product info and its comments in one request.
  // RU: Возвращает объединенный payload с данными продукта и его комментариями одним запросом.
  getProductWithComments: async (id: number): Promise<ProductWithComments> => {
    const response = await api.get<ProductWithComments>(`/products/${id}`);
    return response.data;
  },
};

// EN: Comment API group used by details/modals for reading and posting reviews.
// RU: Группа API комментариев для экрана деталей/модалок: чтение и публикация отзывов.
export const commentService = {
  // EN: Reads all active comments for a product (returns DTO with username).
  // RU: Получает все активные комментарии для продукта (с именем пользователя).
  getCommentsByProductId: async (productId: number) => {
    const response = await api.get(`/products/${productId}/comments`);
    return response.data;
  },

  // EN: Creates a new comment for a product; authentication is required.
  // RU: Создает новый комментарий к продукту; требуется авторизация.
  createComment: async (productId: number, description: string) => {
    const response = await api.post(`/products/${productId}/comments`, { description });
    return response.data;
  },

  // EN: Soft-deletes the comment; only the owner can delete their own comment.
  // RU: Мягко удаляет комментарий; удалить может только владелец.
  deleteComment: async (commentId: number) => {
    await api.delete(`/products/comments/${commentId}`);
  },
};

export const favoriteService = {
  // EN: Reads whether the current user already added this product to favorites.
  // RU: Проверяет, добавил ли текущий пользователь этот продукт в избранное.
  getStatus: async (productId: number): Promise<FavoriteStatus | null> => {
    try {
      const response = await api.get<FavoriteStatus>(`/products/${productId}/favorite`);
      return response.data;
    } catch {
      return null;
    }
  },

  // EN: Adds the product to the current user's favorites.
  // RU: Добавляет продукт в избранное текущего пользователя.
  addFavorite: async (productId: number): Promise<FavoriteStatus> => {
    const response = await api.post<FavoriteStatus>(`/products/${productId}/favorite`);
    return response.data;
  },

  // EN: Removes the product from the current user's favorites.
  // RU: Убирает продукт из избранного текущего пользователя.
  removeFavorite: async (productId: number): Promise<FavoriteStatus> => {
    const response = await api.delete<FavoriteStatus>(`/products/${productId}/favorite`);
    return response.data;
  },
};

// EN: Profile API group used by the side panel after the user is authenticated.
// RU: Группа API профиля, используется боковой панелью после авторизации пользователя.
export const profileService = {
  // EN: Returns current user info together with their comments and favorite games.
  // RU: Возвращает данные текущего пользователя вместе с его отзывами и избранными играми.
  getMyProfile: async (): Promise<UserProfile> => {
    const response = await api.get<UserProfile>('/profile/me');
    return response.data;
  },
};

// EN: Authentication API group for login/registration flows in AuthModal.
// RU: Группа API авторизации для сценариев входа/регистрации в AuthModal.
export const authService = {
  // EN: Authenticates existing user and returns JWT + username payload.
  // RU: Аутентифицирует существующего пользователя и возвращает JWT + username.
  login: async (username: string, password: string): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', { username: username.trim(), password });
    return response.data;
  },

  // EN: Registers new user and returns auth payload expected by UI.
  // RU: Регистрирует нового пользователя и возвращает auth-payload, ожидаемый UI.
  register: async (username: string, password: string): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register', { username: username.trim(), password });
    return response.data;
  },

  // EN: Returns the current logged-in user's id and username from JWT context.
  // RU: Возвращает id и username текущего залогиненного пользователя из JWT контекста.
  getCurrentUser: async (): Promise<CurrentUser | null> => {
    try {
      const response = await api.get('/auth/me');
      return response.data;
    } catch {
      return null;
    }
  },
};

// EN: Export raw axios client for rare custom requests not covered by service groups.
// RU: Экспортирует сырой axios-клиент для редких кастомных запросов вне сервисных групп.
export default api;
