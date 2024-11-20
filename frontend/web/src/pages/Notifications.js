import React, { useState } from 'react';

const Notifications = () => {
  // Sample data for notifications
  const initialNotifications = [
    { id: 1, message: 'Молоко истекает через 2 дня', type: 'Warning', date: '19 Ноя 2024' },
    { id: 2, message: 'Вы добавили новый продукт: Яблоки', type: 'Info', date: '18 Ноя 2024' },
    { id: 3, message: 'Сыр просрочен. Удалите его из списка', type: 'Error', date: '17 Ноя 2024' },
    { id: 4, message: 'Хлеб закончится через 5 дней', type: 'Warning', date: '16 Ноя 2024' },
    { id: 5, message: 'Добавьте больше молочных продуктов', type: 'Info', date: '15 Ноя 2024' },
  ];

  const [notifications, setNotifications] = useState(initialNotifications);
  const [activeFilter, setActiveFilter] = useState('All');
  const [entries, setEntries] = useState(5);
  const [currentPage, setCurrentPage] = useState(1);

  const filteredNotifications = notifications.filter((notification) => {
    if (activeFilter === 'All') return true;
    return notification.type === activeFilter;
  });

  const totalPages = Math.ceil(filteredNotifications.length / entries);
  const currentNotifications = filteredNotifications.slice((currentPage - 1) * entries, currentPage * entries);

  const handleFilterChange = (filter) => {
    setActiveFilter(filter);
    setCurrentPage(1); 
  };

  const handleEntriesChange = (event) => {
    setEntries(event.target.value);
    setCurrentPage(1);
  };

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  return (
    <div className="main">
      <div className="header">
        <div>
          <h1>Уведомления</h1>
        </div>
      </div>
        <section>
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
          <table className="document-table">
              <thead>
                  <tr>
                      <th>Тип</th>
                      <th>Дата</th>
                      <th>Сообщение</th>
                      <th>Действия</th>
                  </tr>
              </thead>
              <tbody>
                {currentNotifications.map((notification) => (
                  <tr key={notification.id}>
                    <td>
                      <span className={`type ${notification.type === 'Info' ? 'info' : (notification.type === 'Error' ? 'error' : 'warning')}`}>
                        {notification.type}
                      </span>
                    </td>
                    <td>{notification.date}</td>
                    <td>{notification.message}</td>                    
                    <td>
                      <button className="light-button">Удалить</button>
                    </td>
                  </tr>
                ))}
              </tbody>
          </table>
          <div className="pagination">
            <div className="entries">
              <span>Показать </span>
              <select
                value={entries}
                onChange={handleEntriesChange}
                className="dropdown"
              >
                <option value="5">5</option>
                <option value="10">10</option>
                <option value="50">50</option>
              </select>
              <span> из { filteredNotifications.length } результатов</span>
            </div>
            <div className="page-numbers">
              {Array.from({ length: totalPages }, (_, index) => (
                <a
                  key={index}
                  href="#"
                  className={currentPage === index + 1 ? 'active' : ''}
                  onClick={() => handlePageChange(index + 1)}
                >
                  {index + 1}
                </a>
              ))}
            </div>
          </div>
          <div className="actions">
              <button className="dark-button">Очистить все уведомления</button>
          </div>
        </section>
    </div>
  );
};

export default Notifications;
