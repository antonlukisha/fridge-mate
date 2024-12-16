import React from 'react';
import sparkles from '../assets/sparkles.png';
import note from '../assets/note.png';
import shadow from '../assets/shadow.png';

const SplashAccount = () => {
  const shapes = [
    { id: 1, img: sparkles, alt: 'FridgeMate', className: 'floating-sparkles' },
    { id: 2, img: note, alt: 'FridgeMate', className: 'floating-note-main' },
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

export default SplashAccount;
