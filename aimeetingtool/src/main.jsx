import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import LandingPage from "./Components/LandingPage";
import RegisterPage from "./Components/RegisterPage";
import LoginPage from "./Components/LoginPage";
import ChatPage from "./Components/ChatPage";
ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
          <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/chat" element={<ChatPage/>}/>
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);
