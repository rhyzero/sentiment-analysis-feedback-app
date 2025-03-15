import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { submitFeedback } from "../services/feedbackService";
import Loading from "../components/Loading";
import ErrorMessage from "../components/ErrorMessage";
import "./FeedbackForm.css";

const FeedbackForm = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    text: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === "rating" ? parseInt(value, 10) : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await submitFeedback(formData);
      setSuccess(true);
      setFormData({
        text: "",
      });
      // Redirect to dashboard after 2 seconds
      setTimeout(() => {
        navigate("/");
      }, 2000);
    } catch (err) {
      setError(
        "An error occurred while submitting feedback. Please try again."
      );
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="feedback-form-container">
      <h1>Submit Feedback</h1>
      <p>
        Please share your experience with us. Your feedback helps us improve!
      </p>

      {success && (
        <div className="success-message">
          Thank you for your feedback! Redirecting to dashboard...
        </div>
      )}

      {error && <ErrorMessage message={error} />}

      <form onSubmit={handleSubmit} className="feedback-form">
        <div className="form-group">
          <label htmlFor="text">Your Feedback</label>
          <textarea
            id="text"
            name="text"
            value={formData.text}
            onChange={handleChange}
            rows="5"
            placeholder="Please share your thoughts, suggestions, or concerns..."
            required
          ></textarea>
        </div>

        <button type="submit" disabled={loading}>
          {loading ? "Submitting..." : "Submit Feedback"}
        </button>

        {loading && <Loading message="Submitting your feedback..." />}
      </form>
    </div>
  );
};

export default FeedbackForm;
