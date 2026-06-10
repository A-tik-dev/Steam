import React from 'react';
import { Genre } from '../types';

interface CategoryFilterProps {
  genres: Genre[];
  selectedGenreId: string;
  onGenreChange: (genreId: string) => void;
}

// EN: Genre dropdown that asks the parent to load a real category page.
// RU: Выпадающий список жанров, который просит родителя загрузить настоящую страницу категории.
const CategoryFilter: React.FC<CategoryFilterProps> = ({ genres, selectedGenreId, onGenreChange }) => {
  return (
    <label className="category-filter">
      <span>Genre</span>
      <select
        value={selectedGenreId}
        onChange={(event) => onGenreChange(event.target.value)}
        disabled={genres.length === 0}
      >
        <option value="">All genres</option>
        {genres.map((genre) => (
          <option key={genre.id} value={genre.id}>
            {genre.name}
          </option>
        ))}
      </select>
    </label>
  );
};

export default CategoryFilter;
