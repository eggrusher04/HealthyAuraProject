import React, { useState } from "react";
import Layout from "../components/Layout";
import { useAuth } from "../contexts/AuthContext";

export default function Profile() {
  const { user, signOut } = useAuth();

  // Seed form from user (fallbacks for demo)
  const [form, setForm] = useState({
    username: user?.username || "Alex P.",
    email: user?.email || "user@email.com",
    phone: user?.phone || "",
    diet: user?.diet || "",           // simple preference text for now
    password: "",                     // for change-password flow (optional)
  });

  const [editingInfo, setEditingInfo] = useState(false);
  const [editingPrefs, setEditingPrefs] = useState(false);
  const [showPwd, setShowPwd] = useState(false);

  const onSaveInfo = () => {
    // TODO: call backend to persist; for now just demo alert
    setEditingInfo(false);
    alert("Personal information saved (demo).");
  };

  const onSavePrefs = () => {
    setEditingPrefs(false);
    alert("Preferences saved (demo).");
  };

  return (
    <Layout>
      {/* Header card */}
      <div className="bg-white rounded-2xl shadow overflow-hidden mb-6">
        <div className="h-24 bg-gradient-to-r from-green-600 to-emerald-500" />
        <div className="flex flex-col items-center -mt-10 pb-5 px-5">
          <div className="bg-white p-3 rounded-full shadow">
            {/* avatar */}
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-16 w-16 text-green-600"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
                d="M15.75 7.5a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.5 19.5a7.5 7.5 0 0115 0" />
            </svg>
          </div>

          <h2 className="mt-3 text-xl font-bold text-gray-900">{form.username}</h2>
          <p className="text-gray-600 text-sm">{form.email}</p>

          <div className="flex gap-3 mt-3">
            <div className="px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm">
              🪙 0 Points
            </div>
            <div className="px-3 py-1 bg-emerald-50 text-emerald-700 rounded-full text-sm">
              ✅ Profile Active
            </div>
          </div>
        </div>
      </div>

      {/* Personal Information */}
      <div className="bg-white rounded-2xl shadow p-5 mb-6">
        <div className="flex items-center justify-between mb-3">
          <h3 className="text-lg font-semibold text-gray-900">Personal Information</h3>
          {!editingInfo ? (
            <button
              onClick={() => setEditingInfo(true)}
              className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-green-700"
            >
              Edit
            </button>
          ) : (
            <div className="flex gap-2">
              <button
                onClick={onSaveInfo}
                className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-green-700"
              >
                Save
              </button>
              <button
                onClick={() => {
                  setEditingInfo(false);
                  // reset view values from user if you want strict cancel
                }}
                className="px-4 py-2 rounded-lg text-sm border"
              >
                Cancel
              </button>
            </div>
          )}
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {/* Username */}
          <div>
            <label className="block text-xs text-gray-500 mb-1">Username</label>
            <input
              disabled={!editingInfo}
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              className={`w-full p-2 rounded-lg border ${editingInfo ? "bg-white" : "bg-gray-50"}`}
            />
          </div>

          {/* Email */}
          <div>
            <label className="block text-xs text-gray-500 mb-1">Email</label>
            <input
              type="email"
              disabled={!editingInfo}
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              className={`w-full p-2 rounded-lg border ${editingInfo ? "bg-white" : "bg-gray-50"}`}
            />
          </div>

          {/* Phone */}
          <div>
            <label className="block text-xs text-gray-500 mb-1">Phone</label>
            <input
              disabled={!editingInfo}
              value={form.phone}
              onChange={(e) => setForm({ ...form, phone: e.target.value })}
              className={`w-full p-2 rounded-lg border ${editingInfo ? "bg-white" : "bg-gray-50"}`}
            />
          </div>

          {/* Password (change) */}
          <div>
            <label className="block text-xs text-gray-500 mb-1">Password</label>
            <div className="flex gap-2">
              <input
                type={showPwd ? "text" : "password"}
                placeholder={editingInfo ? "Enter new password (optional)" : "••••••••"}
                disabled={!editingInfo}
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                className={`w-full p-2 rounded-lg border ${editingInfo ? "bg-white" : "bg-gray-50"}`}
              />
              <button
                type="button"
                onClick={() => setShowPwd((s) => !s)}
                className="px-3 rounded-lg border text-sm"
              >
                {showPwd ? "Hide" : "Show"}
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Preferences (simple text for now) */}
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
                onClick={onSavePrefs}
                className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-green-700"
              >
                Save
              </button>
              <button
                onClick={() => setEditingPrefs(false)}
                className="px-4 py-2 rounded-lg text-sm border"
              >
                Cancel
              </button>
            </div>
          )}
        </div>

        <label className="block text-xs text-gray-500 mb-1">Dietary / Notes</label>
        <input
          disabled={!editingPrefs}
          value={form.diet}
          onChange={(e) => setForm({ ...form, diet: e.target.value })}
          placeholder="e.g., Halal, Vegetarian, Low sugar"
          className={`w-full p-2 rounded-lg border ${editingPrefs ? "bg-white" : "bg-gray-50"}`}
        />
      </div>

      {/* Danger & Sign out */}
      <div className="flex items-center justify-between">
        <button
          onClick={signOut}
          className="px-4 py-2 rounded-lg border text-sm"
        >
          Sign out
        </button>
        <button
          className="px-4 py-2 rounded-lg text-sm bg-red-50 text-red-600 border border-red-200"
          onClick={() => alert("Delete account (demo placeholder).")}
        >
          Delete Account
        </button>
      </div>
    </Layout>
  );
}
