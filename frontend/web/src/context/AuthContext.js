import React, { createContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const initAuthState = () => {
    const saved = localStorage.getItem('auth');
    return saved ? JSON.parse(saved) : { auth: false, confirm: false, username: '', email: '', token: '' };
  };

  const [auth, setAuth] = useState(initAuthState);

  useEffect(() => {
    localStorage.setItem('auth', JSON.stringify(auth));
  }, [auth]);

  const login = (userData) => {
    setAuth({ auth: true, confirm: false, ...userData });
    localStorage.setItem('auth', JSON.stringify(userData));
  };

  const logout = () => {
    setAuth({ auth: false, confirm: false, username: '', email: '', token: '' });
    localStorage.removeItem('auth');
  };

  return (
    <AuthContext.Provider value={{ auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
