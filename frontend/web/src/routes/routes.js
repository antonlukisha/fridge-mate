import Main from '../pages/Main';
import Fridge from '../pages/Fridge';
import Recipes from '../pages/Recipes';
import ShoppingList from '../pages/ShoppingList';
import Notifications from '../pages/Notifications';
import Budget from '../pages/Budget';
import Account from '../pages/Account';
import Logout from '../pages/Logout';
import Auth from '../pages/Auth';
import LoginPage from '../pages/LoginPage';
import Error from '../pages/Error';

const routes = [
  {
    path: '/',
    element: <Main />,
  },
  {
    path: '/fridge',
    element: <Fridge />,
  },
  {
    path: '/wish',
    element: <ShoppingList />,
  },
  {
    path: '/recipes',
    element: <Recipes />,
  },
  {
    path: '/notifications',
    element: <Notifications />,
  },
  {
    path: '/budget',
    element: <Budget />,
  },
  {
    path: '/me',
    element: <Account />,
  },
  {
    path: '/auth',
    element: <Auth />,
  },
  {
    path: '/logout',
    element: <Logout />,
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '*',
    element: <Error />,
  },
];

export default routes;
