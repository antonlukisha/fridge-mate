import React from 'react';
import { render } from '@testing-library/react';
import { expect } from 'chai';
import { MemoryRouter } from 'react-router-dom';
import Fridge from './src/pages/Fridge';

describe('Fridge Component Tests:', () => {
  const renderWithRouter = (ui) => {
    return render(<MemoryRouter>{ui}</MemoryRouter>);
  };

  it('TEST 1 (renders the header correctly)', () => {
    renderWithRouter(<Fridge />);
    const header = screen.getByText('Ваши продукты');
    expect(header).to.exist;
  });

  it('TEST 2 (renders all products initially)', () => {
    renderWithRouter(<Fridge />);
    const productRows = screen.getAllByRole('row');
    expect(productRows).to.have.length(6); // 5 продуктов + заголовок таблицы
  });

  it('TEST 3 (filters expired products correctly)', () => {
    renderWithRouter(<Fridge />);
    const expiredTab = screen.getByText('Просроченные');
    fireEvent.click(expiredTab);

    const rows = screen.getAllByRole('row');
    expect(rows).to.have.length(2); // 1 продукт + заголовок таблицы
    expect(screen.getByText('Яблоки')).to.exist;
  });

  it('TEST 4 (searches products by name)', () => {
    renderWithRouter(<Fridge />);
    const searchInput = screen.getByPlaceholderText('Введите название');
    fireEvent.change(searchInput, { target: { value: 'Сыр' } });

    const rows = screen.getAllByRole('row');
    expect(rows).to.have.length(2); // 1 продукт + заголовок таблицы
    expect(screen.getByText('Сыр')).to.exist;
  });

  it('TEST 5 (navigates to wish list when the button is clicked)', () => {
    const mockNavigate = cy.spy(); // Подменяем `useNavigate`
    renderWithRouter(<Fridge />);
    const shopListButton = screen.getByText('Создать список покупок');
    fireEvent.click(shopListButton);

    expect(mockNavigate).to.have.been.calledWith('/wish');
  });
});
