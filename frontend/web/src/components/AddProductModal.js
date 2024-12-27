import React, { useState, useEffect } from 'react';
import Modal from 'react-modal';
import Header from '../components/Header';
import api from '../api/api';

Modal.setAppElement('#root');

const AddProductModal = ({ isOpen, onRequestClose, addProduct, token }) => {
  const [productDetails, setProductDetails] = useState({
    productName: '',
    productQuantity: 1,
    enterType: '',
    typeId: '',
    expiryDate: '',
    addedDate: '',
    amount: 0.0,
  });
  const [types, setTypes] = useState([]);
  const [filteredTypes, setFilteredTypes] = useState([]);
  const [error, setError] = useState('');
  const [isSelect, setIsSelect] = useState(false);

  const fetchTypes = async () => {
    try {
      const cachedTypes = localStorage.getItem('product-types');
      if (cachedTypes) {
        setTypes(JSON.parse(cachedTypes));
      } else {
        const response = await api.get('/products/types/all');
        const gotTypes = response.data.map((item) => ({
          id: item.id,
          name: item.name,
        }));
        setTypes(gotTypes);
        localStorage.setItem('product-types', JSON.stringify(gotTypes));
      }
    } catch (error) {
      console.error('Ошибка загрузки типов продуктов:', error);
    }
  };

  const createProduct = async (newProduct) => {
    try {
      await api.post(`/products/add?token=${token}`, newProduct);
      addProduct();
      onRequestClose();
    } catch (error) {
      console.error('Ошибка при загрузке продукта:', error);
      setError('Не удалось загрузить продукт');
    }
  };

  useEffect(() => {
    fetchTypes();
  }, []);

  useEffect(() => {
    if (productDetails.enterType && types.length > 0 && !isSelect) {
      setFilteredTypes(
        types.filter((type) =>
          type.name.toLowerCase().includes(productDetails.enterType.toLowerCase())
        )
      );
    } else if (isSelect) {
      setIsSelect(false);
    } else {
      setFilteredTypes(types);
    }
  }, [productDetails.enterType, types]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setProductDetails((prevDetails) => ({
      ...prevDetails,
      [name]: value,
    }));
    setError('');
  };

  const handleQuantityChange = (e) => {
    const quantity = Math.max(1, parseInt(e.target.value, 10));
    if (!isNaN(quantity)) {
      setProductDetails((prevDetails) => ({
        ...prevDetails,
        productQuantity: quantity,
      }));
    }
    setError('');
  };

  const handleAmountChange = (e) => {
    const amount = parseFloat(e.target.value);
    if (!isNaN(amount)) {
      setProductDetails((prevDetails) => ({
        ...prevDetails,
        amount: amount,
      }));
    }
  };

  const handleSubmit = () => {
    const { productName, typeId, amount, productQuantity } = productDetails;

    if (!productName || !typeId || !amount) {
      setError('Пожалуйста, укажите необходимые данные');
      return;
    }

    if (productQuantity <= 0 || amount <= 0) {
      setError('Количество и стоимость должны быть положительными числами');
      return;
    }

    const newProduct = {
      name: productName,
      quantity: productQuantity,
      typeId: typeId,
      expiryDate: productDetails.expiryDate,
      addedDate: productDetails.addedDate,
      amount: parseFloat(amount),
    };

    createProduct(newProduct);
  };

  const handleTypeSelect = (type) => {
    setIsSelect(true);
    setProductDetails((prevDetails) => ({
      ...prevDetails,
      enterType: type.name,
      typeId: type.id,
    }));
    setFilteredTypes([]);
  };

  const handleClose = () => {
    setProductDetails({
      productName: '',
      productQuantity: 1,
      enterType: '',
      typeId: '',
      expiryDate: '',
      addedDate: '',
      amount: 0.0,
    });
    setError('');
    onRequestClose();
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={onRequestClose} contentLabel="Add Product" className="modal-content">
      <Header name="Добавить продукт" />
      <div className="modal-body">
        <h3>Название продукта</h3>
        <input
          type="text"
          name="productName"
          value={productDetails.productName}
          placeholder="Введите название"
          className="form-input"
          onChange={handleInputChange}
        />
        <h3>Количество</h3>
        <input
          type="number"
          name="productQuantity"
          value={productDetails.productQuantity}
          className="form-input"
          onChange={handleQuantityChange}
        />
        <h3>Тип продукта</h3>
        <input
          type="text"
          name="enterType"
          value={productDetails.enterType}
          placeholder="Введите название типа"
          className="form-input"
          onChange={handleInputChange}
        />
        {productDetails.enterType && filteredTypes.length > 0 && (
          <ul className="dropdown">
            {filteredTypes.map((type) => (
              <li
                key={type.id}
                className="dropdown-item"
                onClick={() => handleTypeSelect(type)}
              >
                {type.name}
              </li>
            ))}
          </ul>
        )}
        <h3>Дата истечения срока годности</h3>
        <input
          type="date"
          name="expiryDate"
          value={productDetails.expiryDate}
          className="form-input"
          onChange={handleInputChange}
        />
        <h3>Дата добавления</h3>
        <input
          type="date"
          name="addedDate"
          value={productDetails.addedDate}
          className="form-input"
          placeholder="Дата добавления"
          onChange={handleInputChange}
        />
        <h3>Стоимость</h3>
        <input
          type="number"
          name="amount"
          value={isNaN(productDetails.amount) ? '' : productDetails.amount} // Handle NaN
          className="form-input"
          placeholder="Введите стоимость"
          step="0.01"
          onChange={handleAmountChange}
        />
      </div>

      {error && <div className="error-message">{error}</div>}
      <br/>
      <div className="actions">
        <button className="dark-button" onClick={handleSubmit}>Добавить</button>
        <button className="dark-button" onClick={handleClose}>Закрыть</button>
      </div>
    </Modal>
  );
};

export default AddProductModal;
