import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import Header from '../components/Header';
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
      <section>
        <div>Имя: {auth.username ? auth.username : "Не указано"}</div>
        <div>Email: {auth.email ? auth.email : "Не указано"}</div>
      </section>
    </div>
  );
}

export default Account;
