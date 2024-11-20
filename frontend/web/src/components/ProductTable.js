import React from 'react';

const ProductTable = ({ products }) => {
  return (
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
        {products.map((product) => (
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
  );
};

export default ProductTable;
