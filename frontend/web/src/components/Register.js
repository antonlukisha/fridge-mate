import React, { useState, useContext } from 'react';
import api from '../api/api';
import AuthContext from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Register = () => {
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [repeat, setRepeat] = useState(false);
  const navigate = useNavigate();
  const { register } = useContext(AuthContext);

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const response = await api.post('/users/register', {
        'username': username,
        'password': password,
        'email': email
      }, {
        headers: {
          'Content-Type': 'application/json',
        }
      });

      const { token, username: registeredUsername, email: registeredEmail } = response.data;
      register(registeredUsername, registeredEmail, token);
      navigate('/login');
    } catch (error) {
      if (error.response && (error.response.status === 401 || error.response.status === 400)) {
        setRepeat(true);
      }
      console.error('Registration error:', error);
    }

  }

  return (
    <>
      <form onSubmit={handleSubmit} className="note-container">
        <input
          type="text"
          placeholder="Имя пользователя"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          className="form-input"
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
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
        {repeat && (<div className="warn-message">
            Повторите попытку
          </div>)}
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
