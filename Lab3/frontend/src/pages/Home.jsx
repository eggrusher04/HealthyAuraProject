import React, { useEffect, useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import { useNavigate } from "react-router-dom";
import API from "../services/api";

export default function Home() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [recommendations, setRecommendations] = useState([]);
  const [coords, setCoords] = useState(null);
  const [loading, setLoading] = useState(true);

  // Get user location
  useEffect(() => {
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setCoords({
          lat: pos.coords.latitude,
          lng: pos.coords.longitude,
        });
      },
      (err) => {
        console.warn("Location not available:", err.message);
        setCoords(null);
      }
    );
  }, []);

  // Fetch recommendations (only if logged in)
  useEffect(() => {
    const fetchRecommendations = async () => {
      if (!user) {
        setLoading(false);
        return;
      }

      setLoading(true);
      try {
        const params = coords ? { lat: coords.lat, lng: coords.lng } : {};
        const response = await API.get("/home/recommendations", { params });

        if (response.data && response.data.length > 0) {
          console.log("Fetched recommendations:", response.data);
          setRecommendations(response.data);
        } else {
          setRecommendations([]);
        }
      } catch (err) {
        console.error("Error fetching recommendations:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchRecommendations();
  }, [user, coords]);

  // Loading state
  if (loading)
    return (
      <div className="p-6 text-gray-600 text-center">
        Loading recommendations...
      </div>
    );

  // Not logged in
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

  const featured = recommendations.slice(0, 10);
  const others = recommendations.slice(10);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-semibold mb-4 text-green-800">
        Recommended for You
      </h1>

      {recommendations.length > 0 ? (
        <>
          {/* Featured Top 10 */}
          <h2 className="text-lg font-semibold text-green-700 mb-3">
            Top Picks
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
            {featured.map((rec) => (
              <div
                key={rec.id}
                className="p-4 bg-white rounded-lg shadow hover:shadow-md transition flex flex-col justify-between"
              >
                <div>
                  <h3 className="text-lg font-semibold text-green-700">
                    {rec.name}
                  </h3>
                  <p className="text-sm text-gray-600">
                    {rec.fullAddress || rec.address}
                  </p>
                  <p className="text-xs text-gray-500 mt-1">
                    {rec.description}
                  </p>
                  {rec.tags?.length > 0 && (
                    <div className="mt-2 flex flex-wrap gap-1">
                      {rec.tags.map((tag, i) => (
                        <span
                          key={i}
                          className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full"
                        >
                          {tag}
                        </span>
                      ))}
                    </div>
                  )}
                </div>
                <div className="mt-4">
                  <button
                    onClick={() => navigate(`/details/${rec.id}`)}
                    className="w-full bg-green-600 text-white py-2 rounded-md text-sm hover:bg-green-700 transition"
                  >
                    View
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Scrollable List for Remaining */}
          {others.length > 0 && (
            <>
              <h2 className="text-lg font-semibold text-green-700 mb-2">
                More Eateries
              </h2>
              <div className="overflow-x-auto whitespace-nowrap flex gap-3 p-2 border-t border-gray-200">
                {others.map((rec) => (
                  <div
                    key={rec.id}
                    className="inline-block w-72 bg-white rounded-lg shadow p-4 flex-shrink-0 hover:shadow-md transition flex flex-col justify-between"
                  >
                    <div>
                      <h3 className="text-md font-semibold text-green-700 truncate">
                        {rec.name}
                      </h3>
                      <p className="text-sm text-gray-500 truncate">
                        {rec.fullAddress || rec.address}
                      </p>
                      {rec.tags?.length > 0 && (
                        <div className="mt-2 flex flex-wrap gap-1">
                          {rec.tags.map((tag, i) => (
                            <span
                              key={i}
                              className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full"
                            >
                              {tag}
                            </span>
                          ))}
                        </div>
                      )}
                    </div>
                    <div className="mt-3">
                      <button
                        onClick={() => navigate(`/details/${rec.id}`)}
                        className="w-full bg-green-600 text-white py-2 rounded-md text-sm hover:bg-green-700 transition"
                      >
                        View
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </>
          )}
        </>
      ) : (
        <div className="p-4 bg-white rounded shadow text-center text-gray-500">
          No recommendations available.
        </div>
      )}
    </div>
  );
}
