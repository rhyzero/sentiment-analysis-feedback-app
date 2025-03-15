import React, { useState, useEffect } from "react";
import { getFeedbackList } from "../services/feedbackService";
import SentimentChartDashboard from "../components/SentimentChart";
import Loading from "../components/Loading";
import ErrorMessage from "../components/ErrorMessage";
import "./Dashboard.css";

const Dashboard = () => {
  const [feedback, setFeedback] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState({
    sentiment: "all",
    dateRange: "all",
  });

  useEffect(() => {
    const fetchFeedback = async () => {
      try {
        const data = await getFeedbackList();
        setFeedback(data);
        setLoading(false);
      } catch (err) {
        setError("Failed to load feedback data. Please try again later.");
        setLoading(false);
      }
    };

    fetchFeedback();
  }, []);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters({
      ...filters,
      [name]: value,
    });
  };

  const filteredFeedback = feedback.filter((item) => {
    // Apply sentiment filter
    if (
      filters.sentiment !== "all" &&
      item.sentimentLabel !== filters.sentiment
    ) {
      return false;
    }

    // Apply date range filter (simplified for now)
    if (filters.dateRange !== "all") {
      const itemDate = new Date(item.createdAt);
      const now = new Date();

      if (filters.dateRange === "today") {
        const today = new Date(now.setHours(0, 0, 0, 0));
        return itemDate >= today;
      } else if (filters.dateRange === "week") {
        const weekAgo = new Date(now.setDate(now.getDate() - 7));
        return itemDate >= weekAgo;
      } else if (filters.dateRange === "month") {
        const monthAgo = new Date(now.setMonth(now.getMonth() - 1));
        return itemDate >= monthAgo;
      }
    }

    return true;
  });

  // Calculate sentiment distribution for visualization
  const sentimentCounts = {
    positive: filteredFeedback.filter(
      (item) => item.sentimentLabel === "positive"
    ).length,
    neutral: filteredFeedback.filter(
      (item) => item.sentimentLabel === "neutral"
    ).length,
    negative: filteredFeedback.filter(
      (item) => item.sentimentLabel === "negative"
    ).length,
  };

  const getSentimentColor = (sentiment) => {
    switch (sentiment) {
      case "positive":
        return "#4CAF50"; // Green
      case "negative":
        return "#F44336"; // Red
      case "neutral":
      default:
        return "#2196F3"; // Blue
    }
  };

  return (
    <div className="dashboard-container">
      <h1>Feedback Dashboard</h1>

      {error && <div className="error-message">{error}</div>}

      <div className="dashboard-header">
        <div className="filters">
          <div className="filter-group">
            <label htmlFor="sentiment">Sentiment</label>
            <select
              id="sentiment"
              name="sentiment"
              value={filters.sentiment}
              onChange={handleFilterChange}
            >
              <option value="all">All Sentiments</option>
              <option value="positive">Positive</option>
              <option value="neutral">Neutral</option>
              <option value="negative">Negative</option>
            </select>
          </div>

          <div className="filter-group">
            <label htmlFor="dateRange">Date Range</label>
            <select
              id="dateRange"
              name="dateRange"
              value={filters.dateRange}
              onChange={handleFilterChange}
            >
              <option value="all">All Time</option>
              <option value="today">Today</option>
              <option value="week">Last 7 Days</option>
              <option value="month">Last 30 Days</option>
            </select>
          </div>
        </div>
      </div>

      <div className="dashboard-stats">
        <div className="stat-card total">
          <h3>Total Feedback</h3>
          <p className="stat-value">{filteredFeedback.length}</p>
        </div>

        <div className="stat-card positive">
          <h3>Positive</h3>
          <p className="stat-value">{sentimentCounts.positive}</p>
          <p className="stat-percentage">
            {filteredFeedback.length > 0
              ? Math.round(
                  (sentimentCounts.positive / filteredFeedback.length) * 100
                )
              : 0}
            %
          </p>
        </div>

        <div className="stat-card neutral">
          <h3>Neutral</h3>
          <p className="stat-value">{sentimentCounts.neutral}</p>
          <p className="stat-percentage">
            {filteredFeedback.length > 0
              ? Math.round(
                  (sentimentCounts.neutral / filteredFeedback.length) * 100
                )
              : 0}
            %
          </p>
        </div>

        <div className="stat-card negative">
          <h3>Negative</h3>
          <p className="stat-value">{sentimentCounts.negative}</p>
          <p className="stat-percentage">
            {filteredFeedback.length > 0
              ? Math.round(
                  (sentimentCounts.negative / filteredFeedback.length) * 100
                )
              : 0}
            %
          </p>
        </div>
      </div>

      {/* Advanced Visualizations */}
      <SentimentChartDashboard feedbackData={filteredFeedback} />

      {/* Basic Sentiment Distribution (for non-JavaScript fallback) */}
      <div className="sentiment-distribution">
        <h2>Sentiment Distribution</h2>
        <div className="sentiment-bars">
          {["positive", "neutral", "negative"].map((sentiment) => (
            <div key={sentiment} className="sentiment-bar-container">
              <div className="sentiment-label">{sentiment}</div>
              <div className="sentiment-bar-wrapper">
                <div
                  className="sentiment-bar"
                  style={{
                    width: `${
                      filteredFeedback.length
                        ? (sentimentCounts[sentiment] /
                            filteredFeedback.length) *
                          100
                        : 0
                    }%`,
                    backgroundColor: getSentimentColor(sentiment),
                  }}
                ></div>
              </div>
              <div className="sentiment-count">
                {sentimentCounts[sentiment]}
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="feedback-list">
        <h2>Recent Feedback</h2>
        {loading ? (
          <Loading message="Loading feedback data..." />
        ) : error ? (
          <ErrorMessage
            message={error}
            onRetry={() => {
              setLoading(true);
              setError(null);
              getFeedbackList()
                .then((data) => {
                  setFeedback(data);
                  setLoading(false);
                })
                .catch((err) => {
                  setError(
                    "Failed to load feedback data. Please try again later."
                  );
                  setLoading(false);
                });
            }}
          />
        ) : filteredFeedback.length === 0 ? (
          <div className="no-data">
            No feedback available with the selected filters.
          </div>
        ) : (
          <table className="feedback-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Feedback</th>
                <th>Sentiment</th>
                <th>Score</th>
              </tr>
            </thead>
            <tbody>
              {filteredFeedback.map((item) => (
                <tr key={item.id}>
                  <td>{new Date(item.createdAt).toLocaleDateString()}</td>
                  <td className="feedback-cell">{item.text}</td>
                  <td
                    className="sentiment-cell"
                    style={{
                      color: getSentimentColor(item.sentimentLabel),
                    }}
                  >
                    {item.sentimentLabel || "N/A"}
                  </td>
                  <td>
                    {item.sentimentScore
                      ? item.sentimentScore.toFixed(2)
                      : "N/A"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
