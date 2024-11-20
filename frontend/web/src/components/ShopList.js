import React from 'react';

const ShopList = ({ items, setItems }) => {
  const toggleBought = (id) => {
    setItems((prevItems) =>
      prevItems.map((item) =>
        item.id === id ? { ...item, bought: !item.bought } : item
      )
    );
  };

  return (
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
  );
};

export default ShopList;
