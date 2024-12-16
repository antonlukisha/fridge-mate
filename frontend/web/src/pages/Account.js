import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import Header from '../components/Header';
import SplashAccount from '../components/SplashAccount';
import AuthContext from '../context/AuthContext';

function Account() {
  const { auth } = useContext(AuthContext);

  if (!auth.auth) {
    return <Navigate to="/login" />;
  }

  return (
    <div className="main">
      <Header
        name={"Ваш профиль"}
      />
      <section className="main-container">
        <div className="account-text-section">
          <h3 style={{margin: '0', fontSize: '20px'}}>ЛИЧНЫЕ ДАННЫЕ</h3>
          <div className="divider"></div>
          <div className="account-text">
            <h4>Имя: </h4>
            <h4 className="bold-part">{auth.username ? auth.username : "Не указано"}</h4>
          </div>
          <div className="account-text">
            <h4>Email: </h4>
            <h4 className="bold-part">{auth.email ? auth.email : "Не указано"}</h4>
          </div>
          <br/>
          <button className="dark-button">Поменять пароль</button>
          <button className="light-button">Удалить аккаунт</button>
        </div>
        <div className="art-container-account">
          <SplashAccount />
        </div>
      </section>
    </div>
  );
}

export default Account;
