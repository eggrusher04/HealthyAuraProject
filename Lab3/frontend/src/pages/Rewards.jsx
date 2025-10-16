import React, { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { mockApi } from '../services/mockApi';

const sampleRewards = [
  { id: 'r1', title: '$10 Voucher', cost: 500, expiry: '2025-12-31' },
  { id: 'r2', title: 'Spa Treatment 10% off', cost: 1200, expiry: '2025-06-30' }
];

export default function Rewards(){
  const { user } = useAuth();
  const [points, setPoints] = useState(0);

  useEffect(()=> {
    if (user) mockApi.getUserPoints(user.username).then(r => setPoints(r.points));
  },[user]);

  const redeem = async (r) => {
    const res = await mockApi.redeemReward(user.username, r);
    if (res.success) {
      alert('Reward redeemed successfully.');
      setPoints(res.newBalance);
    } else {
      alert(res.message || 'Not enough points.');
    }
  };

  if (!user) return <div>Please sign in to view rewards.</div>;

  return (
    <div>
      <div className="bg-white p-4 rounded shadow mb-4">
        <div className="text-sm text-gray-500">Your Points Balance</div>
        <div className="text-2xl font-bold">{points}</div>
      </div>

      <div className="grid gap-3">
        {sampleRewards.map(r => (
          <div key={r.id} className="bg-white p-4 rounded shadow flex justify-between items-center">
            <div>
              <div className="font-semibold">{r.title}</div>
              <div className="text-xs text-gray-500">Cost: {r.cost} pts • Expires: {r.expiry}</div>
            </div>
            <div>
              <button disabled={points < r.cost} onClick={()=>redeem(r)} className={`px-3 py-1 rounded ${points >= r.cost ? 'bg-green-600 text-white' : 'bg-gray-200 text-gray-500'}`}>Redeem</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
