import React, { useState, useEffect } from "react";
import axios from "axios";

/**
 * Administrative dashboard for the HealthyAura web application.
 *
 * <p>The `AdminDashboard` component provides a centralized interface for platform administrators
 * to monitor system metrics, flagged user reviews, and recent moderation activity.
 * It also serves as an entry point for deeper moderation tools (e.g., review flag resolution and
 * administrative log auditing).</p>
 *
 * <p>Key functionalities include:
 * <ul>
 *   <li>Fetching and displaying key system metrics (pending flags, keywords, and categories)</li>
 *   <li>Filtering flagged reviews by reason or status</li>
 *   <li>Displaying recent and historical admin action logs</li>
 *   <li>Real-time data refresh using secured JWT authorization</li>
 *   <li>Link to the moderation management panel</li>
 * </ul>
 * </p>
 *
 * @component
 * @example
 * // Used in admin route for management overview
 * <Route path="/admin/dashboard" element={<AdminDashboard />} />
 *
 * @returns {JSX.Element} The rendered administrative dashboard view with metrics, flags, and logs.
 *
 * @since 2025-11-07
 * @version 1.0
 */
export default function AdminDashboard() {
  const [metrics, setMetrics] = useState(null);
  const [flags, setFlags] = useState([]);
  const [recentLogs, setRecentLogs] = useState([]);
  const [allLogs, setAllLogs] = useState([]);

  const [statusFilter, setStatusFilter] = useState("PENDING");
  const [reasonFilter, setReasonFilter] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const token = localStorage.getItem("token");
  const adminUsername = localStorage.getItem("adminUsername") || "Admin";

  useEffect(() => {
    loadDashboard();
  }, []);

  /**
   * Loads all admin dashboard data concurrently.
   *
   * <p>This method performs multiple authenticated `GET` requests using {@link axios} to retrieve:</p>
   * <ul>
   *   <li>System metrics summary</li>
   *   <li>Pending or resolved review flags</li>
   *   <li>Recent admin actions summary</li>
   *   <li>All administrative logs</li>
   * </ul>
   *
   * @async
   * @returns {Promise<void>} Resolves after all dashboard data is fetched and stored in state.
   */
  const loadDashboard = async () => {
    setLoading(true);
    try {
      const [metricsRes, flagsRes, recentRes, allLogsRes] = await Promise.all([
        axios.get("http://localhost:8080/admin/dashboard/metrics", {
          headers: { Authorization: `Bearer ${token}` },
        }),
        axios.get(
          `http://localhost:8080/admin/dashboard/flags?status=${statusFilter}`,
          { headers: { Authorization: `Bearer ${token}` } }
        ),
        axios.get("http://localhost:8080/admin/dashboard/recent-summary", {
          headers: { Authorization: `Bearer ${token}` },
        }),
        axios.get("http://localhost:8080/admin/logs", {
          headers: { Authorization: `Bearer ${token}` },
        }),
      ]);

      setMetrics(metricsRes.data);
      setFlags(flagsRes.data);
      setRecentLogs(recentRes.data);
      setAllLogs(allLogsRes.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load admin dashboard data.");
    } finally {
      setLoading(false);
    }
  };

  /**
   * Filters flagged reviews by reason and status.
   *
   * <p>When a reason is entered, this function queries the backend using
   * the `/admin/dashboard/flags/by-reason` endpoint and updates the table view.</p>
   *
   * @async
   * @returns {Promise<void>} Resolves after flags are filtered or resets to default view.
   */
  const handleReasonSearch = async () => {
    if (!reasonFilter.trim()) {
      loadDashboard();
      return;
    }
    setLoading(true);
    try {
      const res = await axios.get(
        `http://localhost:8080/admin/dashboard/flags/by-reason?reason=${encodeURIComponent(
          reasonFilter
        )}&status=${statusFilter}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setFlags(res.data);
    } catch (err) {
      console.error(err);
      setError("Failed to filter flags by reason.");
    } finally {
      setLoading(false);
    }
  };

  /**
   * Utility function to format ISO timestamps into localized Singapore date-time strings.
   *
   * @param {string|Date} d - The date or timestamp to format.
   * @returns {string} Formatted date-time string or "N/A" if unavailable.
   */
  const formatDate = (d) =>
    d
      ? new Date(d).toLocaleString("en-SG", {
          year: "numeric",
          month: "short",
          day: "2-digit",
          hour: "2-digit",
          minute: "2-digit",
          hour12: true,
        })
      : "N/A";

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center text-gray-600">
        Loading admin dashboard...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* === Header Section === */}
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Admin Dashboard</h1>
            <p className="text-sm text-gray-500">Welcome, {adminUsername}</p>
          </div>
          <button
            onClick={loadDashboard}
            className="px-3 py-2 bg-green-600 text-white rounded-md text-sm hover:bg-green-700"
          >
            Refresh
          </button>
        </div>
      </header>

      {/* === Main Content === */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-6">
            {error}
          </div>
        )}

        {/* === System Metrics === */}
        {metrics && (
          <>
            <h2 className="text-xl font-semibold text-gray-800 mb-3">
              System Metrics
            </h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
              <div className="bg-white p-4 rounded-xl shadow text-center">
                <h3 className="text-sm text-gray-500">Pending Flags</h3>
                <p className="text-3xl font-bold text-green-600">
                  {metrics.pendingFlags}
                </p>
              </div>
              <div className="bg-white p-4 rounded-xl shadow text-center">
                <h3 className="text-sm text-gray-500">By Reason</h3>
                {Object.entries(metrics.pendingByReason || {}).map(([k, v]) => (
                  <p key={k} className="text-gray-700 text-sm">
                    {k}: <span className="font-semibold">{v}</span>
                  </p>
                ))}
              </div>
              <div className="bg-white p-4 rounded-xl shadow text-center">
                <h3 className="text-sm text-gray-500">By Keyword</h3>
                {Object.entries(metrics.pendingByKeywords || {}).map(([k, v]) => (
                  <p key={k} className="text-gray-700 text-sm">
                    {k}: <span className="font-semibold">{v}</span>
                  </p>
                ))}
              </div>
              <div className="bg-white p-4 rounded-xl shadow text-center">
                <h3 className="text-sm text-gray-500">Last Updated</h3>
                <p className="text-gray-700">{formatDate(new Date())}</p>
              </div>
            </div>
          </>
        )}

        {/* === Flagged Reviews Section === */}
        <h2 className="text-xl font-semibold text-gray-800 mb-4">
          Flagged Reviews
        </h2>

        {/* Filters */}
        <div className="flex flex-wrap items-center gap-3 mb-4">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="p-2 border rounded text-sm"
          >
            <option value="PENDING">Pending</option>
            <option value="RESOLVED">Resolved</option>
            <option value="DISMISSED">Dismissed</option>
          </select>

          <input
            type="text"
            placeholder="Search by reason..."
            value={reasonFilter}
            onChange={(e) => setReasonFilter(e.target.value)}
            className="p-2 border rounded text-sm flex-1"
          />
          <button
            onClick={handleReasonSearch}
            className="px-3 py-2 bg-green-600 text-white rounded-md text-sm hover:bg-green-700"
          >
            Filter
          </button>
        </div>

        {/* Flags Table */}
        <div className="bg-white rounded-xl shadow overflow-x-auto mb-10">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Flag ID</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Reason</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Status</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Admin Notes</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Created At</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Reviewed At</th>
              </tr>
            </thead>

            <tbody className="bg-white divide-y divide-gray-100">
              {flags.length > 0 ? (
                flags.map((f) => (
                  <tr key={f.id}>
                    <td className="px-6 py-4 text-sm text-gray-700">{f.id}</td>
                    <td className="px-6 py-4 text-sm text-gray-700">{f.reason}</td>
                    <td
                      className={`px-6 py-4 text-sm font-semibold ${
                        f.status === "PENDING"
                          ? "text-yellow-600"
                          : f.status === "RESOLVED"
                          ? "text-green-600"
                          : "text-gray-500"
                      }`}
                    >
                      {f.status}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {f.adminNotes || "—"}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-500">
                      {formatDate(f.createdAt)}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-500">
                      {f.reviewedAt ? formatDate(f.reviewedAt) : "—"}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan="6"
                    className="px-6 py-4 text-center text-sm text-gray-500"
                  >
                    No flagged reviews found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* === Recent Admin Actions === */}
        <h2 className="text-xl font-semibold text-gray-800 mb-4">
          Your Recent Actions
        </h2>
        <div className="bg-white rounded-xl shadow overflow-x-auto mb-10">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Action Type</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Target</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Details</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Timestamp</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-100">
              {recentLogs.length > 0 ? (
                recentLogs.map((log) => (
                  <tr key={log.id}>
                    <td className="px-6 py-4 text-sm text-gray-700">
                      {log.actionType}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {log.targetType} #{log.targetId ?? "—"}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-500">
                      {log.details ?? "—"}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-500">
                      {formatDate(log.timestamp)}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan="4"
                    className="px-6 py-4 text-center text-sm text-gray-500"
                  >
                    No recent actions found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* === All Admin Logs === */}
        <h2 className="text-xl font-semibold text-gray-800 mb-4">
          All Admin Activity Logs
        </h2>
        <div className="bg-white rounded-xl shadow overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Admin</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Action Type</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Target</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Details</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Timestamp</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-100">
              {allLogs.length > 0 ? (
                allLogs.map((log) => (
                  <tr key={log.id}>
                    <td className="px-6 py-4 text-sm text-gray-700">
                      {log.adminUsername}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-700">
                      {log.actionType}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {log.targetType} #{log.targetId ?? "—"}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-500">
                      {log.details ?? "—"}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-500">
                      {formatDate(log.timestamp)}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan="5"
                    className="px-6 py-4 text-center text-sm text-gray-500"
                  >
                    No admin logs found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </main>

      {/* === Moderation Redirect Section === */}
      <div className="mt-12 flex justify-center">
        <div className="bg-white shadow-md rounded-xl p-6 text-center w-full sm:w-2/3 lg:w-1/2">
          <h2 className="text-lg font-semibold text-gray-800 mb-2">
            Need to moderate flagged reviews?
          </h2>
          <p className="text-sm text-gray-600 mb-4">
            Go to the moderation panel to resolve, hide, or delete reviews.
          </p>
          <a
            href="/admin/review-moderation"
            className="inline-block px-5 py-2 bg-green-600 text-white rounded-md text-sm font-medium hover:bg-green-700 transition"
          >
            Go to Moderation Page →
          </a>
        </div>
      </div>
    </div>
  );
}
