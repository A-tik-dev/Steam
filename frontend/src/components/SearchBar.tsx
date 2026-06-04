import React from 'react';

interface SearchBarProps {
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  onSearch: () => void;
  onShowPopular: () => void;
  limit: number;
  setLimit: (limit: number) => void;
}

const SearchBar: React.FC<SearchBarProps> = ({
  searchQuery,
  setSearchQuery,
  onSearch,
  onShowPopular,
  limit,
  setLimit,
}) => {
  return (
    <div className="search-bar">
      <input
        type="text"
        placeholder="Search games..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        onKeyPress={(e) => e.key === 'Enter' && onSearch()}
      />
      <button onClick={onSearch}>Search</button>
      <button onClick={onShowPopular}>Show Popular</button>
      <select
        value={limit}
        onChange={(e) => setLimit(Number(e.target.value))}
        className="limit-select"
      >
        <option value="20">20 games</option>
        <option value="50">50 games</option>
        <option value="100">100 games</option>
        <option value="200">200 games</option>
      </select>
    </div>
  );
};

export default SearchBar;
