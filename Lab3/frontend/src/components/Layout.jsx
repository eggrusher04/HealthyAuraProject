import React from "react";
import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { LogIn } from "lucide-react";

// Import the custom PNG icon images
import HomeIconImage from "../home-outline-circle.png";
import ExploreIconImage from "../explore-outline-circle.png";
import RewardsIconImage from "../rewards-outline-circle.png";
import ProfileIconImage from "../profile-outline-circle.png";

// Import admin icons
import CreateAdminIcon from "../create-admin-icon.png";
import DashboardIcon from "../dashboard-icon.png";
import ActivityLogsIcon from "../activity-logs-icon.png";
import TagManagerIcon from "../tag-manager-icon.png";

// Nav item component
const NavItem = ({ to, customIconSrc, label, isAdmin = false }) => {
  const location = useLocation();
  const isActive = location.pathname === to;

  return (
    <Link
      to={to}
      className={`flex flex-col items-center p-2 transition-colors ${
        isActive ? "text-green-700" : "text-gray-500 hover:text-green-600"
      }`}
    >
      <img
        src={customIconSrc}
        alt={label}
        className="w-8 h-8 mb-1"
      />
      <span className={`text-xs font-medium ${isActive ? 'text-green-700' : 'text-black'}`}>
        {label}
      </span>
    </Link>
  );
};

export default function Layout({ children }) {
  const { user, signOut } = useAuth();
  const location = useLocation();

  const isSpecialPage = location.pathname.startsWith("/details/") || location.pathname.startsWith("/review/");

  if (isSpecialPage) {
      return (
          <div className="min-h-screen bg-gray-50">
              {children}
          </div>
      );
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">

      {/* --- Top Header --- */}
      <header className="bg-white shadow-md sticky top-0 z-20">
        <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
          <Link to="/" className="text-2xl font-bold text-green-700 tracking-tight">
            HealthyAura
          </Link>

          <div className="flex items-center space-x-4 text-sm">
            {user ? (
              <>
                <span className="text-gray-700">
                  Hi, {user.username} {user?.role === 'ADMIN' && '(Admin)'}
                </span>
                <button
                  onClick={signOut}
                  className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 transition"
                >
                  Sign Out
                </button>
              </>
            ) : (
              <Link to="/auth" className="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700 transition flex items-center gap-1">
                <LogIn size={16} />
                Sign In
              </Link>
            )}
          </div>
        </div>
      </header>

      {/* --- Main Content --- */}
      <main className="flex-1 max-w-4xl mx-auto w-full pb-24">{children}</main>

      {/* --- Bottom Navigation - 8 Icons --- */}
      <nav className="fixed bottom-0 left-0 right-0 bg-white border-t shadow-lg z-30">
        <div className="max-w-4xl mx-auto flex">
          {/* Regular User Icons (Left Side) */}
          <div className="flex flex-1 justify-around">
            <NavItem
              to="/"
              customIconSrc={HomeIconImage}
              label="Home"
            />
            <NavItem
              to="/explore"
              customIconSrc={ExploreIconImage}
              label="Explore"
            />
            <NavItem
              to="/rewards"
              customIconSrc={RewardsIconImage}
              label="Rewards"
            />
            <NavItem
              to="/profile"
              customIconSrc={ProfileIconImage}
              label="Profile"
            />
          </div>

          {/* Admin Icons (Right Side) - Only show for admin users */}
          {user?.role === 'ADMIN' && (
            <>
              {/* Vertical separator line */}
              <div className="border-l border-gray-200 my-2"></div>

              {/* Admin icons container */}
              <div className="flex flex-1 justify-around">
                <NavItem
                  to="/auth/admin/signup"
                  customIconSrc={CreateAdminIcon}
                  label="Create Admin"
                />
                <NavItem
                  to="/admin/dashboard"
                  customIconSrc={DashboardIcon}
                  label="Dashboard"
                />
                <NavItem
                  to="/admin/review-moderation"
                  customIconSrc={ActivityLogsIcon}
                  label="Activity"
                />
                <NavItem
                  to="/admin/tags"
                  customIconSrc={TagManagerIcon}
                  label="Tags"
                />
              </div>
            </>
          )}
        </div>
      </nav>
    </div>
  );
}