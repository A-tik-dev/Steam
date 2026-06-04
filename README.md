# Game Catalog Application

Full-stack Video Game Catalog application with IGDB integration.

## Stack
- **Backend**: Java Spring Boot, MySQL
- **Frontend**: React with TypeScript, Axios
- **API Integration**: IGDB API (via Twitch OAuth2)

## Features
- Browse games with pagination and filtering
- Server-side proxy for IGDB API (solves CORS)
- JWT-based authentication
- Soft delete for all entities
- Admin and User roles

## Setup Instructions

### Backend Setup

1. **Install MySQL** and create a database:
```sql
CREATE DATABASE gamecatalog;
```

2. **Configure application.properties**:
   - Update MySQL credentials if needed (default: root/root)
   - IGDB credentials are already configured

3. **Run the backend**:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Install dependencies**:
```bash
cd frontend
npm install
```

2. **Run the frontend**:
```bash
npm run dev
```

The frontend will start on `http://localhost:3000`

## How to Use

### 1. Fetch Games from IGDB

Make a POST request to fetch 20 popular games:
```bash
curl -X POST http://localhost:8080/api/igdb/fetch-popular
```

Or use the frontend to trigger this endpoint.

### 2. Browse Games

Visit `http://localhost:3000` to see the game catalog with:
- Grid view of all games
- Search by title and category
- Pagination

### 3. Authentication

Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

## API Endpoints

### Products
- `GET /api/products` - Get all products (with pagination)
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Soft delete product (Admin only)

### IGDB Integration
- `POST /api/igdb/fetch-popular` - Fetch 20 popular games from IGDB
- `GET /api/igdb/search?query={term}` - Search games on IGDB

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

## How the IGDB Proxy Works

The backend acts as a server-side proxy to avoid CORS issues:

1. **Frontend → Backend**: The React app calls our backend API (same origin, no CORS)
2. **Backend → Twitch**: Backend authenticates with Twitch OAuth2 to get access token
3. **Backend → IGDB**: Backend makes requests to IGDB API with proper headers
4. **Backend → Frontend**: Backend returns processed data to frontend

This solves CORS because:
- Browser CORS restrictions only apply to browser-to-server requests
- Server-to-server requests (backend to IGDB) have no CORS restrictions
- Frontend only communicates with our backend (same origin via Vite proxy)

## Database Schema

All entities implement soft delete with `isDeleted` boolean flag:

- **Product**: Games with title, description, image, categories
- **Category**: Game genres/categories
- **Comment**: User comments on games
- **User**: Authentication with ADMIN/USER roles

## Technologies Used

### Backend
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- MySQL 8
- JWT (jjwt 0.12.3)
- Jackson for JSON

### Frontend
- React 18
- TypeScript
- Axios
- Vite
- CSS3
