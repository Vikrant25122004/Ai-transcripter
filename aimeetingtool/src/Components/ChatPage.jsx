import React, { useState } from "react";
import "./ChatPage.css";
import ReactMarkdown from "react-markdown";

function ChatPage() {
  const [activeChatId, setActiveChatId] = useState(null); // current conversation ID
  const [messages, setMessages] = useState([]); // chat messages
  const [prompt, setPrompt] = useState(""); // input text
  const [model, setModel] = useState("openai/gpt-oss-20b"); // default model
  const [emailTo, setEmailTo] = useState(""); // recipient email

  const jwt = localStorage.getItem("jwt");

  // Send text only with conversation ID
  const handleSend = async () => {
    if (!prompt) return;

    try {
      const response = await fetch(
        `http://localhost:8080/user/sendtranscriptertext?id=${activeChatId || ""}&model=${model}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwt}`,
          },
          body: JSON.stringify(prompt),
        }
      );

      if (response.ok) {
        const data = await response.json();
        const conversationId = data[0];
        const apiResponseString = data[1];

        let apiResponseJson = {};
        try {
          apiResponseJson = JSON.parse(apiResponseString);
        } catch (err) {
          console.error("Failed to parse API response JSON:", err);
        }

        const assistantMessage =
          apiResponseJson.choices?.[0]?.message?.content || "No response";

        setActiveChatId(conversationId);

        setMessages((prev) => [
          ...prev,
          { role: "user", content: prompt },
          { role: "assistant", content: assistantMessage },
        ]);
        setPrompt("");
      } else {
        console.error("Failed to send:", await response.text());
      }
    } catch (err) {
      console.error(err);
    }
  };

  // Start a new chat
  const handleNewChat = () => {
    setActiveChatId(null);
    setMessages([]);
    setPrompt("");
  };

  // Mailto email sender
  const handleMailto = (email, subject, body) => {
    if (!email) {
      alert("Please enter recipient email first.");
      return;
    }
    const mailtoUrl = `mailto:${encodeURIComponent(email)}?subject=${encodeURIComponent(
      subject
    )}&body=${encodeURIComponent(body)}`;
    window.location.href = mailtoUrl;
  };

  // Logout functionality
  const handleLogout = () => {
    localStorage.removeItem("jwt");
    // Redirect to login page or home
    window.location.href = "/"; // adjust path as needed
  };

  return (
    <div className="chat-page">
      {/* Top bar with Logout and New Chat buttons */}
      <div className="top-bar" style={{ display: "flex", justifyContent: "flex-end", padding: "12px 24px", backgroundColor: "#f3f4f6" }}>
        <button
          onClick={handleNewChat}
          className="send-button"
          style={{ backgroundColor: "#10b981", borderRadius: "20px", marginRight: "12px" }}
        >
          New Chat
        </button>
        <button
          onClick={handleLogout}
          className="send-button"
          style={{ backgroundColor: "#ef4444", borderRadius: "20px" }}
        >
          Logout
        </button>
      </div>

      {/* Main chat area */}
      <section className="chat-area">
        <div className="messages-container">
          {messages.map((msg, idx) => (
            <div
              key={idx}
              className={`message ${msg.role === "assistant" ? "assistant" : "user"}`}
            >
              {msg.role === "assistant" ? (
                <>
                  <ReactMarkdown>{msg.content}</ReactMarkdown>
                  <button
                    onClick={() =>
                      handleMailto(emailTo, "Chat Response", msg.content)
                    }
                    className="email-send-button"
                  >
                    Send this as Email
                  </button>
                </>
              ) : (
                msg.content
              )}
            </div>
          ))}
        </div>

        {/* Input area */}
        <div className="input-area">
          <textarea
            placeholder="Type your message here..."
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            rows={3}
            style={{ width: "100%" }}
          />

          <input
            type="email"
            placeholder="Recipient Email"
            value={emailTo}
            onChange={(e) => setEmailTo(e.target.value)}
            style={{
              marginLeft: "12px",
              padding: "6px 8px",
              borderRadius: "6px",
              border: "1px solid #ccc",
              width: "220px",
              flexShrink: 0,
            }}
          />

          <select
            value={model}
            onChange={(e) => setModel(e.target.value)}
            className="model-select"
            style={{ marginLeft: "12px" }}
          >
            <option value="openai/gpt-oss-20b">GPT OSS 20B</option>
            {/* Add more models if needed */}
          </select>

          <button
            onClick={handleSend}
            disabled={!prompt}
            className="send-button"
            style={{ marginLeft: "12px" }}
          >
            Send
          </button>
        </div>
      </section>
    </div>
  );
}

export default ChatPage;
