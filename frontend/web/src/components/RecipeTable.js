import React, { useState, useEffect } from 'react';
import OpenRecipe from '../components/OpenRecipe';

const RecipeTable = ({ recipes }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [expandedRecipe, setExpandedRecipe] = useState(null);

  const toggleRecipeDetails = (recipe) => {
    if (expandedRecipe && expandedRecipe.id === recipe.id) {
      setIsModalOpen(false);
      setExpandedRecipe(null);
    } else {
      setExpandedRecipe(recipe);
      setIsModalOpen(true);
    }
  };

  useEffect(() => {
    if (isModalOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
  }, [isModalOpen]);

  return (
    <>
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
              <td>{recipе.name}</td>
              <td>{recipе.serving}</td>
              <td>{recipе.ingredients}</td>
              <td>
                <div className="actions">
                  <button
                    className="light-button"
                    onClick={() => toggleRecipeDetails(recipе)}
                  >
                    Просмотр
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <OpenRecipe
        isOpen={isModalOpen}
        onRequestClose={() => setIsModalOpen(false)}
        recipe={expandedRecipe}
      />
    </>
  );
};

export default RecipeTable;
