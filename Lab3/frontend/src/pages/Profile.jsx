import React, { useEffect, useState } from "react";
import Layout from "../components/Layout";
import { useAuth } from "../contexts/AuthContext";
import API from "../services/api";

/**
 * Profile Page
 *
 * <p>The `Profile` component allows authenticated users to view and update
 * their personal account details. It integrates with the backend API and
 * the AuthContext to manage user data such as:</p>
 *
 * <ul>
 *   <li><b>Email address</b> – editable with backend update</li>
 *   <li><b>Dietary preferences</b> – used for personalized recommendations</li>
 *   <li><b>Password</b> – securely updated via backend endpoint</li>
 *   <li><b>Total Points</b> – reward system placeholder (read-only)</li>
 * </ul>
 *
 * <p>Key Functionalities:</p>
 * <ul>
 *   <li>Uses the <code>useAuth()</code> hook for session context</li>
 *   <li>Synchronizes data between frontend and backend via API</li>
 *   <li>Provides edit/save/cancel interactions for each editable field</li>
 *   <li>Includes fallback behavior if AuthContext lacks complete profile data</li>
 * </ul>
 *
 * <p>Backend Endpoints:</p>
 * <ul>
 *   <li><code>GET /profile/me</code> → Fetch user profile</li>
 *   <li><code>PUT /profile/me</code> → Update dietary preferences</li>
 *   <li><code>PUT /profile/me/email</code> → Update email</li>
 *   <li><code>PUT /profile/me/password</code> → Change password</li>
 * </ul>
 *
 * @component
 * @example
 * // Example route
 * <Route path="/profile" element={<Profile />} />
 *
 * @returns {JSX.Element} User profile page with editable fields.
 *
 * @since 2025-11-07
 * @version 1.0
 */
