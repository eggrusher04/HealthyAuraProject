import React, { useState, useEffect } from "react";
import axios from "axios";

export default function AdminModeration() {
  const token = localStorage.getItem("token");
  const [selectedFlagId, setSelectedFlagId] = useState("");
  const [resolveAction, setResolveAction] = useState("REMOVE");
  const [adminNotes, setAdminNotes] = useState("");
  const [reviewId, setReviewId] = useState("");
  const [hideReason, setHideReason] = useState("");
  const [deleteReason, setDeleteReason] = useState("");
  const [feedback, setFeedback] = useState("");
  const [pendingFlags, setPendingFlags] = useState([]);

  const handleResolveFlag = async () => {
    if (!selectedFlagId) return alert("Please enter a flag ID to resolve.");
    try {
      const url = `http://localhost:8080/admin/review-moderation/flags/${selectedFlagId}/resolve?action=${resolveAction}&notes=${encodeURIComponent(adminNotes)}`;
      await axios.put(url, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setFeedback(`Flag #${selectedFlagId} resolved as ${resolveAction}.`);
    } catch (err) {
      console.error(err);
      setFeedback("Failed to resolve flag.");
    }
  };

  const handleHideReview = async () => {
    if (!reviewId || !hideReason.trim())
      return alert("Review ID and reason are required.");
    try {
      const url = `http://localhost:8080/admin/review-moderation/reviews/${reviewId}/hide?reason=${encodeURIComponent(hideReason)}`;
      await axios.put(url, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setFeedback(`Review #${reviewId} hidden successfully.`);
    } catch (err) {
      console.error(err);
      setFeedback("Failed to hide review.");
    }
  };

  const handleDeleteReview = async () => {
    if (!reviewId || !deleteReason.trim())
      return alert("Review ID and reason are required.");
    try {
      const url = `http://localhost:8080/admin/review-moderation/reviews/${reviewId}?reason=${encodeURIComponent(deleteReason)}`;
      await axios.delete(url, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setFeedback(`Review #${reviewId} deleted successfully.`);
    } catch (err) {
      console.error(err);
      setFeedback("Failed to delete review.");
    }
  };

useEffect(() => {
  fetchResolvedFlags();
}, []);

const fetchResolvedFlags = async () => {
  try {
    const res = await axios.get("http://localhost:8080/admin/dashboard/flags?status=RESOLVED", {
      headers: { Authorization: `Bearer ${token}` },
    });
    setPendingFlags(res.data);
  } catch (err) {
    console.error("Error fetching pending flags:", err);
  }
};


  return (
    <div className="min-h-screen bg-gray-50 py-10 px-6">
      <div className="max-w-4xl mx-auto bg-white shadow-md rounded-xl p-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-6">
          Admin Review Moderation
        </h1>
        <p className="text-sm text-gray-600 mb-8">
          Use this panel to resolve flags, hide, or delete reviews.
        </p>

        {feedback && (
          <div className="bg-gray-100 border-l-4 border-green-500 text-gray-700 p-3 rounded mb-6">
            {feedback}
          </div>
        )}

        {/* Resolve Flag */}
        <section className="mb-8">
          <h2 className="text-lg font-semibold text-gray-800 mb-2">
            Resolve a Flag
          </h2>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
            <input
              type="number"
              placeholder="Flag ID"
              value={selectedFlagId}
              onChange={(e) => setSelectedFlagId(e.target.value)}
              className="p-2 border rounded text-sm"
            />
            <select
              value={resolveAction}
              onChange={(e) => setResolveAction(e.target.value)}
              className="p-2 border rounded text-sm"
            >
              <option value="REMOVE">REMOVE (approve removal)</option>
              <option value="DISMISS">DISMISS (approve review)</option>
            </select>
            <input
              type="text"
              placeholder="Admin notes (e.g. verified content)"
              value={adminNotes}
              onChange={(e) => setAdminNotes(e.target.value)}
              className="p-2 border rounded text-sm"
            />
            <button
              onClick={handleResolveFlag}
              className="px-3 py-2 bg-green-600 text-white rounded-md text-sm hover:bg-green-700"
            >
              Resolve Flag
            </button>
          </div>
          <p className="text-xs text-gray-500 mt-2">
            Resolving marks the flag as handled. Use REMOVE to take further action
            (e.g. hide/delete review), or DISMISS to approve it.
          </p>
        </section>

        {/* === Resolved Flags Overview === */}
        <section className="mb-8">
          <h2 className="text-lg font-semibold text-gray-800 mb-3">
            Resolved Flags Overview
          </h2>
          <div className="bg-white border rounded-lg shadow overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-2 text-left text-xs font-semibold text-gray-600 uppercase">
                    Flag ID
                  </th>
                  <th className="px-4 py-2 text-left text-xs font-semibold text-gray-600 uppercase">
                    Review ID
                  </th>
                  <th className="px-4 py-2 text-left text-xs font-semibold text-gray-600 uppercase">
                    Reason
                  </th>
                  <th className="px-4 py-2 text-left text-xs font-semibold text-gray-600 uppercase">
                    Created At
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {pendingFlags.length > 0 ? (
                  pendingFlags.map((flag) => (
                    <tr
                      key={flag.id}
                      className="hover:bg-gray-50 cursor-pointer"
                      onClick={() => {
                        setSelectedFlagId(flag.id);
                        if (flag.reviewId) setReviewId(flag.reviewId);
                      }}
                    >
                      <td className="px-4 py-2 text-sm text-gray-700">{flag.id}</td>
                      <td className="px-4 py-2 text-sm text-gray-700">
                        {flag.reviewId || "—"}
                      </td>
                      <td className="px-4 py-2 text-sm text-gray-600">{flag.reason}</td>
                      <td className="px-4 py-2 text-sm text-gray-500">
                        {new Date(flag.createdAt).toLocaleString("en-SG")}
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td
                      colSpan="4"
                      className="px-4 py-3 text-center text-sm text-gray-500"
                    >
                      No pending flags.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
          <p className="text-xs text-gray-500 mt-2">
            Click a row to autofill its Flag ID (and Review ID if available).
          </p>
        </section>


        <hr className="my-6" />

        {/* Hide Review */}
        <section className="mb-8">
          <h2 className="text-lg font-semibold text-gray-800 mb-2">
            Hide a Review
          </h2>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
            <input
              type="number"
              placeholder="Review ID"
              value={reviewId}
              onChange={(e) => setReviewId(e.target.value)}
              className="p-2 border rounded text-sm"
            />
            <input
              type="text"
              placeholder="Reason for hiding..."
              value={hideReason}
              onChange={(e) => setHideReason(e.target.value)}
              className="p-2 border rounded text-sm"
            />
            <button
              onClick={handleHideReview}
              className="px-3 py-2 bg-yellow-600 text-white rounded-md text-sm hover:bg-yellow-700"
            >
              Hide Review
            </button>
          </div>
          <p className="text-xs text-gray-500 mt-2">
            Hides the review (isHidden=true). Use only after resolving flag as REMOVE.
          </p>
        </section>

        <hr className="my-6" />

        {/* Delete Review */}
        <section>
          <h2 className="text-lg font-semibold text-gray-800 mb-2">
            Delete a Review
          </h2>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
            <input
              type="number"
              placeholder="Review ID"
              value={reviewId}
              onChange={(e) => setReviewId(e.target.value)}
              className="p-2 border rounded text-sm"
            />
            <input
              type="text"
              placeholder="Reason for deletion..."
              value={deleteReason}
              onChange={(e) => setDeleteReason(e.target.value)}
              className="p-2 border rounded text-sm"
            />
            <button
              onClick={handleDeleteReview}
              className="px-3 py-2 bg-red-600 text-white rounded-md text-sm hover:bg-red-700"
            >
              Delete Review
            </button>
          </div>
          <p className="text-xs text-gray-500 mt-2">
            Permanently deletes the review. Ensure flag was resolved first.
          </p>
        </section>

        <div className="mt-10 text-center">
          <a
            href="/admin/dashboard"
            className="text-green-700 text-sm font-medium hover:underline"
          >
            ← Back to Dashboard
          </a>
        </div>
      </div>
    </div>
  );
}
