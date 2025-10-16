import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function Navbar(){
  const { user, signOut } = useAuth();
  return (
    <nav className="bg-white border-b shadow-sm">
      <div className="max-w-4xl mx-auto px-4 py-3 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Link to="/" className="text-green-700 font-bold text-xl">HealthyAura</Link>
          <Link to="/explore" className="text-sm text-gray-600">Explore</Link>
          <Link to="/rewards" className="text-sm text-gray-600">Rewards</Link>
        </div>
        <div className="flex items-center gap-4">
          {user ? (
            <>
              <div className="text-sm text-gray-700">Hi, {user.username}</div>
              <Link to="/profile" className="text-sm text-green-700">Profile</Link>
              <button onClick={signOut} className="text-sm text-red-500">Sign out</button>
            </>
          ) : (
            <div className="text-sm text-gray-700">Not signed in</div>
          )}
        </div>
      </div>
    </nav>
  );
}
