import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Explore from './pages/Explore';
import Rewards from './pages/Rewards';
import Profile from './pages/Profile';
import Auth from './pages/Auth';
import DetailsPage from './pages/DetailsPage';
import AdminAuth from './pages/AdminAuth';
import AdminDashboard from './pages/AdminDashboard';
import AdminModeration from './pages/AdminModeration';

import { AuthProvider, useAuth } from './contexts/AuthContext';

function Protected({ children }) {
  const { user } = useAuth();
  return user ? children : <Navigate to="/auth" replace />;
}

export default function App(){
  return (
    <AuthProvider>
      <div className="min-h-screen bg-gray-50">
        <Navbar />
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
            <Route path="*" element={<div>Not Found</div>} />
          </Routes>
        </main>
      </div>
    </AuthProvider>
  );
}
