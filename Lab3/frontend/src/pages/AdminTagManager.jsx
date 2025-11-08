import React, { useState } from "react";
import axios from "axios";

/**
 * Admin Tag Management Panel
 *
 * <p>The `AdminTagManager` component provides administrators with full CRUD operations
 * for managing **dietary tags** associated with specific eateries. These tags
 * are used to describe health-related attributes of food options
 * (e.g. "Low Sodium", "High Protein", "Vegetarian").</p>
 *
 * <p>Accessible only to admins via JWT-secured endpoints, this panel interacts with
 * the backend `/api/eateries/{id}/tags` endpoints to:
 * <ul>
 *   <li>Retrieve the list of existing tags for a given eatery</li>
 *   <li>Add new tags dynamically</li>
 *   <li>Rename existing tags</li>
 *   <li>Delete tags from an eatery record</li>
 * </ul>
 * </p>
 *
 * <p>Each modification updates the database through the HealthyAura Spring Boot backend
 * and provides visual feedback for administrative confirmation.</p>
 *
 * @component
 * @example
 * // Example route setup for admin access
 * <Route path="/admin/tags" element={<AdminTagManager />} />
 *
 * @returns {JSX.Element} A full-page interface for tag creation, renaming, and deletion.
 *
 * @since 2025-11-07
 * @version 1.0
 */
