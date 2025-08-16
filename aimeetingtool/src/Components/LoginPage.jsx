import React, { useState } from "react";
import "./Auth.css";
import { Navigate, useNavigate} from "react-router-dom";


function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const Navigate = useNavigate();



  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch("http://localhost:8080/public/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (response.ok) {
        const data = await response.text();
        console.log(data.body);
        localStorage.setItem("jwt",data);
        setMessage("✅ Login successful! Welcome " + (data.email || ""));
        // Optionally store JWT/token in localStorage here
        // localStorage.setItem("token", data.token);
        Navigate("/chat")
        
      } else {
        const errorText = await response.text();
        setMessage("❌ Login failed: " + errorText);
      }
    } catch (error) {
      setMessage("⚠️ Error: " + error.message);
    }
  };

  return (
    <div className="auth-container">
      <h2>Login</h2>
      <form onSubmit={handleLogin} className="auth-form">
        <input
          type="email"
          placeholder="Enter email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Enter password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Login</button>
      </form>
      {message && <p className="auth-message">{message}</p>}
    </div>
  );
}

export default LoginPage;
