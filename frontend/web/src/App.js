import React from 'react';
import './styles/App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Nav from './components/Nav';
import routes from './routes/routes';
import { AuthProvider } from './context/AuthContext';

const App = () => {
  return (
    <Router>
      <div className="dashboard">
        <AuthProvider>
          <Nav />
          <Routes>
            {routes.map((route, index) => (
              <Route key={index} path={route.path} element={route.element} />
            ))}
          </Routes>
        </AuthProvider>
      </div>
    </Router>
  );
};

export default App;
