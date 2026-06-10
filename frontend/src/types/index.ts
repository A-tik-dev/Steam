// EN: Shared frontend DTOs matching backend responses and IGDB proxy payloads.
// RU: Общие frontend DTO, соответствующие ответам backend и payload от IGDB-прокси.

// EN: Game data received from the backend IGDB proxy for catalog cards.
// RU: Данные игры из backend-прокси IGDB для карточек каталога.
export interface Game {
  id: number;
  name: string;
  summary: string;
  cover?: {
    imageId: string;
    url: string;
  };
  rating?: number;
  releaseDates?: Array<{
    date: number;
  }>;
  genres?: Array<{
    id: number;
    name: string;
  }>;
}

// EN: Genre/category item loaded from backend IGDB endpoint.
// RU: Жанр/категория, загруженная из backend endpoint IGDB.
export interface Genre {
  id: number;
  name: string;
}

// EN: Review/comment data shown in the game details modal.
// RU: Данные отзыва/комментария для модального окна деталей игры.
export interface Comment {
  id: number;
  description: string;
  creationDate: string;
  isDeleted: boolean;
  creatorUserId: number;
  creatorUsername: string;
}

// EN: Locally saved product record returned by product/profile endpoints.
// RU: Локально сохранённая запись продукта из endpoints продукта/профиля.
export interface Product {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  categories?: string[];
  creationDate: string;
  isDeleted: boolean;
  creatorUserId: number;
}

// EN: Combined details response: local product plus all active comments.
// RU: Объединённый ответ деталей: локальный продукт и все активные комментарии.
export interface ProductWithComments {
  product: Product;
  comments: Comment[];
}

// EN: Current logged-in user returned by /auth/me.
// RU: Текущий залогиненный пользователь из /auth/me.
export interface CurrentUser {
  id: number;
  username: string;
  role: string;
}

// EN: Favorite state for one local product.
// RU: Состояние избранного для одного локального продукта.
export interface FavoriteStatus {
  productId: number;
  favorite: boolean;
}

// EN: Comment row displayed in the profile panel history.
// RU: Строка комментария, которая показывается в истории профиля.
export interface ProfileComment {
  commentId: number;
  productId: number;
  productTitle: string;
  productImageUrl: string;
  description: string;
  creationDate: string;
}

// EN: Full profile panel data loaded after login.
// RU: Полные данные панели профиля, загружаемые после входа.
export interface UserProfile {
  user: CurrentUser;
  comments: ProfileComment[];
  favoriteGames: Product[];
}
