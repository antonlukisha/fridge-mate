import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const ShoppingList = () => {
  const initialItems = [
    { id: 1, name: 'Хлеб', bought: false },
    { id: 2, name: 'Молоко', bought: false },
    { id: 3, name: 'Яйца', bought: false },
    { id: 4, name: 'Сыр', bought: false }
  ];
  const [items, setItems] = useState([]);
  const [note, setNote] = useState('');
  const navigate = useNavigate();

  const toggleBought = (id) => {
    setItems((prevItems) =>
      prevItems.map((item) =>
        item.id === id ? { ...item, bought: !item.bought } : item
      )
    );
  };

  const handleFridgeClick = () => {
    navigate('/fridge');
  };

  const handleChange = (event) => {
    setNote(event.target.value);
  };

  const handleEntry = () => {
    if (note == '' || note.includes('\n') || note.length > 20) return;
    setItems((prevItems) => [...prevItems, { id: note.length + 1, name: note, bought: false }]);
    setNote('');
  };

  return (
    <div className="main">
      <div className="header">
        <div>
          <h1>Список покупок</h1>
        </div>
      </div>
      <div className="container">
        <section>
          <button onClick={handleFridgeClick} className="light-button">Вернуться в холодильник</button>
          <ul className="shop-list">
            {items.length == 0 && "Список пуст"}
            {items.map((item) => (
              <li
                key={item.id}
                style={{
                  textDecoration: item.bought ? 'line-through' : 'none',
                  color: item.bought ? 'gray' : 'black'
                }}
              >
                <input
                  type="checkbox"
                  checked={item.bought}
                  onChange={() => toggleBought(item.id)}
                />
                {item.name}
              </li>
            ))}
          </ul>
          <div className="actions">
            <button className="dark-button">Добавить купленные продукты</button>
            <button className="dark-button" onClick={() => {setItems([])}}>Очистить список</button>
          </div>
        </section>
        <section>
          <div className="note-container">
            <textarea
              className="note-textarea"
              value={note}
              onChange={handleChange}
              placeholder="Введите текст заметки..."
              rows="8"
            />
            <button className="light-button" onClick={handleEntry}>Добавить запись</button>
          </div>
        </section>
      </div>
    </div>
  );
};

export default ShoppingList;
