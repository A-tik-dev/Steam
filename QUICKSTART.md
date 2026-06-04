# Game Catalog - Quick Start Guide

## After MySQL Installation

### 1. Start MySQL Service
```bash
net start MySQL80
```

### 2. Create Database
```bash
mysql -u root -p
# Enter password: root
CREATE DATABASE gamecatalog;
EXIT;
```

### 3. Run the Application

**Option A: Use the start script**
```bash
start.bat
```

**Option B: Manual start**

Terminal 1 - Backend:
```bash
cd backend
java -jar target/game-catalog-1.0.0.jar
```

Terminal 2 - Frontend:
```bash
cd frontend
npm run dev
```

### 4. Fetch Games from IGDB

```bash
curl -X POST http://localhost:8080/api/igdb/fetch-popular
```

Or use PowerShell:
```powershell
Invoke-WebRequest -Uri http://localhost:8080/api/igdb/fetch-popular -Method POST
```

### 5. Access the Application

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/products

## Troubleshooting

If MySQL password is different from "root", update:
`backend/src/main/resources/application.properties`

```properties
spring.datasource.password=YOUR_PASSWORD
```

Then rebuild:
```bash
cd backend
./mvnw clean package -DskipTests
```
