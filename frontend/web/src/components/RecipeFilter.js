import React from 'react';

const RecipeFilter = ({ searchInput, setSearchInput, setCurrentPage }) => {
  const handleSearchChange = (event) => {
    setSearchInput(event.target.value);
    setCurrentPage(1);
  };

  return (
    <div className="filter-field">
      <div className="search-bar">
        <div className="search-field">
          <input
            type="text"
            placeholder="Введите название"
            value={searchInput}
            onChange={handleSearchChange}
          />
          <button className="dark-input-button">Найти</button>
        </div>
        <button className="dark-button">Обновить рекоммендации</button>
      </div>
    </div>
  );
};

export default RecipeFilter;
