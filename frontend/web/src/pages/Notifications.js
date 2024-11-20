import React, { useState } from 'react';
import NotificationTable from '../components/NotificationTable';
import Pagination from '../components/Pagination';
import Header from '../components/Header';
import NotificationFilter from '../components/NotificationFilter';
import { useFilteredNotifications } from '../hooks/useFilteredNotifications';
import { usePagination } from '../hooks/usePagination';

const Notifications = () => {
  const initialNotifications = [
    { id: 1, message: 'Молоко истекает через 2 дня', type: 'Warning', date: '19 Ноя 2024' },
    { id: 2, message: 'Вы добавили новый продукт: Яблоки', type: 'Info', date: '18 Ноя 2024' },
    { id: 3, message: 'Сыр просрочен. Удалите его из списка', type: 'Error', date: '17 Ноя 2024' },
    { id: 4, message: 'Хлеб закончится через 5 дней', type: 'Warning', date: '16 Ноя 2024' },
    { id: 5, message: 'Добавьте больше молочных продуктов', type: 'Info', date: '15 Ноя 2024' },
    { id: 6, message: 'Молоко истекает через 2 дня', type: 'Warning', date: '19 Ноя 2024' },
    { id: 7, message: 'Вы добавили новый продукт: Яблоки', type: 'Info', date: '18 Ноя 2024' },
    { id: 8, message: 'Сыр просрочен. Удалите его из списка', type: 'Error', date: '17 Ноя 2024' },
    { id: 9, message: 'Хлеб закончится через 5 дней', type: 'Warning', date: '16 Ноя 2024' },
    { id: 10, message: 'Добавьте больше молочных продуктов', type: 'Info', date: '16 Ноя 2024' },
  ];

  const [notifications, setNotifications] = useState(initialNotifications);
  const [activeFilter, setActiveFilter] = useState('All');
  const [entries, setEntries] = useState(5);
  const {currentPage, setCurrentPage} = usePagination();

  const filteredNotifications = useFilteredNotifications(initialNotifications, activeFilter);

  const totalPages = Math.ceil(filteredNotifications.length / entries);
  const currentNotifications = filteredNotifications.slice((currentPage - 1) * entries, currentPage * entries);

  return (
    <div className="main">
      <Header
        name={"Уведомления"}
      />
        <section>
          <NotificationFilter
            activeFilter={activeFilter}
            setActiveFilter={setActiveFilter}
            setCurrentPage={setCurrentPage}
          />
          <NotificationTable
            notifications={currentNotifications}
          />
          <Pagination
            totalPages={totalPages}
            currentPage={currentPage}
            setCurrentPage={setCurrentPage}
            entries={entries}
            setEntries={setEntries}
            totalResults={filteredNotifications.length}
          />
          <div className="actions">
              <button className="dark-button">Очистить все уведомления</button>
          </div>
        </section>
    </div>
  );
};

export default Notifications;
