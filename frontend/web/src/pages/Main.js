import React from 'react';
import Header from '../components/Header';

const Main = () => (
  <div className="main">
    <Header
      name={"Виртуальный холодильник"}
    />
    <div className="description">
      Проект "Электронный холодильник студента" может стать полезным и увлекательным приложением, которое поможет пользователю эффективно управлять запасами продуктов, минимизировать их порчу и рационально готовить.
    </div>
  </div>
);

export default Main;