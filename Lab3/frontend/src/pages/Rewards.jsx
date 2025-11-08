import React, { useEffect, useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import API from "../services/api";

/**
 * Rewards Page
 *
 * <p>The `Rewards` component manages the user's reward points system
 * and redemption functionalities in the HealthyAura application.</p>
 *
 * <p>It allows users to:</p>
 * <ul>
 *   <li>View their current reward points balance.</li>
 *   <li>Browse available rewards from the catalog.</li>
 *   <li>Redeem rewards if they have sufficient points.</li>
 * </ul>
 *
 * <p>Key Features:</p>
 * <ul>
 *   <li>Integrates with backend endpoints for point tracking and redemption.</li>
 *   <li>Displays active/inactive rewards dynamically.</li>
 *   <li>Provides instant balance updates upon successful redemption.</li>
 *   <li>Gracefully handles API and authentication errors.</li>
 * </ul>
 *
 * <p>Backend Endpoints:</p>
 * <ul>
 *   <li><code>GET /rewards/me</code> → Fetch current user's total points.</li>
 *   <li><code>GET /rewards/catalog</code> → Retrieve available reward items.</li>
 *   <li><code>POST /rewards/me/redeem-reward/{rewardId}</code> → Redeem selected reward.</li>
 * </ul>
 *
 * @component
 * @example
 * // Example route
 * <Route path="/rewards" element={<Rewards />} />
 *
 * @returns {JSX.Element} The Rewards page showing point balance and redeemable rewards.
 * @since 2025-11-07
 * @version 1.0
 */
export default function Rewards() {
  const { user } = useAuth();

  // === State Variables ===
  const [points, setPoints] = useState(0);         // Current user’s total points
  const [rewards, setRewards] = useState([]);      // List of available rewards
  const [loading, setLoading] = useState(true);    // Loading indicator for fetch calls

  /**
   * Fetches the user's current point balance from the backend.
   *
   * @async
   * @returns {Promise<void>}
   */
  const fetchPoints = async () => {
    try {
      const res = await API.get("/rewards/me");
      setPoints(res.data.totalPoints || 0);
    } catch (err) {
      console.error("Error fetching user points:", err);
    }
  };

  /**
   * Fetches the catalog of all available rewards.
   *
   * @async
   * @returns {Promise<void>}
   */
  const fetchRewards = async () => {
    try {
      const res = await API.get("/rewards/catalog");
      setRewards(res.data || []);
    } catch (err) {
      console.error("Error fetching rewards catalog:", err);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Fetches both user points and rewards catalog when user is authenticated.
   *
   * @effect
   * @returns {void}
   */
  useEffect(() => {
    if (user) {
      fetchPoints();
      fetchRewards();
    }
  }, [user]);

  /**
   * Handles reward redemption request.
   *
   * <p>Attempts to redeem the specified reward and updates the user's
   * point balance upon success. Displays alerts for both success and
   * error scenarios.</p>
   *
   * @async
   * @param {number|string} rewardId - ID of the reward to redeem.
   * @returns {Promise<void>}
   */
  const redeem = async (rewardId) => {
    try {
      const res = await API.post(`/rewards/me/redeem-reward/${rewardId}`);
      alert(res.data.message || "Reward redeemed successfully!");
      fetchPoints(); // Refresh balance after redemption
    } catch (err) {
      console.error("Error redeeming reward:", err);
      alert("Not enough points or reward unavailable.");
    }
  };

  // === Conditional Rendering ===
  if (!user) return <div>Please sign in to view rewards.</div>;

  if (loading)
    return (
      <div className="text-center text-gray-500 mt-8">
        Loading rewards...
      </div>
    );

  // === Render Main Content ===
  return (
    <div className="p-4">
      {/* === Points Display Section === */}
      <div className="bg-white p-4 rounded shadow mb-4">
        <div className="text-sm text-gray-500">Your Points Balance</div>
        <div className="text-2xl font-bold text-green-700">{points}</div>
      </div>

      {/* === Rewards Catalog Section === */}
      <div className="grid gap-3">
        {rewards.length === 0 ? (
          <div className="text-center text-gray-500">No rewards available.</div>
        ) : (
          rewards.map((r) => (
            <div
              key={r.id}
              className="bg-white p-4 rounded shadow flex justify-between items-center"
            >
              <div>
                <div className="font-semibold text-green-700">{r.name}</div>
                <div className="text-xs text-gray-600 mb-1">{r.description}</div>
                <div className="text-xs text-gray-500">
                  Cost: {r.pointsRequired} pts •{" "}
                  {r.active ? "Active" : "Inactive"}
                </div>
              </div>

              <div>
                <button
                  disabled={points < r.pointsRequired}
                  onClick={() => redeem(r.id)}
                  className={`px-3 py-1 rounded ${
                    points >= r.pointsRequired
                      ? "bg-green-600 text-white hover:bg-green-700"
                      : "bg-gray-200 text-gray-500"
                  }`}
                >
                  Redeem
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
