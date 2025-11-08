import React, { useEffect, useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import { useNavigate } from "react-router-dom";
import API from "../services/api";

/**
 * Home Page (Recommendations Dashboard)
 *
 * <p>The `Home` component serves as the main dashboard after user login.
 * It displays two categories of eatery recommendations:
 * <ul>
 *   <li><b>Personalized Recommendations</b> – based on user preferences and past activity</li>
 *   <li><b>Nearby Eateries</b> – based on the user’s current geolocation</li>
 * </ul>
 * </p>
 *
 * <p>Key Features:
 * <ul>
 *   <li>Uses the <b>Geolocation API</b> to fetch current coordinates.</li>
 *   <li>Parallel API fetching for performance optimization via <code>Promise.all()</code>.</li>
 *   <li>Displays a <b>loading skeleton</b> while fetching data.</li>
 *   <li>Responsive UI grid for eatery cards.</li>
 *   <li>Fallback handling for users not logged in.</li>
 * </ul>
 * </p>
 *
 * <p>Backend Endpoints:
 * <ul>
 *   <li><code>GET /home/recommendations</code> → Personalized recommendations</li>
 *   <li><code>GET /home/recommendations?lat={lat}&lng={lng}</code> → Location-based eateries</li>
 * </ul>
 * </p>
 *
 * @component
 * @example
 * // Example route
 * <Route path="/" element={<Home />} />
 *
 * @returns {JSX.Element} The home dashboard displaying user recommendations.
 * @since 2025-11-07
 * @version 1.0
 */
export default function Home() {
  const { user } = useAuth();
  const navigate = useNavigate();

  // === State Variables ===
  const [personalized, setPersonalized] = useState([]); // Recommendations based on preferences
  const [nearby, setNearby] = useState([]); // Recommendations near current location
  const [coords, setCoords] = useState(null); // User’s current geolocation
  const [loading, setLoading] = useState(true); // Loading state for data fetch

  /**
   * Attempts to get the user's current location using the browser's Geolocation API.
   * Fallbacks to `null` if permission is denied or unavailable.
   *
   * @effect
   * @returns {void}
   */
  useEffect(() => {
    navigator.geolocation.getCurrentPosition(
      (pos) => setCoords({ lat: pos.coords.latitude, lng: pos.coords.longitude }),
      (err) => {
        console.warn("Location not available:", err.message);
        setCoords(null);
      }
    );
  }, []);

  /**
   * Fetches both personalized and nearby recommendations concurrently.
   *
   * <p>Personalized recommendations are based on user profile preferences.
   * Nearby recommendations depend on current coordinates. Both API calls are
   * executed in parallel using <code>Promise.all()</code>.</p>
   *
   * @async
   * @returns {Promise<void>}
   */
  useEffect(() => {
    const fetchRecommendations = async () => {
      if (!user) {
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        console.time("recommendations");

        const [personalRes, nearbyRes] = await Promise.all([
          API.get("/home/recommendations"), // Personalized
          coords
            ? API.get("/home/recommendations", {
                params: { lat: coords.lat, lng: coords.lng },
              })
            : Promise.resolve({ data: [] }), // Fallback if no location
        ]);

        console.timeEnd("recommendations");

        if (Array.isArray(personalRes.data)) {
          setPersonalized(personalRes.data.slice(0, 5));
        }
        if (Array.isArray(nearbyRes.data)) {
          setNearby(nearbyRes.data.slice(0, 5));
        }
      } catch (err) {
        console.error("Error fetching recommendations:", err);
      } finally {
        setLoading(false);
      }
    };

    if (user === null) {
      // Ensures shimmer disappears if user is not logged in
      setLoading(false);
    } else {
      fetchRecommendations();
    }
  }, [user, coords]);

  /**
   * Displays a shimmer-style loading placeholder while recommendations are being fetched.
   *
   * @returns {JSX.Element} Loading skeleton animation.
   */
  const LoadingSkeleton = () => (
    <div className="animate-pulse">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {[...Array(6)].map((_, i) => (
          <div key={i} className="p-4 bg-gray-100 rounded-lg shadow-sm h-44 space-y-3">
            <div className="h-4 bg-gray-300 rounded w-3/4"></div>
            <div className="h-3 bg-gray-300 rounded w-1/2"></div>
            <div className="h-3 bg-gray-200 rounded w-5/6"></div>
            <div className="flex gap-2 mt-2">
              <div className="h-4 w-12 bg-gray-300 rounded-full"></div>
              <div className="h-4 w-10 bg-gray-200 rounded-full"></div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );

  // === Loading State ===
  if (loading)
    return (
      <div className="p-6 text-gray-600">
        <h1 className="text-2xl font-semibold mb-4 text-green-800">
          Loading recommendations...
        </h1>
        <LoadingSkeleton />
      </div>
    );

  // === Not Logged In State ===
  if (!user)
    return (
      <div className="min-h-screen flex flex-col items-center justify-center text-center p-6">
        <h1 className="text-2xl font-semibold mb-4 text-green-800">
          Welcome to HealthyAura
        </h1>
        <p className="text-gray-700 bg-white p-4 rounded shadow max-w-md">
          You must be logged in to see personalized recommendations.
          <br />
          <span className="text-sm text-gray-500">
            Please sign in to view eateries tailored to your preferences.
          </span>
        </p>
      </div>
    );

  /**
   * Renders an eatery card component with details such as tags, address, scores, and distance.
   *
   * @param {Object} rec - The eatery recommendation object.
   * @returns {JSX.Element} Formatted eatery card UI.
   */
  const renderCard = (rec) => (
    <div
      key={rec.id}
      className="p-4 bg-white rounded-lg shadow hover:shadow-md transition flex flex-col justify-between"
    >
      <div>
        <h3 className="text-lg font-semibold text-green-700">{rec.name}</h3>
        <p className="text-sm text-gray-600">{rec.fullAddress || rec.address}</p>

        {/* Dietary Tags */}
        {Array.isArray(rec.tags) && rec.tags.length > 0 && (
          <div className="mt-2 flex flex-wrap gap-1">
            {rec.tags.map((tag, i) => (
              <span
                key={i}
                className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full"
              >
                {typeof tag === "string" ? tag : tag.tag}
              </span>
            ))}
          </div>
        )}

        {/* Description */}
        {rec.description && (
          <p className="text-xs text-gray-500 mt-2">{rec.description}</p>
        )}

        {/* Reason (e.g., "You liked similar stalls") */}
        {rec.reason && (
          <p className="text-xs text-gray-400 mt-1 italic">{rec.reason}</p>
        )}

        {/* Distance from current location */}
        {rec.distance != null && (
          <p className="text-xs text-gray-500 mt-1">
            📍 {rec.distance.toFixed(2)} km away
          </p>
        )}

        {/* Scores + Review Count */}
        <div className="mt-3 flex flex-wrap items-center gap-4 text-sm text-gray-700">
          {rec.averageHealth != null && !isNaN(rec.averageHealth) && (
            <span>
              Health:{" "}
              <span className="font-semibold text-green-700">
                {Number(rec.averageHealth).toFixed(1)}
              </span>
            </span>
          )}
          {rec.averageHygiene != null && !isNaN(rec.averageHygiene) && (
            <span>
              Hygiene:{" "}
              <span className="font-semibold text-green-700">
                {Number(rec.averageHygiene).toFixed(1)}
              </span>
            </span>
          )}
          {rec.score != null && !isNaN(rec.score) && (
            <span>
              Score:{" "}
              <span className="font-semibold text-green-700">
                {Number(rec.score).toFixed(1)}
              </span>
            </span>
          )}
          {rec.reviewCount != null && (
            <span>
              {rec.reviewCount} review{rec.reviewCount !== 1 ? "s" : ""}
            </span>
          )}
        </div>
      </div>

      {/* Navigation Button */}
      <div className="mt-4">
        <button
          onClick={() => navigate(`/details/${rec.id}`)}
          className="w-full bg-green-600 text-white py-2 rounded-md text-sm hover:bg-green-700 transition"
        >
          View
        </button>
      </div>
    </div>
  );

  // === Render Final Layout ===
  return (
    <div className="p-6">
      <h1 className="text-2xl font-semibold mb-4 text-green-800">
        Recommended for You
      </h1>

      {/* Nearby Eateries Section */}
      {nearby.length > 0 && (
        <>
          <h2 className="text-lg font-semibold text-green-700 mb-3">
            Nearby Eateries
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
            {nearby.map(renderCard)}
          </div>
        </>
      )}

      {/* Personalized Recommendations Section */}
      {personalized.length > 0 && (
        <>
          <h2 className="text-lg font-semibold text-green-700 mb-3">
            Based on Your Preferences
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
            {personalized.map(renderCard)}
          </div>
        </>
      )}

      {/* Fallback: No Data */}
      {nearby.length === 0 && personalized.length === 0 && (
        <div className="p-4 bg-white rounded shadow text-center text-gray-500">
          No recommendations available.
        </div>
      )}
    </div>
  );
}
