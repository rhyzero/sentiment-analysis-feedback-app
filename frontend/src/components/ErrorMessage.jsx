import React from "react";
import "./ErrorMessage.css";

const ErrorMessage = ({ message, onRetry }) => {
  return (
    <div className="error-container">
      <div className="error-icon">!</div>
      <p className="error-text">
        {message || "An error occurred. Please try again."}
      </p>
      {onRetry && (
        <button className="error-retry-button" onClick={onRetry}>
          Try Again
        </button>
      )}
    </div>
  );
};

export default ErrorMessage;
