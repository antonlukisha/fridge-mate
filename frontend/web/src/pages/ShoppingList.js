import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Note from '../components/Note';
import ShopList from '../components/ShopList';

const ShoppingList = () => {
  const [items, setItems] = useState([]);
  const [note, setNote] = useState('');
  const navigate = useNavigate();

  const handleFridgeClick = () => {
    navigate('/fridge');
  };

  return (
    <div className="main">
      <Header
        name={"Список покупок"}
      />
      <div className="container">
        <section>
          <button onClick={handleFridgeClick} className="light-button">Вернуться в холодильник</button>
          <ShopList
            items={items}
            setItems={setItems}
          />
          <div className="actions">
            <button className="dark-button">Добавить купленные продукты</button>
            <button className="dark-button" onClick={() => {setItems([])}}>Очистить список</button>
          </div>
        </section>
        <section>
          <Note
            note={note}
            setNote={setNote}
            items={items}
            setItems={setItems}
          />
        </section>
      </div>
    </div>
  );
};

export default ShoppingList;
