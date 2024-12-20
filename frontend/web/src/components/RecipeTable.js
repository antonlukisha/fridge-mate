import React from 'react';

const RecipeTable = ({ recipes }) => {

  return (
    <table className="document-table">
      <thead>
        <tr>
          <th>Название рецепта</th>
          <th>Количество порций</th>
          <th>Ингредиенты</th>
          <th>Действия</th>
        </tr>
      </thead>
      <tbody>
        {recipes.map((recipе) => (
          <tr key={recipе.id}>
            <td >{recipе.name}</td>
            <td>{recipе.serving}</td>
            <td>{recipе.ingredients}
            </td>
            <td>
              <div className="actions">
                <button className="light-button">Просмотр</button>
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default RecipeTable;
