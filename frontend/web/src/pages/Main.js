import React, { useContext } from 'react';
import Header from '../components/Header';
import Splash from '../components/Splash';
import AuthContext from '../context/AuthContext';

const Main = () => {
  const { auth } = useContext(AuthContext);
  return (
    <div className="main">
      <section className="main-container">
        <div className="main-text-section">
          <div className="main-header">
            FridgeMate
            <span className="main-span">Твой виртуальный холодильник </span>
          </div>
          <div className="main-text">
            Приложение которое поможет вам эффективно управлять запасами своих продуктов, минимизировать их порчу, рационально готовить и планировать свой бюджет на покупку продуктов.
          </div>
          <button
            className="dark-button"
            style={{fontSize: '22px'}}
          >Начать</button>
        </div>
        <div className="art-container">
          <Splash />
        </div>
      </section>
    </div>
  );
}

export default Main;
