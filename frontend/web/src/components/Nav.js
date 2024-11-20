import React from 'react';
import { Link } from 'react-router-dom';
import { Dashboard, Notifications, BarChart, Logout, Assignment, Icecream, AccountCircle } from '@mui/icons-material';

const Nav = () => (
  <div className="nav">
    <div className="logo">
      <h1>fridge</h1>
      <h1 className="black-part">MATE</h1>
    </div>
    <div className="pages">
      <div className="general">
        <div className="general-label">
          МЕНЮ
        </div>
        <ul className="general-nav">
          <li><Link to="/fridge"><Dashboard />Холодильник</Link></li>
          <li><Link to="/wish"><Assignment />Список покупок</Link></li>
          <li><Link to="/recipes"><Icecream />Рецепты</Link></li>
          <li><Link to="/notifications"><Notifications />Уведомления</Link></li>
          <li><Link to="/budget"><BarChart />Бюджет</Link></li>
        </ul>
        <div className="general-label">
          ЛИЧНЫЙ КАБИНЕТ
        </div>
        <ul className="general-nav">
          <li><Link to="/me"><AccountCircle /> Антон</Link></li>
          <li><Link to="/logout"><Logout /> Выход</Link></li>
        </ul>
      </div>
    </div>
    <div className="auth">
      
    </div>
  </div>
);

export default Nav;
