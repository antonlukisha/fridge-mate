import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Fridge = () => {
  const initialProducts = [
    { id: 1, name: 'Молоко Ряженка', type: 'Молоко', quantity: '100 мл', status: 'Свежий', addedDate: '29 Июл 2023' },
    { id: 2, name: 'Яблоки', type: 'Фрукты', quantity: '1 кг', status: 'Просроченный', addedDate: '01 Авг 2023' },
    { id: 3, name: 'Сыр', type: 'Молочные продукты', quantity: '200 г', status: 'Истекает срок', addedDate: '15 Ноя 2023' },
    { id: 4, name: 'Хлеб', type: 'Хлебобулочные изделия', quantity: '1 шт', status: 'Свежий', addedDate: '18 Ноя 2023' },
    { id: 5, name: 'Молоко Ряженка', type: 'Молоко', quantity: '100 мл', status: 'Свежий', addedDate: '29 Июл 2023' },
    { id: 6, name: 'Яблоки', type: 'Фрукты', quantity: '1 кг', status: 'Просроченный', addedDate: '01 Авг 2023' },
    { id: 7, name: 'Сыр', type: 'Молочные продукты', quantity: '200 г', status: 'Истекает срок', addedDate: '15 Ноя 2023' },
    { id: 8, name: 'Хлеб', type: 'Хлебобулочные изделия', quantity: '1 шт', status: 'Свежий', addedDate: '18 Ноя 2023' },
    { id: 9, name: 'Молоко Ряженка', type: 'Молоко', quantity: '100 мл', status: 'Свежий', addedDate: '29 Июл 2023' },
    { id: 10, name: 'Яблоки', type: 'Фрукты', quantity: '1 кг', status: 'Просроченный', addedDate: '01 Авг 2023' },
    { id: 11, name: 'Сыр', type: 'Молочные продукты', quantity: '200 г', status: 'Истекает срок', addedDate: '15 Ноя 2023' },
    { id: 12, name: 'Хлеб', type: 'Хлебобулочные изделия', quantity: '1 шт', status: 'Свежий', addedDate: '18 Ноя 2023' },
  ];

  const [products, setProducts] = useState(initialProducts);
  const [searchInput, setSearchInput] = useState('');
  const [activeFilter, setActiveFilter] = useState('All');
  const [entries, setEntries] = useState(5);
  const [currentPage, setCurrentPage] = useState(1);
  const navigate = useNavigate();

  const filteredProducts = products.filter((product) => {
    const matches = product.name.toLowerCase().includes(searchInput.toLowerCase());

    if (activeFilter === 'Expired') {
      return product.status === 'Просроченный' && matches;
    }
    if (activeFilter === 'Recommend') {
      return product.status === 'Истекает срок' && matches;
    }
    return matches;
  });

  const totalPages = Math.ceil(filteredProducts.length / entries);
  const currentProducts = filteredProducts.slice((currentPage - 1) * entries, currentPage * entries);

  const handleFilterChange = (filter) => {
    setActiveFilter(filter);
    setCurrentPage(1); 
  };

  const handleSearchChange = (event) => {
    setSearchInput(event.target.value);
    setCurrentPage(1); 
  };

  const handleEntriesChange = (event) => {
    setEntries(event.target.value);
    setCurrentPage(1);
  };

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const handleShopListClick = () => {
    navigate('/wish');
  };

  return (
    <div className="main">
      <div className="header">
        <div>
          <h1>Ваши продукты</h1>
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
              <button className="light-button">Удалить просроченные</button>
            </div>
          </div>
          <table className="document-table">
              <thead>
                  <tr>
                      <th>Название продукта</th>
                      <th>Тип</th>
                      <th>Количество</th>
                      <th>Статус</th>
                      <th>Действия</th>
                  </tr>
              </thead>
              <tbody>
                {currentProducts.map((product) => (
                  <tr key={product.id}>
                    <td>{product.name} <span className="upload-date">Добавлен {product.addedDate}</span></td>
                    <td>{product.type}</td>
                    <td>{product.quantity}</td>
                    <td>
                      <span className={`status ${product.status === 'Свежий' ? 'active' : (product.status === 'Просроченный' ? 'expired' : 'missing')}`}>
                        {product.status}
                      </span>
                    </td>
                    <td>
                      <div className="actions">
                        <button className="light-button">Изменить</button>
                        <button className="light-button">Удалить</button>
                      </div>
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
              <span> из { filteredProducts.length } результатов</span>
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
              <button className="dark-button">Добавить продукт</button>
              <button onClick={handleShopListClick} className="dark-button">Создать список покупок</button>
          </div>
        </section>
    </div>
  );
};

export default Fridge;