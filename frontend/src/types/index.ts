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

export interface Comment {
  id: number;
  description: string;
  creationDate: string;
  isDeleted: boolean;
  creatorUserId: number;
  creatorUsername: string;
}

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

export interface ProductWithComments {
  product: Product;
  comments: Comment[];
}

export interface CurrentUser {
  id: number;
  username: string;
  role: string;
}

export interface FavoriteStatus {
  productId: number;
  favorite: boolean;
}

export interface ProfileComment {
  commentId: number;
  productId: number;
  productTitle: string;
  productImageUrl: string;
  description: string;
  creationDate: string;
}

export interface UserProfile {
  user: CurrentUser;
  comments: ProfileComment[];
  favoriteGames: Product[];
}
