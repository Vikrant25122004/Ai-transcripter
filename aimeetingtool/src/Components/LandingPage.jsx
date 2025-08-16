import React from "react";
import "./LandingPage.css";
import { Link } from "react-router-dom";

function LandingPage() {
  return (
    <div className="landing-container">
      <header className="landing-header">
        <h1>AI Meeting Transcripter</h1>
        <p>Summarize and share your meeting transcripts with AI</p>
      </header>

      <main className="landing-main">
        <p className="tagline">
          Upload your transcript, generate smart summaries, and share them instantly.
        </p>
        
        <div className="button-group">
        
          <Link to="/register">
            <button className="secondary-btn">Register</button>
          </Link>
          <Link to="/login">
            <button className="secondary-btn">Login</button>
          </Link>
        </div>
      </main>

      <footer className="landing-footer">
        <p>© {new Date().getFullYear()} AI Transcripter. All rights reserved.</p>
      </footer>
    </div>
  );
}

export default LandingPage;
