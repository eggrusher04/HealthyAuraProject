import React, { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import API from '../services/api';

export default function Home() {
  const { user } = useAuth();
  const [recommendations, setRecommendations] = useState([]);
  const [recommend, setRecommend] = useState(null); // Popup eatery

  useEffect(() => {
    if (user) {
      const tags = user?.diet ? [user.diet] : [];

      const fetchRecommendations = async () => {
        try {
          let response;
          if (tags.length > 0) {
            response = await API.get('/home/recommendations/tags', { params: { tags } });
          } else {
            response = await API.get('/home/recommendations');
          }

          if (response.data && response.data.length > 0) {
            console.log('Fetched recommendations:', response.data);
            setRecommendations(response.data);
            setRecommend(response.data[0]); // popup always shows
          }
        } catch (err) {
          console.error('Error fetching recommendations:', err);
        }
      };

      fetchRecommendations();
    }
  }, [user]);

  const featured = recommendations.slice(0, 10);
  const others = recommendations.slice(10);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-semibold mb-4 text-green-800">
        Recommended for you
      </h1>

      {!user && (
        <div className="p-4 bg-white rounded shadow">
          Please sign in to see recommendations.
        </div>
      )}

      {user && (
        <>
          {/* Featured Top 10 */}
          <h2 className="text-lg font-semibold text-green-700 mb-3">
            Top Picks
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
            {featured.map((rec) => (
              <div
                key={rec.id}
                className="p-4 bg-white rounded-lg shadow hover:shadow-md transition"
              >
                <h3 className="text-lg font-semibold text-green-700">{rec.name}</h3>
                <p className="text-sm text-gray-600">{rec.fullAddress || rec.address}</p>
                <p className="text-xs text-gray-500 mt-1">{rec.description}</p>
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
                    className="inline-block w-72 bg-white rounded-lg shadow p-4 flex-shrink-0 hover:shadow-md transition"
                  >
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
                ))}
              </div>
            </>
          )}

          {/* Always-Visible Popup */}
          {recommend && (
            <div className="fixed bottom-6 right-6 w-80 bg-white border p-4 rounded-lg shadow-lg z-50 animate-slideUp">
              <div className="flex justify-between items-start">
                <div>
                  <div className="font-semibold text-green-800">{recommend.name}</div>
                  <div className="text-xs text-gray-500">
                    {recommend.fullAddress || recommend.address}
                  </div>
                  <div className="text-xs text-gray-400 mt-1">{recommend.description}</div>
                  {recommend.tags?.length > 0 && (
                    <div className="mt-2 flex flex-wrap gap-2">
                      {recommend.tags.map((tag, i) => (
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
                <button
                  onClick={() => setRecommend(null)}
                  className="text-sm text-gray-500"
                >
                  ✕
                </button>
              </div>

              <div className="mt-3 flex gap-2">
                <button className="flex-1 border rounded py-1 text-sm">View</button>
                <button className="flex-1 bg-green-600 text-white rounded py-1 text-sm">
                  Get Directions
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
