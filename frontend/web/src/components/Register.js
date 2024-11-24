import React, { useState, useContext } from 'react';
import api from '../api/api';
import AuthContext from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import emailjs from 'emailjs-com';

const Register = () => {
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [repeat, setRepeat] = useState(false);
  const [pleaseConfirm, setPleaseConfirm] = useState(false);
  const { register } = useContext(AuthContext);

  emailjs.init('_6ilxvs6JJUcODexn');

  const sendConfirmEmail = async (username, email, token) => {
    try {
      const params = {
        to_name: username,
        to_email: email,
        message: `Здравствуйте, ${username}! Пожалуйста, подтвердите ваш email, перейдя по ссылке: http://localhost:3000/confirm-email/${token}`,
      };

      const result = await emailjs.send(
        'service_nz52gpg',
        'template_udwv5wj',
        params
      );
      setPleaseConfirm(true);
      console.log('Email sent successfully!', result.text);
    } catch (error) {
      console.error('Error sending email:', error);
    }
  };

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
      console.log('Response data:', response.data);
      const { token, username: registeredUsername, email: registeredEmail } = response.data;
      register(registeredUsername, registeredEmail, token);
      console.log('Email:', registeredEmail);
      sendConfirmEmail(registeredUsername, registeredEmail, token);
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
        {pleaseConfirm && (<div className="ok-message">
            Подтвердите вашу электронную почту
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
