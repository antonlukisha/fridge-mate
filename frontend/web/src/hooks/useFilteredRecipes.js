import { useMemo } from 'react';

export const useFilteredRecipes = (recipes, searchInput) => {
  return useMemo(() => {
    return recipes.filter(recipе => {
      const matchesSearch = recipе.name.toLowerCase().includes(searchInput.toLowerCase());
      return matchesSearch;
    });
  }, [recipes, searchInput]);
};
