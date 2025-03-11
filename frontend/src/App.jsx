import React from "react";
import { Outlet, Link } from "react-router-dom";
import "./App.css";

function App() {
  return (
    <div className="app">
      <header>
        <h1>Sentiment Analysis Feedback App</h1>
        <nav>
          <Link to="/">Dashboard</Link> |
          <Link to="/submit">Submit Feedback</Link>
        </nav>
      </header>

      <main>
        <Outlet />
      </main>

      <footer>
        <p>© 2025 Sentiment Analysis Feedback App</p>
      </footer>
    </div>
  );
}

export default App;
