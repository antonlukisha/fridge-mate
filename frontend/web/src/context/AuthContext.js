import React, { createContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const initAuthState = () => {
		const saved = localStorage.getItem('auth-data');
  	console.log(saved);
    return saved ? JSON.parse(saved) : { auth: false, confirm: false, username: '', email: '', token: '' };
  };

  const [auth, setAuth] = useState(initAuthState);

  useEffect(() => {
    localStorage.setItem('auth-data', JSON.stringify(auth));
  }, [auth]);

	const login = (username, email, token) => {
	  setAuth({ auth: true, confirm: true, username, email, token });
	  localStorage.setItem('auth-data', JSON.stringify({ auth: true, confirm: true, username, email, token }));
	};


	const register = (username, email, token) => {
	  setAuth({ auth: true, confirm: false, username, email, token });
	  localStorage.setItem('auth-data', JSON.stringify({ auth: true, confirm: false, username, email, token }));
	};

	const confirm = () => {
    setAuth((prevState) => ({ ...prevState, confirm: true }));
    const updatedAuth = { ...auth, confirm: true };
    localStorage.setItem('auth-data', JSON.stringify(updatedAuth));
  };

  const logout = () => {
    setAuth({ auth: false, confirm: false, username: '', email: '', token: '' });
    localStorage.removeItem('auth-data');
  };

  return (
    <AuthContext.Provider value={{ auth, login, logout, register, confirm }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
