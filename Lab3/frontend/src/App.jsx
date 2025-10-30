import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Explore from './pages/Explore';
import Rewards from './pages/Rewards';
import Profile from './pages/Profile';
import Auth from './pages/Auth';
import DetailsPage from './components/DetailsPage';
import CrowdQueueStatus from './components/CrowdQueueStatus';
import LeaveReview from './components/LeaveReview';

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

            <Route path="/eatery/:id" element={<DetailsPage />} />
            <Route path="/eatery/:id/status" element={<CrowdQueueStatus />} />
            <Route path="/eatery/:id/review" element={<LeaveReview />} />

            <Route path="*" element={<div>Not Found</div>} />
          </Routes>
        </main>
      </div>
    </AuthProvider>
  );
}
