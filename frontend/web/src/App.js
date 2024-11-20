import React from 'react';
import './styles/App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Nav from './components/Nav';
import Main from './pages/Main';
import Fridge from './pages/Fridge';
import Recipes from './pages/Recipes';
import ShoppingList from './pages/ShoppingList';
import Notifications from './pages/Notifications';
import Budget from './pages/Budget';
import Account from './pages/Account';
import Login from './pages/Login';
import Logout from './pages/Logout';
import Register from './pages/Register';

const App = () => (
  <Router>
      <div className="dashboard">
        <Nav />
        <Routes>
          <Route path="/" element={<Main />} />
          <Route path="/fridge" element={<Fridge />} />
          <Route path="/wish" element={<ShoppingList />} />
          <Route path="/recipes" element={<Recipes />} />
          <Route path="/notifications" element={<Notifications />} />
          <Route path="/budget" element={<Budget />} />
          <Route path="/me" element={<Account />} />
          <Route path="/login" element={<Login />} />
          <Route path="/logout" element={<Logout />} />
          <Route path="/register" element={<Register />} />
        </Routes>
      </div>
    </Router>
);

export default App;
