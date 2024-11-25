import React from 'react';
import fridge from '../assets/fridge.png';
import notification from '../assets/notification.png';
import apple from '../assets/apple.png';
import sparkles from '../assets/sparkles.png';
import note from '../assets/note.png';
import chupaChups from '../assets/chupa_chups.png';
import hamburger from '../assets/hamburger.png';
import shadow from '../assets/shadow.png';

const FloatingShapes = () => {
  const shapes = [
    { id: 1, img: sparkles, alt: 'FridgeMate', className: 'floating-sparkles' },
    { id: 2, img: hamburger, alt: 'FridgeMate', className: 'floating-hamburger' },
    { id: 3, img: fridge, alt: 'FridgeMate', className: 'floating-fridge' },
    { id: 4, img: notification, alt: 'FridgeMate', className: 'floating-notification' },
    { id: 5, img: apple, alt: 'FridgeMate', className: 'floating-apple' },
    { id: 6, img: note, alt: 'FridgeMate', className: 'floating-note' },
    { id: 7, img: chupaChups, alt: 'FridgeMate', className: 'floating-chupa-chups' },
  ];
  return (
    <div className="splash-container">
      <img
        src={shadow}
        alt='FridgeMate'
        className='floating-shadow'
      />
      {shapes.map(shape => (
        <img
          key={shape.id}
          src={shape.img}
          alt={shape.alt}
          className={`floating-shape ${shape.className}`}
        />
      ))}
    </div>
  );
};

export default FloatingShapes;
