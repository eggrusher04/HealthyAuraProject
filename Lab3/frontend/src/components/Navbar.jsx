import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

/**
 * Navigation bar component for the HealthyAura web application.
 *
 * <p>The `Navbar` component dynamically renders navigation links based on the
 * authenticated user's role and login state. It supports both general users
 * and administrative users with additional access to management tools such
 * as the Admin Dashboard, Tag Manager, and Create Admin page.</p>
 *
 * <p>Key functionalities include:
 * <ul>
 *   <li>Dynamic visibility of links depending on authentication state</li>
 *   <li>Conditional rendering of admin-specific routes</li>
 *   <li>Sign-in and sign-out navigation management</li>
 *   <li>Integration with {@link useAuth} context for session control</li>
 * </ul>
 * </p>
 *
 * @component
 * @example
 * // Displays the global navbar across pages
 * <Navbar />
 *
 * @returns {JSX.Element} The rendered navigation bar for authenticated and guest users.
 *
 * @since 2025-11-07
 * @version 1.0
 */
export default function Navbar() {
  const { user, signOut } = useAuth();

  return (
    <nav className="bg-white border-b shadow-sm">
      <div className="max-w-4xl mx-auto px-4 py-3 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Link to="/" className="text-green-700 font-bold text-xl">
            HealthyAura
          </Link>
          <Link to="/explore" className="text-sm text-gray-600">
            Explore
          </Link>
          <Link to="/rewards" className="text-sm text-gray-600">
            Rewards
          </Link>

          {user?.role === 'ADMIN' && (
            <Link
              to="/auth/admin/signup"
              className="text-sm font-semibold text-green-700 hover:underline"
            >
              Create Admin
            </Link>
          )}
          {user?.role === 'ADMIN' && (
            <Link
              to="/admin/dashboard"
              className="text-sm font-semibold text-green-700 hover:underline"
            >
              Activity Logs
            </Link>
          )}
          {user?.role === "ADMIN" && (
            <>
              <a
                href="/admin/tags"
                className="text-sm font-semibold text-green-700 hover:underline"
              >
                Tag Manager
              </a>
            </>
          )}
        </div>

        <div className="flex items-center gap-4">
          {user ? (
            <>
              <div className="text-sm text-gray-700">Hi, {user.username}</div>
              <Link to="/profile" className="text-sm text-green-700">
                Profile
              </Link>
              <button
                onClick={() => {
                  signOut();
                  window.location.href = "/auth";
                }}
                className="text-sm text-red-500"
              >
                Sign out
              </button>
            </>
          ) : (
            <>
              {/* Sign In Button */}
              <Link
                to="/auth"
                className="text-sm text-green-700 font-semibold hover:underline"
              >
                Sign In
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
