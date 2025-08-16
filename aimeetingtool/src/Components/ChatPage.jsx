import React, { useEffect, useState } from "react";
import "./ChatPage.css";

function ChatPage() {
  const [conversations, setConversations] = useState([]); // sidebar list
  const [activeChatId, setActiveChatId] = useState(null); // selected chat
  const [messages, setMessages] = useState([]); // messages in selected chat
  const [prompt, setPrompt] = useState(""); // input text
  const [file, setFile] = useState(null); // PDF/DOCX file
  const [model, setModel] = useState("openai/gpt-oss-20b"); // default model

  const jwt = localStorage.getItem("jwt");

  // Load all conversations when page loads
  useEffect(() => {
    fetch("http://localhost:8080/user/getallmsgs", {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    })
      .then((res) => res.json())
      .then((data) => setConversations(data))
      .catch((err) => console.error(err));
  }, [jwt]);

  // Load messages for selected chat
  const loadMessages = (chatId) => {
    setActiveChatId(chatId);
    fetch(`http://localhost:8080/user/getmsg?id=${chatId}`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    })
      .then((res) => res.json())
      .then((data) => setMessages(data.messages || []))
      .catch((err) => console.error(err));
  };

  // Send text / pdf / docx
  const handleSend = async () => {
    if (!prompt && !file) return;

    try {
      let response;
      if (file) {
        const formData = new FormData();
        formData.append("id", activeChatId || ""); // "" = new chat
        formData.append("prompt", prompt);
        formData.append("model", model);
        formData.append(file.type.includes("pdf") ? "pdf" : "docx", file);

        const url = file.type.includes("pdf")
          ? "http://localhost:8080/user/sendtranscripterpdf"
          : "http://localhost:8080/user/sendtranscripterdocx";

        response = await fetch(url, {
          method: "POST",
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
          body: formData,
        });
      } else {
        response = await fetch("http://localhost:8080/user/sendtranscriptertext?id=" + (activeChatId || ""), {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwt}`,
          },
          body: JSON.stringify(prompt),
        });
      }

      if (response.ok) {
        const data = await response.text();
        setMessages((prev) => [...prev, { role: "user", content: prompt }, { role: "assistant", content: data }]);
        setPrompt("");
        setFile(null);
      } else {
        console.error("Failed to send:", await response.text());
      }
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="chat-container">
      {/* Sidebar */}
      <div className="sidebar">
        <h3>Conversations</h3>
        <button onClick={() => setActiveChatId(null)}>+ New Chat</button>
        <ul>
          {conversations.map((c) => (
            <li
              key={c.id}
              className={activeChatId === c.id ? "active" : ""}
              onClick={() => loadMessages(c.id)}
            >
              Chat {c.id}
            </li>
          ))}
        </ul>
      </div>

      {/* Chat Window */}
      <div className="chat-window">
        <div className="messages">
          {messages.map((m, i) => (
            <div key={i} className={`message ${m.role}`}>
              <strong>{m.role}:</strong> {m.content}
            </div>
          ))}
        </div>

        {/* Controls */}
        <div className="chat-controls">
          <select value={model} onChange={(e) => setModel(e.target.value)}>
            <option value="openai/gpt-oss-20b">openai/gpt-oss-20b</option>
            <option value="deepseek-r1-distill-llama-70b">deepseek-r1-distill-llama-70b</option>
          </select>
          <input
            type="text"
            value={prompt}
            placeholder="Type your prompt..."
            onChange={(e) => setPrompt(e.target.value)}
          />
          <input
            type="file"
            accept=".pdf,.docx"
            onChange={(e) => setFile(e.target.files[0])}
          />
          <button onClick={handleSend}>Send</button>
        </div>
      </div>
    </div>
  );
}

export default ChatPage;
