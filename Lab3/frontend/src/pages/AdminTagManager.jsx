import React, { useState } from "react";
import axios from "axios";

export default function AdminTagManager() {
  const token = localStorage.getItem("token");
  const [eateryId, setEateryId] = useState("");
  const [tags, setTags] = useState([]);
  const [newTag, setNewTag] = useState("");
  const [oldTag, setOldTag] = useState("");
  const [renameTo, setRenameTo] = useState("");
  const [feedback, setFeedback] = useState("");

  // Fetch tags for eatery
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

  // Add new tag
  const handleAddTag = async () => {
    if (!newTag.trim()) return alert("Please enter a tag name.");
    try {
      // Always send the exact format backend expects
      const payload = { tags: [newTag.trim()] };

      console.log("Sending payload:", payload);

      const response = await axios.post(
        `http://localhost:8080/api/eateries/${eateryId}/tags`,
        payload,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // Assuming backend returns updated tags array
      setTags(response.data.tags || [...tags, newTag.trim()]);
      setNewTag("");
      setFeedback(`Tag "${newTag}" added successfully.`);
    } catch (err) {
      console.error("Add tag error:", err.response?.data || err.message);
      setFeedback("Failed to add tag. Please check backend logs.");
    }
  };



  // Rename tag
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


  // Delete tag
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

  return (
    <div className="min-h-screen bg-gray-50 py-10 px-6">
      <div className="max-w-3xl mx-auto bg-white rounded-xl shadow p-8">
        <h1 className="text-2xl font-bold mb-4 text-gray-800">
          Admin Tag Management
        </h1>

        {feedback && (
          <div className="bg-green-50 text-green-700 p-3 rounded mb-4">
            {feedback}
          </div>
        )}

        {/* Eatery ID + Fetch */}
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

        {/* Display Tags */}
        <div className="mb-8">
          <h2 className="text-lg font-semibold mb-2 text-gray-700">Current Tags</h2>
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

        {/* Add Tag */}
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

        {/* Rename Tag */}
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