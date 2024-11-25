import React, { useContext } from 'react';
import Header from '../components/Header';
import Splash from '../components/Splash';
import AuthContext from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Main = () => {
  const { auth } = useContext(AuthContext);
  const navigate = useNavigate();
  const handleStart = () => {
    if (auth.auth) {
      navigate('/fridge');
    } else {
      navigate('/auth');
    }
  }
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
            onClick={handleStart}
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
