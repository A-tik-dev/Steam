import React from 'react';

interface PaginationProps {
  offset: number;
  limit: number;
  totalVisible: number;
  loading: boolean;
  onPrevious: () => void;
  onNext: () => void;
}

const Pagination: React.FC<PaginationProps> = ({
  offset,
  limit,
  totalVisible,
  loading,
  onPrevious,
  onNext,
}) => {
  return (
    <div className="pagination">
      <button onClick={onPrevious} disabled={offset === 0 || loading}>
        Previous
      </button>
      <span className="page-info">
        Showing {offset + 1} - {offset + totalVisible}
      </span>
      <button onClick={onNext} disabled={loading || totalVisible < limit}>
        Next
      </button>
    </div>
  );
};

export default Pagination;
