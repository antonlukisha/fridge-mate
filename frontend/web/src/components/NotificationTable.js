import React from 'react';

const NotificationTable = ({ notifications }) => {
  return (
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
      {notifications.map((notification) => (
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
  );
};

export default NotificationTable;
