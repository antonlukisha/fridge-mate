import Header from '../components/Header';
import RecipeTable from '../components/RecipeTable';
import RecipeFilter from '../components/RecipeFilter';
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { usePagination } from '../hooks/usePagination';
import { useFilteredRecipes } from '../hooks/useFilteredRecipes';
import Pagination from '../components/Pagination';
import api from '../api/api';

const Recipes = () => {
  const [recipes, setRecipes] = useState([]);
  const [searchInput, setSearchInput] = useState('');
  const [entries, setEntries] = useState(5);
  const {currentPage, setCurrentPage} = usePagination();
  const navigate = useNavigate();

  const fetchRecipes = async () => {
    try {
      const cachedRecipes = localStorage.getItem('recipes');

      if (cachedRecipes) {
        setRecipes(JSON.parse(cachedRecipes));
      } else {
        const response = await api.get('/recipes/all');
        const gotRecipes = response.data.map((item) => {
          return {
            id: item.id,
            name: item.name,
            serving: item.serving,
            ingredients: item.ingredients,
            instructions: item.instructions,
          };
        });

        setRecipes(gotRecipes);
        localStorage.setItem('recipes', JSON.stringify(gotRecipes));
      }
    } catch (error) {
      console.error('Recipes loading error:', error);
    }
  };

  useEffect(() => {
    fetchRecipes();
  }, []);

  const filteredRecipes = useFilteredRecipes(recipes, searchInput);

  const totalPages = Math.ceil(filteredRecipes.length / entries);
  const currentRecipes = filteredRecipes.slice((currentPage - 1) * entries, currentPage * entries);

  return (
    <div className="main">
      <Header
        name={"Рецепты"}
      />
      <section>
        <RecipeFilter
          searchInput={searchInput}
          setSearchInput={setSearchInput}
          setCurrentPage={setCurrentPage}
        />
        <RecipeTable
          recipes={currentRecipes}
        />
        <Pagination
          totalPages={totalPages}
          currentPage={currentPage}
          setCurrentPage={setCurrentPage}
          entries={entries}
          setEntries={setEntries}
          totalResults={filteredRecipes.length}
        />
      </section>
    </div>
  );
};

export default Recipes;
