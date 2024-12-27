import React from 'react';

const Note = ({ note, setNote, items, setItems }) => {
  const handleChange = (event) => {
    setNote(event.target.value);
  };

  const handleEntry = () => {
    if (note === '' || note.includes('\n') || note.length > 20) return;
    const newItem = { id: items.length + 1, name: note, bought: false };
    setItems((prevItems) => [...prevItems, newItem]);
    let savedItems = JSON.parse(localStorage.getItem('product-list')) || [];
    savedItems.push(newItem);
    localStorage.setItem('product-list', JSON.stringify(savedItems));
    setNote('');
  };

  return (
    <div className="note-container">
      <textarea
        className="note-textarea"
        value={note}
        onChange={handleChange}
        placeholder="Введите текст заметки..."
        rows="8"
      />
      <button className="light-button" onClick={handleEntry}>Добавить запись</button>
    </div>
  );
};

export default Note;
