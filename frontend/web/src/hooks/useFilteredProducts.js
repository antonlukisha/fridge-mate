import { useMemo } from 'react';

export const useFilteredProducts = (products, searchInput, activeFilter) => {
  return useMemo(() => {
    return products.filter(product => {
      const matchesSearch = product.name.toLowerCase().includes(searchInput.toLowerCase());
      const matchesFilter =
        activeFilter === 'All' || 
        (activeFilter === 'Expired' && product.status === 'Просроченный') ||
        (activeFilter === 'Recommend' && product.status === 'Истекает срок');
      return matchesSearch && matchesFilter;
    });
  }, [products, searchInput, activeFilter]);
};
