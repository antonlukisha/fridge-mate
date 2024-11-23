import React, { useState } from 'react';
import Header from '../components/Header'
import api from '../api/api';
import { useNavigate } from 'react-router-dom';
import Login from '../pages/Login';
import Register from '../pages/Register';

function Auth() {
  const [isLogin, setIsLogin] = useState(true);
  const navigate = useNavigate();

  const handleChange = () => {
    setIsLogin(!isLogin);
  }

  return (
    <div className="main">
      <Header
        name={"Добро пожаловать в приложении FridgeMate"}
      />
      <div className="container">
        <section style={{width: '70%'}}>
          <div className="frame">
            <div className="container" style={{gap: '0'}}>
              <button
                className={`dark-button-auth ${isLogin ? 'active' : ''}`}
                disabled={ isLogin }
                onClick={handleChange}
              >Вход</button>
              <button
                className={`dark-button-auth ${!isLogin ? 'active' : ''}`}
                disabled={ !isLogin }
                onClick={handleChange}
              >Регистрация</button>
            </div>
            {isLogin && <Login />}
            {!isLogin &&  <Register />}
          </div>
        </section>
      </div>
    </div>
  );
}

export default Auth;
