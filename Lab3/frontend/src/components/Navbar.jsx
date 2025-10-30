import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

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
        </div>

        <div className="flex items-center gap-4">
          {user ? (
            <>
              <div className="text-sm text-gray-700">Hi, {user.username}</div>
              <Link to="/profile" className="text-sm text-green-700">
                Profile
              </Link>
              <button onClick={signOut} className="text-sm text-red-500">
                Sign out
              </button>
            </>
          ) : (
            <>
              {/* Admin Button - Changed style to fit the existing 'Sign In' button style */}
              <Link
                to="/admin/auth"
                className="text-sm text-gray-700 font-semibold hover:text-green-700" // Styled to look like the 'Admin' link in your screenshot
              >
                Admin
              </Link>

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