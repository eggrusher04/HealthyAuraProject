import React, { useEffect, useState } from 'react';
import { mockApi } from '../services/mockApi';

export default function Explore(){
  const [q, setQ] = useState('');
  const [stalls, setStalls] = useState([]);
  const [tags, setTags] = useState([]);
  const [sortBy, setSortBy] = useState('distance');

  useEffect(()=>{ search(); },[]);

  const toggleTag = (t) => {
    setTags(prev => prev.includes(t) ? prev.filter(x=>x!==t) : [...prev,t]);
  };

  const search = async () => {
    const res = await mockApi.getStalls({ q, tags, sortBy });
    if (res.success) setStalls(res.stalls);
  };

  return (
    <div>
      <div className="flex gap-3 mb-3">
        <input value={q} onChange={e=>setQ(e.target.value)} placeholder="Search by stall or centre" className="flex-1 p-2 border rounded" />
        <button onClick={search} className="bg-green-600 text-white px-4 rounded">Search</button>
      </div>

      <div className="flex gap-4 mb-4">
        <div className="space-x-2">
          {['Vegan','Vegetarian','Halal','Healthy','High Protein'].map(t=>(
            <button key={t} onClick={()=>toggleTag(t)} className={`px-2 py-1 border rounded ${tags.includes(t)?'bg-green-100':'bg-white'}`}>{t}</button>
          ))}
        </div>
        <select value={sortBy} onChange={e=>setSortBy(e.target.value)} className="p-2 border rounded">
          <option value="distance">Distance</option>
          <option value="price">Average price</option>
          <option value="popularity">Popularity</option>
        </select>
      </div>

      <div className="grid gap-3">
        {stalls.map(s=>(
          <div key={s.id} className="bg-white p-4 rounded shadow flex justify-between items-center">
            <div>
              <div className="font-semibold">{s.name}</div>
              <div className="text-xs text-gray-500">{s.center} • {s.address}</div>
              <div className="mt-2 text-xs">{s.tags.join(' • ')}</div>
            </div>
            <div className="text-right">
              <div className="text-sm">{s.price ? `$${s.price}` : 'Unavailable'}</div>
              <div className={`text-xs mt-2 ${s.open ? 'text-green-600' : 'text-red-500'}`}>{s.open ? 'Open' : 'Closed'}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
