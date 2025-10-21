import React, { useEffect, useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import API from "../services/api";

export default function Rewards() {
  const { user } = useAuth();
  const [points, setPoints] = useState(0);
  const [rewards, setRewards] = useState([]);
  const [loading, setLoading] = useState(true);

  // Fetch user points
  const fetchPoints = async () => {
    try {
      const res = await API.get("/rewards/me");
      setPoints(res.data.totalPoints || 0);
    } catch (err) {
      console.error("Error fetching user points:", err);
    }
  };

  // Fetch rewards catalog
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

  useEffect(() => {
    if (user) {
      fetchPoints();
      fetchRewards();
    }
  }, [user]);

  // Redeem reward
  const redeem = async (rewardId) => {
    try {
      const res = await API.post(`/rewards/me/redeem-reward/${rewardId}`);
      alert(res.data.message || "Reward redeemed successfully!");
      fetchPoints(); // refresh balance
    } catch (err) {
      console.error("Error redeeming reward:", err);
      alert("Not enough points or reward unavailable.");
    }
  };

  if (!user) return <div>Please sign in to view rewards.</div>;

  if (loading)
    return (
      <div className="text-center text-gray-500 mt-8">
        Loading rewards...
      </div>
    );

  return (
    <div className="p-4">
      {/* Points display */}
      <div className="bg-white p-4 rounded shadow mb-4">
        <div className="text-sm text-gray-500">Your Points Balance</div>
        <div className="text-2xl font-bold text-green-700">{points}</div>
      </div>

      {/* Rewards grid */}
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
                <div className="text-xs text-gray-600 mb-1">
                  {r.description}
                </div>
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
