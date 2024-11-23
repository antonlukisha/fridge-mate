import React, { useState } from 'react';
import api from '../api/api';
import { useNavigate } from 'react-router-dom';

const Register = () => {
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleSubmit = () => {

  }

  return (
    <>
      <form onSubmit={handleSubmit} className="note-container">
        <input
          type="text"
          placeholder="Имя пользователя"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="form-input"
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setUsername(e.target.value)}
          className="form-input"
          required
        />
        <input
          type="password"
          placeholder="Пароль"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="form-input"
          required
        />
        <button
          className="dark-button"
          type="submit"
          style={{fontSize: '22px'}}
        >Зарегистрироваться</button>
      </form>
    </>
  );
}

export default Register;
