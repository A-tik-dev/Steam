/// <reference types="vite/client" />

// EN: Declares CSS modules for TS compiler so importing .css files does not raise type errors.
// RU: Объявляет CSS-модули для TS-компилятора, чтобы импорт .css файлов не вызывал ошибок типов.
declare module '*.css';
