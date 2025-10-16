import React, { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { mockApi } from '../services/mockApi';

export default function Home(){
  const { user } = useAuth();
  const [recommend, setRecommend] = useState(null);
  const [dismissedToday, setDismissedToday] = useState(false);

  useEffect(()=>{
    // check localStorage for dismissed popup today
    const d = localStorage.getItem('rec_dismiss_date');
    if (d === new Date().toDateString()) setDismissedToday(true);

    if (user && !dismissedToday) {
      // fetch one recommendation candidate based on user preferences
      mockApi.getStalls({ tags: user?.diet ? [user.diet] : [] }).then(r => {
        if (r.stalls && r.stalls.length) setRecommend(r.stalls[0]);
      });
    }
  },[user,dismissedToday]);

  const dismiss = () => {
    localStorage.setItem('rec_dismiss_date', new Date().toDateString());
    setDismissedToday(true);
  };

  return (
    <div>
      <h1 className="text-2xl font-semibold mb-4">Recommended for you</h1>
      {user ? (
        <>
          <div className="grid grid-cols-1 gap-3">
            {/* show a few cards (mock) */}
            <div className="p-4 bg-white rounded-lg shadow">
              <div className="text-lg font-medium">Top picks near you</div>
              <div className="text-sm text-gray-500">Based on your preferences</div>
            </div>
          </div>

          {recommend && !dismissedToday && (
            <div className="fixed bottom-6 right-6 w-80 bg-white border p-4 rounded-lg shadow-lg">
              <div className="flex justify-between items-start">
                <div>
                  <div className="font-semibold">{recommend.name}</div>
                  <div className="text-xs text-gray-500">{recommend.center} • {recommend.price ? `Under $${recommend.price}` : 'Unavailable'}</div>
                </div>
                <button onClick={dismiss} className="text-sm text-gray-500">Dismiss</button>
              </div>
              <div className="mt-3 flex gap-2">
                <button className="flex-1 border rounded py-1 text-sm">View</button>
                <button className="flex-1 bg-green-600 text-white rounded py-1 text-sm">Get Directions</button>
              </div>
            </div>
          )}
        </>
      ) : (
        <div className="p-4 bg-white rounded shadow">Please sign in to see recommendations.</div>
      )}
    </div>
  );
}