export default function Profile() {
  const { user, signOut, loadingUser } = useAuth();

  // === State Management ===
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState({ email: "", preferences: "", password: "" });
  const [editingPrefs, setEditingPrefs] = useState(false);
  const [editingEmail, setEditingEmail] = useState(false);
  const [editingPwd, setEditingPwd] = useState(false);
  const [showPwd, setShowPwd] = useState(false);
  const [loading, setLoading] = useState(true);

  /**
   * Fetches the user profile data from either AuthContext or the backend.
   *
   * <p>Steps:
   * <ol>
   *   <li>If `AuthContext` already provides user details, use that directly.</li>
   *   <li>If incomplete, make an API call to `/profile/me` to fetch full profile.</li>
   *   <li>Populates the form state and sets loading to false.</li>
   * </ol>
   * </p>
   *
   * @effect
   * @returns {Promise<void>}
   */
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        // Case 1: User data available from context
        if (user && user.email) {
          setProfile(user);
          setForm({
            email: user.email || "",
            preferences: user.preferences || "",
            password: "",
          });
          setLoading(false);
          return;
        }

        // Case 2: Fallback – Fetch from backend
        const res = await API.get("/profile/me");
        const profileData = res.data;
        setProfile(profileData);
        setForm({
          email: profileData.email || "",
          preferences: profileData.preferences || "",
          password: "",
        });
      } catch (err) {
        console.error("Error fetching profile data:", err);
        if (user) {
          // Minimal fallback if backend fails
          setProfile({
            ...user,
            email: user.email || "Email not found",
            preferences: user.preferences || "No preferences set",
            totalPoints: user.totalPoints ?? 0,
          });
        }
      } finally {
        setLoading(false);
      }
    };

    if (!loadingUser) fetchProfile();
  }, [user, loadingUser]);

  // === Update Handlers ===

  /**
   * Updates user dietary preferences in the backend.
   *
   * @async
   * @returns {Promise<void>}
   */
  const handleSavePrefs = async () => {
    try {
      const res = await API.put("/profile/me", { preferences: form.preferences });
      setProfile(res.data);
      setEditingPrefs(false);
      alert("Preferences updated successfully!");
    } catch (err) {
      console.error(err);
      alert("Failed to update preferences.");
    }
  };

  /**
   * Updates user email in the backend.
   *
   * @async
   * @returns {Promise<void>}
   */
  const handleSaveEmail = async () => {
    try {
      const res = await API.put("/profile/me/email", { email: form.email });
      setProfile((p) => ({ ...p, email: res.data.email }));
      setEditingEmail(false);
      alert("Email updated successfully!");
    } catch (err) {
      console.error(err);
      alert("Failed to update email.");
    }
  };

  /**
   * Updates the user's password.
   *
   * @async
   * @returns {Promise<void>}
   */
  const handleChangePassword = async () => {
    try {
      await API.put("/profile/me/password", { password: form.password });
      setEditingPwd(false);
      setForm((f) => ({ ...f, password: "" }));
      alert("Password changed successfully!");
    } catch (err) {
      console.error(err);
      alert("Failed to change password.");
    }
  };

  // === Conditional Rendering ===
  if (loading || loadingUser) {
    return (
      <Layout>
        <p className="text-center text-gray-500 mt-10">Loading profile...</p>
      </Layout>
    );
  }

  if (!profile) {
    return (
      <Layout>
        <p className="text-center text-red-500 mt-10">
          You must be logged in to view this page.
        </p>
      </Layout>
    );
  }

  // === Render Profile Page ===
  return (

      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* === Header Section === */}
        <div className="bg-white rounded-2xl shadow overflow-hidden mb-6">
          <div className="h-24 bg-gradient-to-r from-green-600 to-emerald-500" />
          <div className="flex flex-col items-center -mt-10 pb-5 px-5">
            <div className="bg-white p-3 rounded-full shadow">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-16 w-16 text-green-600"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M15.75 7.5a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.5 19.5a7.5 7.5 0 0115 0"
                />
              </svg>
            </div>

            <h2 className="mt-3 text-xl font-bold text-gray-900">
              {profile.username}
            </h2>
            <p className="text-gray-600 text-sm">
              {profile.email || "Not set"}
            </p>

            <div className="flex gap-3 mt-3">
              <div className="px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm">
                {profile.totalPoints ?? 0} Points
              </div>
              <div className="px-3 py-1 bg-emerald-50 text-emerald-700 rounded-full text-sm">
                ✅ Profile Active
              </div>
            </div>
          </div>
        </div>

        {/* === Email Editing === */}
        <div className="bg-white rounded-2xl shadow p-5 mb-6">
          <div className="flex items-center justify-between mb-3">
            <h3 className="text-lg font-semibold text-gray-900">Email</h3>
            {!editingEmail ? (
              <button
                onClick={() => setEditingEmail(true)}
                className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-green-700"
              >
                Edit
              </button>
            ) : (
              <div className="flex gap-2">
                <button
                  onClick={handleSaveEmail}
                  className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-green-700"
                >
                  Save
                </button>
                <button
                  onClick={() => {
                    setEditingEmail(false);
                    setForm({ ...form, email: profile.email || "" });
                  }}
                  className="px-4 py-2 rounded-lg text-sm border hover:bg-gray-50"
                >
                  Cancel
                </button>
              </div>
            )}
          </div>

          <input
            disabled={!editingEmail}
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            placeholder="Enter your email"
            className={`w-full p-3 rounded-lg border ${
              editingEmail ? "bg-white border-green-500" : "bg-gray-50 border-gray-200"
            }`}
          />
          {!editingEmail && (profile.email === "" || profile.email === null) && (
            <p className="text-sm text-gray-500 mt-1">No email set yet</p>
          )}
        </div>

        {/* === Preferences Editing === */}
        <div className="bg-white rounded-2xl shadow p-5 mb-6">
          <div className="flex items-center justify-between mb-3">
            <h3 className="text-lg font-semibold text-gray-900">Preferences</h3>
            {!editingPrefs ? (
              <button
                onClick={() => setEditingPrefs(true)}
                className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-green-700"
              >
                Edit
              </button>
            ) : (
              <div className="flex gap-2">
                <button
                  onClick={handleSavePrefs}
                  className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-green-700"
                >
                  Save
                </button>
                <button
                  onClick={() => {
                    setEditingPrefs(false);
                    setForm({ ...form, preferences: profile.preferences || "" });
                  }}
                  className="px-4 py-2 rounded-lg text-sm border hover:bg-gray-50"
                >
                  Cancel
                </button>
              </div>
            )}
          </div>

          <input
            disabled={!editingPrefs}
            value={form.preferences}
            onChange={(e) => setForm({ ...form, preferences: e.target.value })}
            placeholder="e.g., Halal, Vegetarian, Low sugar"
            className={`w-full p-3 rounded-lg border ${
              editingPrefs ? "bg-white border-green-500" : "bg-gray-50 border-gray-200"
            }`}
          />
          {!editingPrefs && (profile.preferences === "" || profile.preferences === null) && (
            <p className="text-sm text-gray-500 mt-1">No preferences set</p>
          )}
        </div>

        {/* === Password Editing === */}
        <div className="bg-white rounded-2xl shadow p-5 mb-6">
          <div className="flex items-center justify-between mb-3">
            <h3 className="text-lg font-semibold text-gray-900">Change Password</h3>
            {!editingPwd ? (
              <button
                onClick={() => setEditingPwd(true)}
                className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-green-700"
              >
                Edit
              </button>
            ) : (
              <div className="flex gap-2">
                <button
                  onClick={handleChangePassword}
                  className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-green-700"
                >
                  Save
                </button>
                <button
                  onClick={() => {
                    setEditingPwd(false);
                    setForm((f) => ({ ...f, password: "" }));
                  }}
                  className="px-4 py-2 rounded-lg text-sm border hover:bg-gray-50"
                >
                  Cancel
                </button>
              </div>
            )}
          </div>

          <div className="flex gap-2 items-center">
            <input
              type={showPwd ? "text" : "password"}
              disabled={!editingPwd}
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              placeholder={editingPwd ? "Enter new password" : "••••••••"}
              className={`w-full p-3 rounded-lg border ${
                editingPwd ? "bg-white border-green-500" : "bg-gray-50 border-gray-200"
              }`}
            />
            {editingPwd && (
              <button
                type="button"
                onClick={() => setShowPwd((s) => !s)}
                className="px-3 py-3 border rounded-lg text-sm hover:bg-gray-50"
              >
                {showPwd ? "Hide" : "Show"}
              </button>
            )}
          </div>
        </div>

        {/* === Sign Out Button === */}
        <div className="flex justify-center">
          <button
            onClick={signOut}
            className="px-6 py-3 bg-red-600 text-white rounded-lg text-sm hover:bg-red-700 transition-colors"
          >
            Sign Out
          </button>
        </div>
      </div>
    
  );
}
