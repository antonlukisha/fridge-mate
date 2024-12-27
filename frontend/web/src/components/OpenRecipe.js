import React from 'react';
import Modal from 'react-modal';
import Header from '../components/Header';

Modal.setAppElement('#root');

const OpenRecipe = ({ isOpen, onRequestClose, recipe }) => {
  if (!recipe) return null; // если рецепта нет, ничего не рендерим

  const checkIngredientAvailability = (ingredient) => {
    const availableProducts = JSON.parse(localStorage.getItem('products'));
    return availableProducts.some(product => product.type === ingredient);
  };

  const addToShopList = () => {
    const shoppingList = JSON.parse(localStorage.getItem('product-list')) || [];

    recipe.ingredients.split(', ').forEach(ingredient => {
      if (!checkIngredientAvailability(ingredient)) {
        const newItem = { id: shoppingList.length + 1, name: ingredient, bought: false };
        shoppingList.push(newItem);
      }
    });

    localStorage.setItem('product-list', JSON.stringify(shoppingList));
  }

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onRequestClose}
      contentLabel="Open Recipe"
      className="modal-content"
    >
      <Header name={recipe.name} />
      <div className="recipe-details">
        <strong>Ингредиенты:</strong>
        <ul>
          {recipe.ingredients.split(', ').map((ingredient, index) => (
            <li key={index} className={checkIngredientAvailability(ingredient) ? 'available' : 'not-available'}>{ingredient}</li>
          ))}
        </ul>
        <strong>Приготовление:</strong>
        <p>{recipe.instructions}</p>
      </div>
      <div className="actions">
        <button className="dark-button" onClick={addToShopList}>Обновить список покупок</button>
        <button className="dark-button" onClick={onRequestClose}>Закрыть</button>
      </div>
    </Modal>
  );
};

export default OpenRecipe;
