import React from "react";
import {
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

// Component for the sentiment distribution pie chart
export const SentimentPieChart = ({ data }) => {
  const COLORS = ["#4CAF50", "#2196F3", "#F44336"]; // positive, neutral, negative

  const pieData = [
    { name: "Positive", value: data.positive || 0 },
    { name: "Neutral", value: data.neutral || 0 },
    { name: "Negative", value: data.negative || 0 },
  ];

  return (
    <div className="chart-container">
      <h3>Sentiment Distribution</h3>
      <ResponsiveContainer width="100%" height={300}>
        <PieChart>
          <Pie
            data={pieData}
            cx="50%"
            cy="50%"
            labelLine={false}
            outerRadius={80}
            fill="#8884d8"
            dataKey="value"
            label={({ name, percent }) => {
              // Only show label if the segment is visible enough
              if (percent < 0.05) return null;
              return `${name}: ${(percent * 100).toFixed(0)}%`;
            }}
          >
            {pieData.map((entry, index) => (
              <Cell
                key={`cell-${index}`}
                fill={COLORS[index % COLORS.length]}
              />
            ))}
          </Pie>
          <Tooltip formatter={(value) => [`${value}`, "Count"]} />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
};

// Component for sentiment trend over time
export const SentimentTrendChart = ({ feedbackData }) => {
  // Process data to create trend data
  const processTrendData = () => {
    if (!feedbackData || feedbackData.length === 0) return [];

    // Group feedback by date
    const groupedByDate = {};

    feedbackData.forEach((item) => {
      const date = new Date(item.createdAt).toLocaleDateString();

      if (!groupedByDate[date]) {
        groupedByDate[date] = {
          date,
          positive: 0,
          neutral: 0,
          negative: 0,
          total: 0,
        };
      }

      if (item.sentimentLabel) {
        groupedByDate[date][item.sentimentLabel]++;
      }

      groupedByDate[date].total++;
    });

    // Convert to array and sort by date
    return Object.values(groupedByDate).sort(
      (a, b) => new Date(a.date) - new Date(b.date)
    );
  };

  const trendData = processTrendData();

  return (
    <div className="chart-container">
      <h3>Sentiment Trend Over Time</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart
          data={trendData}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="date" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line
            type="monotone"
            dataKey="positive"
            stroke="#4CAF50"
            activeDot={{ r: 8 }}
          />
          <Line type="monotone" dataKey="neutral" stroke="#2196F3" />
          <Line type="monotone" dataKey="negative" stroke="#F44336" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

// Component for sentiment score distribution chart
export const SentimentScoreChart = ({ feedbackData }) => {
  // Process data to create sentiment score distribution
  const processScoreData = () => {
    if (!feedbackData || feedbackData.length === 0) return [];

    // Group scores into bins
    const scoreBins = {
      "0.0-0.2": 0,
      "0.2-0.4": 0,
      "0.4-0.6": 0,
      "0.6-0.8": 0,
      "0.8-1.0": 0,
    };

    feedbackData.forEach((item) => {
      if (item.sentimentScore !== undefined && item.sentimentScore !== null) {
        const score = item.sentimentScore;
        if (score < 0.2) scoreBins["0.0-0.2"]++;
        else if (score < 0.4) scoreBins["0.2-0.4"]++;
        else if (score < 0.6) scoreBins["0.4-0.6"]++;
        else if (score < 0.8) scoreBins["0.6-0.8"]++;
        else scoreBins["0.8-1.0"]++;
      }
    });

    return Object.entries(scoreBins).map(([range, count]) => ({
      range,
      count,
    }));
  };

  const scoreData = processScoreData();

  return (
    <div className="chart-container">
      <h3>Sentiment Score Distribution</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart
          data={scoreData}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="range" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Bar dataKey="count" name="Number of Feedbacks" fill="#8884d8" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

// Main component that combines all charts
const SentimentChartDashboard = ({ feedbackData }) => {
  // Calculate sentiment counts
  const sentimentCounts = {
    positive: feedbackData.filter((item) => item.sentimentLabel === "positive")
      .length,
    neutral: feedbackData.filter((item) => item.sentimentLabel === "neutral")
      .length,
    negative: feedbackData.filter((item) => item.sentimentLabel === "negative")
      .length,
  };

  return (
    <div className="charts-dashboard">
      <div className="charts-row">
        <SentimentPieChart data={sentimentCounts} />
        <SentimentScoreChart feedbackData={feedbackData} />
      </div>
      <div className="charts-row">
        <SentimentTrendChart feedbackData={feedbackData} />
      </div>
    </div>
  );
};

export default SentimentChartDashboard;
