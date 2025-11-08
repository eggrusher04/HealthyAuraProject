import React, { useState, useEffect } from "react";
import { useAuth } from "../contexts/AuthContext";
import { useNavigate, useLocation } from "react-router-dom";

/**
 * Authentication Page (Login / Signup)
 *
 * <p>The `Auth` component provides a unified authentication interface for
 * the HealthyAura application. It allows users to sign in or register
 * for a new account through form submission, interacting with the
 * global authentication context (`useAuth`).</p>
 *
 * <p>Core behaviors include:
 * <ul>
 *   <li>Switching between login and signup modes</li>
 *   <li>Handling controlled form inputs for username, email, and password</li>
 *   <li>Submitting credentials through `signIn()` and `signUp()` context methods</li>
 *   <li>Redirecting users to the appropriate page upon success</li>
 *   <li>Handling navigation state for seamless post-signup login transition</li>
 * </ul>
 * </p>
 *
 * <p>This component also ensures a clean UX flow between signup and login
 * screens using React Router’s `useNavigate` and `useLocation` hooks.</p>
 *
 * @component
 * @example
 * // Example usage in your routing file
 * <Route path="/auth" element={<Auth />} />
 *
 * @returns {JSX.Element} A form for user login and registration.
 *
 * @since 2025-11-07
 * @version 1.0
 */
export default function Auth() {
  const { signIn, signUp } = useAuth();
  const [mode, setMode] = useState("login"); // toggles between login/signup
  const [form, setForm] = useState({ username: "", email: "", password: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const location = useLocation();

  /**
   * Handles redirection from the signup flow.
   *
   * <p>If the user has just completed signup, automatically switches
   * the component to login mode to encourage immediate sign-in.</p>
   */
  useEffect(() => {
    if (location.state?.fromSignup) {
      setMode("login");
    }
  }, [location.state]);

  /**
   * Submits the authentication form for either login or signup.
   *
   * <p>On success:
   * <ul>
   *   <li>Login: Redirects to the home page ("/")</li>
   *   <li>Signup: Redirects back to the login form</li>
   * </ul>
   * </p>
   *
   * @async
   * @param {React.FormEvent} e - The form submission event.
   * @returns {Promise<void>}
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      if (mode === "login") {
        await signIn(form.username, form.password);
        navigate("/");
      } else {
        await signUp({
          username: form.username,
          email: form.email,
          password: form.password,
        });
        navigate("/auth", { state: { fromSignup: true } });
      }
    } catch (err) {
      setError(err.message);
    }
  };

  // === Render ===
  return (
    <div className="max-w-sm mx-auto mt-10 bg-white p-6 rounded-lg shadow">
      <h2 className="text-xl font-semibold mb-4 text-center text-green-700">
        {mode === "login" ? "Sign In" : "Sign Up"}
      </h2>

      {/* === Authentication Form === */}
      <form onSubmit={handleSubmit} className="space-y-3">
        {/* Username */}
        <input
          type="text"
          placeholder="Username"
          className="w-full border p-2 rounded"
          value={form.username}
          onChange={(e) => setForm({ ...form, username: e.target.value })}
          required
        />

        {/* Email (only for signup) */}
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

        {/* Password */}
        <input
          type="password"
          placeholder="Password"
          className="w-full border p-2 rounded"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          required
        />

        {/* Error Message */}
        {error && <div className="text-red-500 text-sm">{error}</div>}

        {/* Submit Button */}
        <button
          type="submit"
          className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700"
        >
          {mode === "login" ? "Login" : "Sign Up"}
        </button>
      </form>

      {/* === Mode Toggle === */}
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
