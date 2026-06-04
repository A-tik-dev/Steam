import GameList from './components/GameList';
import './App.css';

// EN: Root shell of the single-page app. Currently delegates main content to GameList.
// RU: Корневой shell одностраничного приложения. Сейчас делегирует основной контент в GameList.
function App() {
  return (
    <div className="App">
      <GameList />
    </div>
  );
}

export default App;
