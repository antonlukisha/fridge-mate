import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import ProductTable from '../components/ProductTable';
import Filter from '../components/Filter';
import Pagination from '../components/Pagination';
import Header from '../components/Header';
import { useFilteredProducts } from '../hooks/useFilteredProducts';
import { usePagination } from '../hooks/usePagination';
import api from '../api/api';
import AuthContext from '../context/AuthContext';

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

  const [products, setProducts] = useState([]);
  const [searchInput, setSearchInput] = useState('');
  const [activeFilter, setActiveFilter] = useState('All');
  const [entries, setEntries] = useState(5);
  const {currentPage, setCurrentPage} = usePagination();
  const navigate = useNavigate();
  const { auth } = useContext(AuthContext);

  const fetchProducts = async () => {
    try {
      const cachedProducts = localStorage.getItem('products');

      if (cachedProducts) {
        setProducts(JSON.parse(cachedProducts));
      } else {
        const response = await api.get(`/products/all?token=${auth.token}`);
        const gotProducts = response.data.map((item) => {
          let diff = Math.floor((new Date(item.expiryDate) - new Date()) / (1000 * 60 * 60 * 24));
          let status = '';
          if (diff > 1) status = 'Свежий';
          else if (diff > 0) status = 'Истекает срок';
          else status = 'Просроченный';
          return {
            id: item.id,
            name: item.name,
            type: item.type.name,
            quantity: item.quantity + ' ' + item.type.quantityType,
            status: status,
            addedDate: new Date(item.addedDate).toLocaleDateString('ru-RU', {
              day: '2-digit',
              month: 'short',
              year: 'numeric'
            }),
          };
        });

        setProducts(gotProducts);
        localStorage.setItem('products', JSON.stringify(gotProducts));
      }
    } catch (error) {
      console.error('Products loading error:', error);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const filteredProducts = useFilteredProducts(products, searchInput, activeFilter);

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
