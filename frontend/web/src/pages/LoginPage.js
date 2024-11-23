import React, { useState } from 'react';
import Header from '../components/Header'
import api from '../api/api';
import { useNavigate } from 'react-router-dom';
import Login from '../components/Login';

function LoginPage() {
  return (
    <div className="main">
      <Header
        name={"Добро пожаловать в приложении FridgeMate"}
      />
      <div className="container">
        <section style={{width: '70%'}}>
          <div className="frame">
            <div className="container" style={{gap: '0'}}>
              Вход
            </div>
            <Login />
          </div>
        </section>
      </div>
    </div>
  );
}

export default LoginPage;
