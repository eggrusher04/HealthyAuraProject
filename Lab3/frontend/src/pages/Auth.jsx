import React, { useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import { useNavigate } from "react-router-dom";

export default function Auth() {
  const { signIn, signUp } = useAuth();
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState({ username: "", email: "", password: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      if (mode === "login") {
        await signIn(form.username, form.password);
      } else {
        await signUp({
          username: form.username,
          email: form.email,
          password: form.password,
        });
      }
      navigate("/"); // redirect to home
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="max-w-sm mx-auto mt-10 bg-white p-6 rounded-lg shadow">
      <h2 className="text-xl font-semibold mb-4 text-center text-green-700">
        {mode === "login" ? "Sign In" : "Sign Up"}
      </h2>
      <form onSubmit={handleSubmit} className="space-y-3">
        <input
          type="text"
          placeholder="Username"
          className="w-full border p-2 rounded"
          value={form.username}
          onChange={(e) => setForm({ ...form, username: e.target.value })}
          required
        />
        {mode === "signup" && (
          <input
            type="email"
            placeholder="Email"
            className="w-full border p-2 rounded"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            required
          />
        )}
        <input
          type="password"
          placeholder="Password"
          className="w-full border p-2 rounded"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          required
        />
        {error && <div className="text-red-500 text-sm">{error}</div>}
        <button
          type="submit"
          className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700"
        >
          {mode === "login" ? "Login" : "Sign Up"}
        </button>
      </form>

      <div className="text-center mt-3 text-sm">
        {mode === "login" ? (
          <span>
            Don’t have an account?{" "}
            <button
              onClick={() => setMode("signup")}
              className="text-green-700 underline"
            >
              Sign Up
            </button>
          </span>
        ) : (
          <span>
            Already have an account?{" "}
            <button
              onClick={() => setMode("login")}
              className="text-green-700 underline"
            >
              Sign In
            </button>
          </span>
        )}
      </div>
    </div>
  );
}
