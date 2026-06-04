-- Skrypt do weryfikacji struktury bazy danych
-- Uruchom po starcie aplikacji: mysql -u root -p gamecatalog < verify_database.sql

USE gamecatalog;

-- Sprawdź wszystkie tabele
SHOW TABLES;

-- Struktura tabeli product
DESCRIBE product;

-- Struktura tabeli comment
DESCRIBE comment;

-- Struktura tabeli category
DESCRIBE category;

-- Struktura tabeli users
DESCRIBE users;

-- Struktura tabeli product_category (tabela łącząca)
DESCRIBE product_category;

-- Sprawdź dane w tabelach
SELECT 'Products count:' as info, COUNT(*) as count FROM product;
SELECT 'Comments count:' as info, COUNT(*) as count FROM comment;
SELECT 'Categories count:' as info, COUNT(*) as count FROM category;
SELECT 'Users count:' as info, COUNT(*) as count FROM users;

-- Przykładowe dane z product
SELECT id, title, description, is_deleted, creation_date, creator_user_id, image_url
FROM product
LIMIT 5;

-- Przykładowe dane z comment
SELECT id, description, creation_date, is_deleted, creator_user_id, product_id
FROM comment
LIMIT 5;

-- Przykładowe dane z category
SELECT id, name, is_deleted
FROM category
LIMIT 5;
