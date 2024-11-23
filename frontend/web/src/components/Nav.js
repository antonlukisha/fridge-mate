import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import AuthContext from '../context/AuthContext';
import { Dashboard, Notifications, BarChart, Logout, Login, Assignment, Icecream, AccountCircle } from '@mui/icons-material';

const Nav = () => {
  const { auth, logout } = useContext(AuthContext);

  return (
    <div className="nav">
      <Link to="/">
        <div className="logo">
          <h1>fridge</h1>
          <h1 className="black-part">MATE</h1>
        </div>
      </Link>
      <div className="pages">
        <div className="general">
          { auth.auth &&
            <>
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
            </>
          }
          <div className="general-label">
            ЛИЧНЫЙ КАБИНЕТ
          </div>
          <ul className="general-nav">
            { auth.auth ? (
              <>
                <li><Link to="/me"><AccountCircle />{auth.username}</Link></li>
                <li><Link to="/logout" onClick={logout}><Logout />Выход</Link></li>
              </>
            ) : (
              <li><Link to="/auth"><Logout />Вход</Link></li>
            ) }
          </ul>
        </div>
      </div>
      <div className="auth">
      </div>
    </div>
  );
};

export default Nav;
