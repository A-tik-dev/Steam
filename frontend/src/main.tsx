import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

// EN: Frontend application entry point. Mounts React root into #root element from index.html.
// RU: Точка входа фронтенд-приложения. Монтирует React-корень в элемент #root из index.html.
ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
