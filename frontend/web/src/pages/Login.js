import React, { useState, useContext } from 'react';
import api from '../api/ApiClient';
import AuthContext from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

function Login() {
  const [user, setUser] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();
  const { login } = useContext(AuthContext);

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const response = await api.post('/users/login', { user, password });
      if (response.status !== 200) {
        return;
      }
      const { token, username, email } = response.data;
      login( username, email, token );
      navigate('/');
    } catch (error) {
      console.error('Login error:', error);
    }

  }

  return (
    <>
      <form onSubmit={handleSubmit} className="note-container">
        <input
          type="user"
          placeholder="Имя пользователя или Email"
          value={user}
          onChange={(event) => setUser(event.target.value)}
          className="form-input"
          required
        />
        <input
          type="password"
          placeholder="Пароль"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          className="form-input"
          required
        />
        <button
          className="dark-button"
          type="submit"
          style={{fontSize: '22px'}}
        >Войти</button>
      </form>
    </>
  );
}

export default Login;
