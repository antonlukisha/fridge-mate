import React from 'react';

const RefreshButton = ({ onClick }) => {
  return (
    <button className="dark-button" onClick={onClick}>
      <i className="fas fa-sync-alt"></i>
    </button>
  );
};

export default RefreshButton;
