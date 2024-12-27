import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import ProductTable from '../components/ProductTable';
import AddProductModal from '../components/AddProductModal';
import Filter from '../components/Filter';
import Pagination from '../components/Pagination';
import RefreshButton from '../components/RefreshButton';
import Header from '../components/Header';
import { useFilteredProducts } from '../hooks/useFilteredProducts';
import { usePagination } from '../hooks/usePagination';
import api from '../api/api';
import AuthContext from '../context/AuthContext';

const Fridge = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [products, setProducts] = useState([]);
  const [searchInput, setSearchInput] = useState('');
  const [activeFilter, setActiveFilter] = useState('All');
  const [entries, setEntries] = useState(5);
  const {currentPage, setCurrentPage} = usePagination();
  const navigate = useNavigate();
  const { auth } = useContext(AuthContext);

  const handleRefresh = async () => {
    try {
      const response = await api.get(`/products/all?token=${auth.token}`);
      const gotProducts = response.data.map((item) => {
        let diff = Math.floor((new Date(item.expiryDate) - new Date()) / (1000 * 60 * 60 * 24));
        let status = '';
        if (diff > 1) status = 'Свежий';
        else if (diff >= 0) status = 'Истекает срок';
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
    } catch (error) {
      console.error('Products refresh error:', error);
    }
  };

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
          else if (diff >= 0) status = 'Истекает срок';
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

  useEffect(() => {
    if (isModalOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
  }, [isModalOpen]);

  const handleAddProducts = () => {
    localStorage.removeItem('products');
    fetchProducts();
    navigate('/fridge');
  }

  const filteredProducts = useFilteredProducts(products, searchInput, activeFilter);

  const totalPages = Math.ceil(filteredProducts.length / entries);
  const currentProducts = filteredProducts.slice((currentPage - 1) * entries, currentPage * entries);

  const handleShopListClick = () => {
    navigate('/wish');
  };

  const clearExpiryProducts = async () => {
    try {
      await api.delete(`/products/expired?token=${auth.token}`);
      localStorage.removeItem('products');
      fetchProducts();
      navigate('/fridge');
    } catch (error) {
      console.error('Expiry products clearing error:', error);
    }
  }

  const deleteProduct = async (id) => {
    try {
      await api.delete(`/products/id?id=${id}`);
      const updatedProducts = products.filter((product) => product.id !== id);
      setProducts(updatedProducts);
      localStorage.setItem('products', JSON.stringify(updatedProducts));
      navigate('/fridge');
    } catch (error) {
      console.error('Products deleting error:', error);
    }
  }

  const toggleAddModal = () => setIsModalOpen(!isModalOpen);

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
            clearExpiryProducts={clearExpiryProducts}
          />
          <ProductTable
            products={currentProducts}
            deleteAction={(id) => deleteProduct(id)}
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
              <button onClick={toggleAddModal} className="dark-button">Добавить продукт</button>
              <button onClick={handleShopListClick} className="dark-button">Создать список покупок</button>
              <RefreshButton onClick={handleRefresh} />
          </div>
        </section>
        <AddProductModal
            isOpen={isModalOpen}
            onRequestClose={toggleAddModal}
            addProduct={handleAddProducts}
            token={auth.token}
          />
    </div>
  );
};

export default Fridge;
