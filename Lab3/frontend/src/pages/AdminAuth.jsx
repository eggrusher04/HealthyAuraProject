import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

/**
 * Admin account creation page for the HealthyAura web platform.
 *
 * <p>The `AdminAuth` component allows existing authenticated administrators to
 * register new admin accounts. It sends a POST request to the backend endpoint
 * `/auth/admin/signup` and requires a valid JWT token from the logged-in admin.</p>
 *
 * <p>Key functionalities include:
 * <ul>
 *   <li>JWT-based authorization for protected admin creation</li>
 *   <li>Form state management for username, email, and password</li>
 *   <li>Client-side validation and error display</li>
 *   <li>Loading feedback during async API requests</li>
 *   <li>Navigation back to the main page using React Router</li>
 * </ul>
 * </p>
 *
 * @component
 * @example
 * // Used for creating admin accounts in the frontend
 * <Route path="/auth/admin/signup" element={<AdminAuth />} />
 *
 * @returns {JSX.Element} The rendered admin signup page with a form and navigation.
 *
 * @since 2025-11-07
 * @version 1.0
 */
export default function AdminAuth() {
  const [form, setForm] = useState({ username: "", email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  /**
   * Handles the submission of the admin creation form.
   *
   * <p>This function validates input fields, sends a `POST` request to the backend
   * to register a new admin, and handles success/error responses. On success,
   * the form resets while keeping the user on the page.</p>
   *
   * @async
   * @param {React.FormEvent} e - The form submission event.
   * @returns {Promise<void>}
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/auth/admin/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(form),
      });

      const data = await response.json();

      if (response.ok) {
        alert(`Admin account "${data.username}" created successfully!`);
        // Reset form but stay on page
        setForm({ username: "", email: "", password: "" });
      } else {
        setError(data.message || "Failed to create admin account");
      }
    } catch (err) {
      setError("Network error. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-sm mx-auto mt-10 bg-white p-6 rounded-lg shadow">
      <h2 className="text-xl font-semibold mb-4 text-center text-green-700">
        Create New Admin
      </h2>
      <p className="text-center text-sm text-gray-600 mb-4">
        Use this form to register a new admin account.
      </p>

      <form onSubmit={handleSubmit} className="space-y-3">
        <input
          type="text"
          placeholder="Username"
          className="w-full border p-2 rounded"
          value={form.username}
          onChange={(e) => setForm({ ...form, username: e.target.value })}
          required
        />

        <input
          type="email"
          placeholder="Email"
          className="w-full border p-2 rounded"
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          required
        />

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
          disabled={loading}
          className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 disabled:opacity-50"
        >
          {loading ? "Creating Admin..." : "Create Admin"}
        </button>
      </form>

      <div className="mt-6">
        <button
          onClick={() => navigate("/")}
          className="w-full py-2 px-4 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
        >
          ← Back to Home
        </button>
      </div>
    </div>
  );
}
