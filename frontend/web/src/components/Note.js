import React from 'react';

const Note = ({ note, setNote, items, setItems }) => {
  const handleChange = (event) => {
    setNote(event.target.value);
  };

  const handleEntry = () => {
    if (note === '' || note.includes('\n') || note.length > 20) return;
    setItems((prevItems) => [...prevItems, { id: note.length + 1, name: note, bought: false }]);
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
