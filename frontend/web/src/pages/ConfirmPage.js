import React, { useEffect, useState, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import AuthContext from '../context/AuthContext';
import api from '../api/api';

const ConfirmPage = () => {
  const { token } = useParams();
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const { confirm } = useContext(AuthContext);

  useEffect(() => {
    api.put(`/users/verified?token=${token}`)
      .then(response => {
        confirm();
        setMessage('Ваша учетная запись успешно подтверждена!');
        navigate('/');
      })
      .catch(error => {
        setMessage('Ошибка подтверждения. Возможно, токен недействителен.');
        navigate('/auth');
      });
  }, [token]);

  return (
    <div className="main">
      <h2>{message}</h2>
    </div>
  );
};

export default ConfirmPage;
