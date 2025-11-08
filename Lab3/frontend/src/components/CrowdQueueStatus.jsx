import React from "react";
import { ArrowLeft, Users, Hourglass } from "lucide-react";
import { useParams, useNavigate } from "react-router-dom";

/**
 * Displays the real-time crowd and queue status for a specific eatery.
 *
 * <p>This component provides users with a visual and textual summary of
 * the current crowd conditions and estimated queue wait time for an eatery.
 * It retrieves the eatery ID dynamically from the URL via React Router parameters.</p>
 *
 * <p>Features include:
 * <ul>
 *   <li>Header with back navigation to the eatery detail page</li>
 *   <li>Eatery image and metadata (name, address, dietary tags)</li>
 *   <li>Queue status section with estimated waiting time</li>
 *   <li>Crowd level visualization using color-coded labels</li>
 * </ul>
 * </p>
 *
 * @component
 * @example
 * // Renders the crowd and queue status for a specific eatery (id provided in URL)
 * <Route path="/eatery/:id/crowd" element={<CrowdQueueStatus />} />
 *
 * @returns {JSX.Element} The rendered UI for the Crowd & Queue Status screen.
 *
 * @version 1.0
 * @since 2025-11-07
 */
export default function CrowdQueueStatus() {
  const { id } = useParams(); // get eatery ID from URL
  const navigate = useNavigate(); // for back navigation

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* Header */}
      <header className="bg-green-600 text-white px-4 py-3 flex items-center">
        <button className="mr-2" onClick={() => navigate(`/eatery/${id}`)}>
          <ArrowLeft size={22} />
        </button>
        <h1 className="text-lg font-semibold">Crowd & Queue Status</h1>
      </header>

      {/* Content */}
      <main className="flex-1 p-4">
        {/* Image Section */}
        <div className="rounded-xl overflow-hidden shadow-md">
          <img
            src="https://images.unsplash.com/photo-1604908177522-0404d38d5e9b"
            alt="Green Bowl"
            className="w-full h-48 object-cover"
          />
        </div>

        {/* Restaurant Info */}
        <div className="mt-4">
          <h2 className="text-2xl font-semibold text-gray-800">Green Bowl</h2>
          <p className="text-gray-500 text-sm">123 Hawker Lane</p>
          <div className="flex flex-wrap gap-2 mt-2">
            <span className="bg-gray-200 text-gray-700 px-3 py-1 rounded-full text-sm font-medium">
              Vegan
            </span>
            <span className="bg-gray-200 text-gray-700 px-3 py-1 rounded-full text-sm font-medium">
              Halal
            </span>
            <span className="bg-gray-200 text-gray-700 px-3 py-1 rounded-full text-sm font-medium">
              Healthy
            </span>
          </div>
        </div>

        {/* Queue Status */}
        <div className="mt-6 bg-white rounded-xl p-4 shadow-sm">
          <div className="flex items-center space-x-2 text-green-700 mb-1">
            <Hourglass size={20} />
            <h3 className="text-lg font-semibold">Queue Status</h3>
          </div>
          <p className="text-gray-700">
            Estimated Wait: <span className="font-semibold">10 mins</span>
          </p>
          <p className="text-gray-400 text-sm mt-1">Last updated: 5 mins ago</p>
        </div>

        {/* Crowd Status */}
        <div className="mt-4 bg-white rounded-xl p-4 shadow-sm">
          <div className="flex items-center space-x-2 text-green-700 mb-1">
            <Users size={20} />
            <h3 className="text-lg font-semibold">Crowd Status</h3>
          </div>
          <p className="text-gray-700">
            Low <span className="font-semibold">(&lt; 5 mins)</span>
          </p>

          <div className="flex gap-2 mt-3">
            <span className="flex-1 text-center bg-green-200 text-green-800 rounded-full py-1 text-sm font-medium">
              Low (&lt; 5 mins)
            </span>
            <span className="flex-1 text-center bg-yellow-200 text-yellow-800 rounded-full py-1 text-sm font-medium">
              Medium (5–15 mins)
            </span>
          </div>
        </div>
      </main>
    </div>
  );
}
