import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import Layout from "./components/Layout";
import Home from "./pages/Home";
import Explore from "./pages/Explore";
import Rewards from "./pages/Rewards";
import Profile from "./pages/Profile";
import Auth from "./pages/Auth";
import DetailsPage from "./pages/DetailsPage";
import AdminAuth from "./pages/AdminAuth";
import AdminDashboard from "./pages/AdminDashboard";
import AdminModeration from "./pages/AdminModeration";
import AdminTagManager from "./pages/AdminTagManager";
import { AuthProvider, useAuth } from "./contexts/AuthContext";

/**
 * Protected route wrapper
 *
 * Ensures that only authenticated users can access certain routes (e.g., Profile).
 * If the authentication state is still loading, a loading screen is shown.
 * If the user is not logged in, they are redirected to the login page.
 */
function Protected({ children }) {
  const { user, loadingUser } = useAuth();

  if (loadingUser) {
    return (
      <div className="flex justify-center items-center min-h-screen text-gray-600">
        Loading...
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/auth" replace />;
  }

  return children;
}

/**
 * Main application component
 *
 * Defines all routes, including protected and admin routes.
 * Wraps the app with the AuthProvider for global authentication context.
 * Displays the Navbar across all pages.
 */
export default function App() {
  return (
    <AuthProvider>
      <div className="min-h-screen bg-gray-50">
        <Layout>
        <main className="p-4 max-w-4xl mx-auto">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/explore" element={<Explore />} />
            <Route path="/rewards" element={<Rewards />} />
            <Route path="/profile" element={<Protected><Profile /></Protected>} />
            <Route path="/auth" element={<Auth />} />
            <Route path="/details/:id" element={<DetailsPage />} />
            <Route path="/auth/admin/signup" element={<AdminAuth />} />
            <Route path="/admin/dashboard" element={<AdminDashboard />} />
            <Route path="/admin/review-moderation" element={<AdminModeration />} />
            <Route path="/admin/tags" element={<AdminTagManager />} />
            <Route path="*" element={<div>Not Found</div>} />
          </Routes>
        </main>
        </Layout>
      </div>
    </AuthProvider>
  );
}
