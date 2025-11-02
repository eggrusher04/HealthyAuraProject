import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Users, Hourglass, Star, UserCircle } from "lucide-react";
import BottomNav from "../components/BottomNav";
import API from "../services/api";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

export default function DetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [stall, setStall] = useState(null);
  const [crowd, setCrowd] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [avgRatings, setAvgRatings] = useState(null);

  // Review form
  const [healthScore, setHealthScore] = useState(0);
  const [hygieneScore, setHygieneScore] = useState(0);
  const [reviewText, setReviewText] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [editingReviewId, setEditingReviewId] = useState(null);

  useEffect(() => {
    const fetchStall = async () => {
      try {
        const res = await API.get(`/api/eateries/${id}`);
        setStall(res.data);
      } catch (err) {
        console.error("Failed to load eatery:", err);
      }
    };
    fetchStall();
  }, [id]);

  useEffect(() => {
    const fetchCrowd = async () => {
      try {
        const res = await API.get(`/crowd/${id}`);
        setCrowd(res.data);
      } catch (err) {
        console.error("Failed to load crowd info:", err);
      }
    };
    fetchCrowd();
  }, [id]);

  const fetchReviews = async () => {
    try {
      const res = await API.get(`/api/eateries/${id}/reviews`);
      setReviews(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.error("Failed to load reviews:", err);
    }
  };

  const fetchAverageRatings = async () => {
    try {
      const res = await API.get(`/api/eateries/${id}/reviews/ratings`);
      setAvgRatings(res.data);
    } catch (err) {
      console.error("Failed to load average ratings:", err);
    }
  };

  useEffect(() => {
    fetchReviews();
    fetchAverageRatings();
  }, [id]);

  // Map setup
  useEffect(() => {
    if (!stall || !stall.latitude || !stall.longitude) return;
    const existing = L.DomUtil.get("details-map");
    if (existing && existing._leaflet_id) existing._leaflet_id = null;

    const map = L.map("details-map", {
      center: [stall.latitude, stall.longitude],
      zoom: 17,
    });

    L.tileLayer(
      "https://www.onemap.gov.sg/maps/tiles/Original/{z}/{x}/{y}.png",
      { attribution: "Map data © OneMap Singapore" }
    ).addTo(map);

    L.marker([stall.latitude, stall.longitude])
      .addTo(map)
      .bindPopup(`<b>${stall.name}</b><br>${stall.address || ""}`)
      .openPopup();

    return () => map.remove();
  }, [stall]);

  // Submit or update review
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!healthScore || !hygieneScore || !reviewText) {
      alert("Please fill in all fields.");
      return;
    }

    setSubmitting(true);
    try {
      const payload = {
        healthScore,
        hygieneScore,
        textFeedback: reviewText,
      };

      if (editingReviewId) {
        await API.put(`/api/eateries/${id}/reviews/${editingReviewId}`, payload);
        alert("Review updated!");
      } else {
        await API.post(`/api/eateries/${id}/reviews`, payload);
        alert("Review submitted!");
      }

      setHealthScore(0);
      setHygieneScore(0);
      setReviewText("");
      setEditingReviewId(null);
      fetchReviews();
      fetchAverageRatings();
    } catch (err) {
      console.error("Failed to submit review:", err);
      alert("You must wait 7 days before submitting a new review for this eatery");
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (reviewId) => {
    if (!window.confirm("Are you sure you want to delete this review?")) return;
    try {
      await API.delete(`/api/eateries/${id}/reviews/${reviewId}`);
      alert("Review deleted.");
      fetchReviews();
      fetchAverageRatings();
    } catch (err) {
      console.error("Failed to delete review:", err);
    }
  };

  const handleFlag = async (reviewId) => {
    try {
      const reason = prompt("Enter reason for flagging this review:");
      if (!reason) return;
      await API.post(`/api/eateries/${id}/reviews/${reviewId}/flag`, { reason });
      alert("Review reported for moderation.");
    } catch (err) {
      console.error("Failed to flag review:", err);
    }
  };

  const startEditInline = (r) => {
    setEditingReviewId(r.id);
    setHealthScore(r.healthScore);
    setHygieneScore(r.hygieneScore);
    setReviewText(r.textFeedback);
  };

  const cancelEdit = () => {
    setEditingReviewId(null);
    setHealthScore(0);
    setHygieneScore(0);
    setReviewText("");
  };

  const renderStars = (count, activeCount) => (
    <div className="flex">
      {[...Array(count)].map((_, i) => (
        <Star
          key={i}
          size={20}
          className={`${
            i < activeCount ? "text-green-500 fill-green-500" : "text-gray-300"
          }`}
        />
      ))}
    </div>
  );

  // Render average stars (with fractional fill)
  const renderAverageStars = (avg) => {
    const full = Math.floor(avg);
    const hasHalf = avg - full >= 0.25 && avg - full < 0.75;
    const rounded = avg - full >= 0.75 ? full + 1 : full;

    return (
      <div className="flex">
        {[...Array(5)].map((_, i) => {
          if (i < rounded && !hasHalf) {
            return <Star key={i} size={20} className="text-green-500 fill-green-500" />;
          } else if (i === full && hasHalf) {
            return (
              <div key={i} className="relative">
                <Star size={20} className="text-gray-300" />
                <div className="absolute top-0 left-0 overflow-hidden w-1/2">
                  <Star size={20} className="text-green-500 fill-green-500" />
                </div>
              </div>
            );
          } else {
            return <Star key={i} size={20} className="text-gray-300" />;
          }
        })}
      </div>
    );
  };

  if (!stall)
    return <div className="p-4 text-center text-gray-500">Loading place details...</div>;

  return (
    <div className="pb-20 bg-gray-50 min-h-screen">
      {/* Header */}
      <div className="flex items-center justify-between p-4 border-b bg-white shadow-sm">
        <button onClick={() => navigate(-1)} className="text-green-700 font-medium">
          ← Back
        </button>
        <h1 className="text-green-700 text-lg font-semibold">HealthyAura</h1>
        <div className="w-10" />
      </div>

      {/* Eatery Info */}
      <div className="px-4 pt-4">
        <h2 className="text-xl font-semibold text-gray-900">{stall.name}</h2>
        <p className="text-sm text-gray-500 mt-1">
          {stall.fullAddress || stall.address || "Address not available"}
        </p>
      </div>

      {/* Map */}
      <div className="px-4 mt-5">
        <div className="bg-white rounded-xl shadow-sm border p-3">
          <div id="details-map" className="w-full h-64 rounded-lg border" />
        </div>
      </div>

      {/* Open in Maps */}
      <div className="px-4 mt-3">
        <button
          onClick={() =>
            window.open(
              `https://www.google.com/maps/dir/?api=1&destination=${stall.latitude},${stall.longitude}`,
              "_blank"
            )
          }
          className="w-full bg-green-600 text-white py-3 rounded-lg hover:bg-green-700 transition text-sm font-medium"
        >
          Open in Google Maps
        </button>
      </div>

      {/* Crowd Info */}
      <div className="px-4 mt-6 space-y-4">
        <div className="bg-white rounded-xl p-4 shadow-sm">
          <div className="flex items-center space-x-2 text-green-700 mb-1">
            <Hourglass size={20} />
            <h3 className="text-lg font-semibold">Queue Status</h3>
          </div>
          {crowd ? (
            <p className="text-gray-700">
              Estimated Wait:{" "}
              <span className="font-semibold">{crowd.estimatedQueueMinutes} mins</span>
            </p>
          ) : (
            <p className="text-gray-400">Loading queue info...</p>
          )}
        </div>

        <div className="bg-white rounded-xl p-4 shadow-sm">
          <div className="flex items-center space-x-2 text-green-700 mb-1">
            <Users size={20} />
            <h3 className="text-lg font-semibold">Crowd Status</h3>
          </div>
          {crowd ? (
            <div
              className="rounded-lg py-2 px-3 text-center font-medium text-gray-800"
              style={{ backgroundColor: crowd.colorCode || "#E5E7EB" }}
            >
              {crowd.crowdLevel}
            </div>
          ) : (
            <p className="text-gray-400">Loading crowd info...</p>
          )}
        </div>
      </div>

      {/* Overall Ratings */}
      <div className="px-4 mt-8">
        <h2 className="text-xl font-semibold text-gray-800 mb-2">Overall Ratings</h2>
        {avgRatings ? (
          avgRatings.totalReviews > 0 ? (
            <div className="bg-white p-4 rounded-xl shadow-sm">
              <div className="flex justify-between">
                <div>
                  <p className="text-gray-800 font-medium flex items-center gap-2">
                    <span className="text-green-700 font-semibold text-lg">
                      {avgRatings.averageHealthScore?.toFixed(1) ?? "-"}
                    </span>
                    {renderAverageStars(avgRatings.averageHealthScore || 0)}
                    <span className="text-gray-600 text-sm ml-1">Health</span>
                  </p>
                  <p className="text-gray-800 font-medium flex items-center gap-2 mt-1">
                    <span className="text-green-700 font-semibold text-lg">
                      {avgRatings.averageHygieneScore?.toFixed(1) ?? "-"}
                    </span>
                    {renderAverageStars(avgRatings.averageHygieneScore || 0)}
                    <span className="text-gray-600 text-sm ml-1">Hygiene</span>
                  </p>
                </div>
                <p className="text-gray-500 text-sm text-right">
                  {avgRatings.totalReviews} Reviews Total
                </p>
              </div>
            </div>
          ) : (
            <div className="bg-white p-4 rounded-xl shadow-sm text-gray-500 text-center">
              No ratings yet. Be the first to leave a review!
            </div>
          )
        ) : (
          <p className="text-gray-500">Loading ratings...</p>
        )}
      </div>

      {/* Reviews Section */}
      <div className="px-4 mt-8 space-y-3 mb-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-3">User Reviews</h2>

        {/* Existing reviews */}
        {reviews.length > 0 ? (
          reviews.map((r) => (
            <div
              key={r.id}
              className={`bg-white rounded-xl p-4 shadow-sm ${
                r.isOwnReview ? "border-2 border-green-400" : ""
              }`}
            >
              <div className="flex justify-between items-start">
                <div>
                  <p className="font-semibold text-gray-800">
                    {r.authorAlias}{" "}
                    {r.isOwnReview && <span className="text-green-600">(You)</span>}
                  </p>
                  <p className="text-xs text-gray-400">
                    {new Date(r.updatedAt || r.createdAt).toLocaleString()}
                  </p>
                </div>
                <div className="flex gap-2">
                  {r.isOwnReview ? (
                    <>
                      <button
                        onClick={() => startEditInline(r)}
                        className="text-blue-600 text-xs hover:underline"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(r.id)}
                        className="text-red-600 text-xs hover:underline"
                      >
                        Delete
                      </button>
                    </>
                  ) : (
                    <button
                      onClick={() => handleFlag(r.id)}
                      className="text-yellow-600 text-xs hover:underline"
                    >
                      Flag
                    </button>
                  )}
                </div>
              </div>

              {editingReviewId === r.id ? (
                <form onSubmit={handleSubmit} className="mt-3 space-y-2">
                  <textarea
                    className="w-full border rounded p-2 text-sm"
                    rows="3"
                    value={reviewText}
                    onChange={(e) => setReviewText(e.target.value)}
                  ></textarea>

                  <div className="flex gap-4">
                    <div>
                      <label className="text-sm font-medium">Health</label>
                      <div className="flex">
                        {[...Array(5)].map((_, i) => (
                          <button
                            type="button"
                            key={i}
                            onClick={() => setHealthScore(i + 1)}
                            className="focus:outline-none"
                          >
                            <Star
                              size={20}
                              className={`${
                                i < healthScore
                                  ? "text-green-500 fill-green-500"
                                  : "text-gray-300"
                              }`}
                            />
                          </button>
                        ))}
                      </div>
                    </div>
                    <div>
                      <label className="text-sm font-medium">Hygiene</label>
                      <div className="flex">
                        {[...Array(5)].map((_, i) => (
                          <button
                            type="button"
                            key={i}
                            onClick={() => setHygieneScore(i + 1)}
                            className="focus:outline-none"
                          >
                            <Star
                              size={20}
                              className={`${
                                i < hygieneScore
                                  ? "text-green-500 fill-green-500"
                                  : "text-gray-300"
                              }`}
                            />
                          </button>
                        ))}
                      </div>
                    </div>
                  </div>

                  <div className="flex gap-2">
                    <button
                      type="submit"
                      disabled={submitting}
                      className="flex-1 bg-green-600 text-white py-1 rounded hover:bg-green-700 text-sm"
                    >
                      {submitting ? "Saving..." : "Save"}
                    </button>
                    <button
                      type="button"
                      onClick={cancelEdit}
                      className="flex-1 bg-gray-200 text-gray-700 py-1 rounded hover:bg-gray-300 text-sm"
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              ) : (
                <>
                  <p className="text-gray-600 text-sm mt-2">{r.textFeedback}</p>
                  <div className="flex gap-3 mt-3 text-xs text-gray-600">
                    <div>
                      <span className="font-medium">Health:</span>{" "}
                      {renderStars(5, r.healthScore)}
                    </div>
                    <div>
                      <span className="font-medium">Hygiene:</span>{" "}
                      {renderStars(5, r.hygieneScore)}
                    </div>
                  </div>
                </>
              )}
            </div>
          ))
        ) : (
          <div className="text-center text-gray-500 bg-white rounded-xl shadow-sm p-4">
            No reviews yet — be the first to share your thoughts!
          </div>
        )}

        {/* Always-visible review form */}
        <div className="bg-white rounded-xl p-4 shadow-sm mt-5">
          <h3 className="text-lg font-semibold text-green-700 mb-2">Leave a Review</h3>
          <form onSubmit={handleSubmit} className="space-y-3">
            <textarea
              className="w-full border border-gray-300 rounded-lg p-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              rows="3"
              placeholder="Write your review here..."
              value={reviewText}
              onChange={(e) => setReviewText(e.target.value)}
            ></textarea>

            <div className="flex gap-5">
              <div>
                <label className="block text-gray-700 text-sm mb-1">Health</label>
                <div className="flex">
                  {[...Array(5)].map((_, i) => (
                    <button
                      type="button"
                      key={i}
                      onClick={() => setHealthScore(i + 1)}
                      className="focus:outline-none"
                    >
                      <Star
                        size={22}
                        className={`${
                          i < healthScore
                            ? "text-green-500 fill-green-500"
                            : "text-gray-300"
                        }`}
                      />
                    </button>
                  ))}
                </div>
              </div>
              <div>
                <label className="block text-gray-700 text-sm mb-1">Hygiene</label>
                <div className="flex">
                  {[...Array(5)].map((_, i) => (
                    <button
                      type="button"
                      key={i}
                      onClick={() => setHygieneScore(i + 1)}
                      className="focus:outline-none"
                    >
                      <Star
                        size={22}
                        className={`${
                          i < hygieneScore
                            ? "text-green-500 fill-green-500"
                            : "text-gray-300"
                        }`}
                      />
                    </button>
                  ))}
                </div>
              </div>
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="w-full bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 transition text-sm font-medium"
            >
              {submitting ? "Submitting..." : "Submit Review"}
            </button>
          </form>
        </div>
      </div>


      <BottomNav />
    </div>
  );
}
