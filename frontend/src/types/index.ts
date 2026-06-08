// EN: Game DTO used by frontend screens. Mirrors fields returned by backend /api/games.* endpoints.
// RU: DTO игры для экранов фронтенда. Повторяет поля, которые отдает backend через /api/games.*.
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
    name: string;
  }>;
}

// EN: One user comment/review attached to a local product record.
// RU: Один пользовательский комментарий/отзыв, связанный с локальной записью продукта.
export interface Comment {
  id: number;
  description: string;
  creationDate: string;
  isDeleted: boolean;
  creatorUserId: number;
  creatorUsername: string;
}

// EN: Locally persisted product entity created from IGDB data.
// RU: Локально сохраненная сущность продукта, созданная из данных IGDB.
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

// EN: Aggregated response used by details modal: product payload + comments list.
// RU: Агрегированный ответ для модалки деталей: данные продукта + список комментариев.
export interface ProductWithComments {
  product: Product;
  comments: Comment[];
}