export default function AdminTagManager() {
  const token = localStorage.getItem("token");

  // === State Management ===
  const [eateryId, setEateryId] = useState("");
  const [tags, setTags] = useState([]);
  const [newTag, setNewTag] = useState("");
  const [oldTag, setOldTag] = useState("");
  const [renameTo, setRenameTo] = useState("");
  const [feedback, setFeedback] = useState("");

  /**
   * Fetches all dietary tags associated with the specified eatery.
   *
   * <p>Triggers a GET request to `/api/eateries/{eateryId}` and updates
   * the tag list displayed in the UI. Requires a valid JWT token.</p>
   *
   * @async
   * @returns {Promise<void>}
   */
  const fetchTags = async () => {
    if (!eateryId) return alert("Enter an eatery ID first");
    try {
      const res = await axios.get(`http://localhost:8080/api/eateries/${eateryId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setTags(res.data.dietaryTags || []);
      setFeedback(`Loaded ${res.data.dietaryTags?.length || 0} tags.`);
    } catch (err) {
      console.error(err);
      setFeedback("Failed to load tags.");
    }
  };

  /**
   * Adds a new tag to the specified eatery.
   *
   * <p>Sends a POST request to `/api/eateries/{eateryId}/tags` with the payload
   * `{ tags: [newTag] }`. On success, the UI updates with the newly added tag.</p>
   *
   * @async
   * @returns {Promise<void>}
   */
  const handleAddTag = async () => {
    if (!newTag.trim()) return alert("Please enter a tag name.");
    try {
      const payload = { tags: [newTag.trim()] };

      const response = await axios.post(
        `http://localhost:8080/api/eateries/${eateryId}/tags`,
        payload,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setTags(response.data.tags || [...tags, newTag.trim()]);
      setNewTag("");
      setFeedback(`Tag "${newTag}" added successfully.`);
    } catch (err) {
      console.error("Add tag error:", err.response?.data || err.message);
      setFeedback("Failed to add tag. Please check backend logs.");
    }
  };

  /**
   * Renames an existing tag on a specific eatery.
   *
   * <p>Sends a PUT request to `/api/eateries/{eateryId}/tags`
   * with payload `{ oldTag, newTag }` to update the tag mapping in the backend.</p>
   *
   * @async
   * @returns {Promise<void>}
   */
  const handleRenameTag = async () => {
    const oldVal = (oldTag || "").trim();
    const newVal = (renameTo || "").trim();

    if (!oldVal || !newVal) {
      alert("Please enter both old and new tag names.");
      return;
    }

    try {
      const payload = { oldTag: oldVal, newTag: newVal };
      const res = await axios.put(
        `http://localhost:8080/api/eateries/${eateryId}/tags`,
        payload,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setTags(res.data.dietaryTags || res.data.tags || []);
      setFeedback(`Tag "${oldVal}" renamed to "${newVal}".`);
      setOldTag("");
      setRenameTo("");
    } catch (err) {
      console.error("Rename tag error:", err.response?.data || err.message);
      setFeedback("Failed to rename tag. Please check backend logs.");
    }
  };

  /**
   * Deletes a tag from a specific eatery.
   *
   * <p>Sends a DELETE request to `/api/eateries/{eateryId}/tags/{tag}`
   * to remove the tag from the eatery’s record in the database.</p>
   *
   * @async
   * @param {string} tag - The name of the tag to delete.
   * @returns {Promise<void>}
   */
  const handleDeleteTag = async (tag) => {
    if (!window.confirm(`Delete tag '${tag}'?`)) return;
    try {
      await axios.delete(
        `http://localhost:8080/api/eateries/${eateryId}/tags/${encodeURIComponent(tag)}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setFeedback(`Deleted tag '${tag}'`);
      fetchTags();
    } catch (err) {
      console.error(err);
      setFeedback("Failed to delete tag.");
    }
  };

  // === Render ===
  return (
    <div className="min-h-screen bg-gray-50 py-10 px-6">
      <div className="max-w-3xl mx-auto bg-white rounded-xl shadow p-8">
        <h1 className="text-2xl font-bold mb-4 text-gray-800">
          Admin Tag Management
        </h1>

        {/* Feedback Messages */}
        {feedback && (
          <div className="bg-green-50 text-green-700 p-3 rounded mb-4">
            {feedback}
          </div>
        )}

        {/* === Eatery ID Input and Fetch === */}
        <div className="flex gap-3 mb-6">
          <input
            type="number"
            placeholder="Enter Eatery ID"
            value={eateryId}
            onChange={(e) => setEateryId(e.target.value)}
            className="border p-2 rounded flex-1"
          />
          <button
            onClick={fetchTags}
            className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
          >
            Load Tags
          </button>
        </div>

        {/* === Display Current Tags === */}
        <div className="mb-8">
          <h2 className="text-lg font-semibold mb-2 text-gray-700">
            Current Tags
          </h2>
          {tags.map((t, idx) => (
            <li
              key={t.id || idx}
              className="flex justify-between items-center bg-gray-100 rounded px-3 py-2"
            >
              <span className="text-gray-800">{t.tag}</span>
              <button
                onClick={() => handleDeleteTag(t.tag)}
                className="text-red-600 text-sm hover:underline"
              >
                Delete
              </button>
            </li>
          ))}
        </div>

        {/* === Add Tag Section === */}
        <div className="mb-6">
          <h2 className="text-lg font-semibold mb-2 text-gray-700">Add Tag</h2>
          <div className="flex gap-3">
            <input
              type="text"
              placeholder="New tag name"
              value={newTag}
              onChange={(e) => setNewTag(e.target.value)}
              className="border p-2 rounded flex-1"
            />
            <button
              onClick={handleAddTag}
              className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
            >
              Add
            </button>
          </div>
        </div>

        {/* === Rename Tag Section === */}
        <div>
          <h2 className="text-lg font-semibold mb-2 text-gray-700">Rename Tag</h2>
          <div className="flex flex-wrap gap-3">
            <input
              type="text"
              placeholder="Old tag"
              value={oldTag}
              onChange={(e) => setOldTag(e.target.value)}
              className="border p-2 rounded flex-1"
            />
            <input
              type="text"
              placeholder="New tag name"
              value={renameTo}
              onChange={(e) => setRenameTo(e.target.value)}
              className="border p-2 rounded flex-1"
            />
            <button
              onClick={handleRenameTag}
              className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            >
              Rename
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
