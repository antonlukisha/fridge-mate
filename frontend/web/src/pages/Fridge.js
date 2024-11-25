import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ProductTable from '../components/ProductTable';
import Filter from '../components/Filter';
import Pagination from '../components/Pagination';
import Header from '../components/Header';
import { useFilteredProducts } from '../hooks/useFilteredProducts';
import { usePagination } from '../hooks/usePagination';
import api from '../api/api';

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
    { id: 12, name: 'Хлеб', type: 'Хлебобулочные изделия', quantity: '1 шт', status: 'Свежий', addedDate: '18 Ноя 2024' },
  ];


  const [searchInput, setSearchInput] = useState('');
  const [activeFilter, setActiveFilter] = useState('All');
  const [entries, setEntries] = useState(5);
  const {currentPage, setCurrentPage} = usePagination();
  const navigate = useNavigate();

  const filteredProducts = useFilteredProducts(initialProducts, searchInput, activeFilter);

  const totalPages = Math.ceil(filteredProducts.length / entries);
  const currentProducts = filteredProducts.slice((currentPage - 1) * entries, currentPage * entries);

  const handleShopListClick = () => {
    navigate('/wish');
  };

  return (
    <div className="main">
      <Header
        name={"Ваши продукты"}
      />
        <section>
          <Filter
            searchInput={searchInput}
            setSearchInput={setSearchInput}
            activeFilter={activeFilter}
            setActiveFilter={setActiveFilter}
            setCurrentPage={setCurrentPage}
          />
          <ProductTable
            products={currentProducts}
          />
          <Pagination
            totalPages={totalPages}
            currentPage={currentPage}
            setCurrentPage={setCurrentPage}
            entries={entries}
            setEntries={setEntries}
            totalResults={filteredProducts.length}
          />
          <div className="actions">
              <button className="dark-button">Добавить продукт</button>
              <button onClick={handleShopListClick} className="dark-button">Создать список покупок</button>
          </div>
        </section>
    </div>
  );
};

export default Fridge;
