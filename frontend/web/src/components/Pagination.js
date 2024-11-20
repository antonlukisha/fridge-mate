import React from 'react';

const Pagination = ({ totalPages, currentPage, setCurrentPage, entries, setEntries, totalResults }) => {
  const handleEntriesChange = (event) => {
    setEntries(event.target.value);
    setCurrentPage(1);
  };

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  return (
    <div className="pagination">
      <div className="entries">
        <span>Показать</span>
        <select
          value={entries}
          onChange={handleEntriesChange}
          className="dropdown"
        >
          <option value="5">5</option>
          <option value="10">10</option>
          <option value="50">50</option>
        </select>
        <span> из { totalResults } результатов</span>
      </div>
      <div className="page-numbers">
        {Array.from({ length: totalPages }, (_, index) => (
          <a
            key={index}
            href="#"
            className={currentPage === index + 1 ? 'active' : ''}
            onClick={() => handlePageChange(index + 1)}
          >
            {index + 1}
          </a>
        ))}
      </div>
    </div>
  );
};

export default Pagination;
