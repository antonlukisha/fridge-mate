import React from 'react';

const Filter = ({ searchInput, setSearchInput, activeFilter, setActiveFilter, setCurrentPage, clearExpiryProducts }) => {
  const handleSearchChange = (event) => {
    setSearchInput(event.target.value);
    setCurrentPage(1);
  };

  const handleFilterChange = (filter) => {
    setActiveFilter(filter);
    setCurrentPage(1);
  };

  return (
    <div className="filter-field">
      <div className="tab-group">
        <div
          className={`tab-item ${activeFilter === 'All' ? 'active' : ''}`}
          onClick={() => handleFilterChange('All')}
        >
          Все
        </div>
        <div
          className={`tab-item ${activeFilter === 'Expired' ? 'active' : ''}`}
          onClick={() => handleFilterChange('Expired')}
        >
          Просроченные
        </div>
        <div
          className={`tab-item ${activeFilter === 'Recommend' ? 'active' : ''}`}
          onClick={() => handleFilterChange('Recommend')}
        >
          Рекомендуем купить
        </div>
      </div>
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
        <button className="light-button" onClick={clearExpiryProducts}>Удалить просроченные</button>
      </div>
    </div>
  );
};

export default Filter;
