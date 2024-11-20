import React from 'react';

const NotificationFilter = ({ activeFilter, setActiveFilter, setCurrentPage }) => {
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
          className={`tab-item ${activeFilter === 'Error' ? 'active' : ''}`}
          onClick={() => handleFilterChange('Error')}
        >
          Проблемы
        </div>
        <div
          className={`tab-item ${activeFilter === 'Warning' ? 'active' : ''}`}
          onClick={() => handleFilterChange('Warning')}
        >
          Предупреждения
        </div>
      </div>
    </div>
  );
};

export default NotificationFilter;
