import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';

export default function Profile(){
  const { user, signOut, signUp } = useAuth();
  const [form, setForm] = useState({ name: user?.username, email: user?.email, phone: user?.phone || '', diet: user?.diet || '' });

  const save = async () => {
    // call backend to update; here we simulate
    alert('Profile saved (demo). Implement API call to persist changes.');
  };

  return (
    <div className="max-w-xl">
      <h2 className="text-xl font-semibold mb-4">Edit Personal Information</h2>
      <div className="bg-white p-4 rounded shadow">
        <label className="block text-xs text-gray-600">Name</label>
        <input value={form.name} onChange={e=>setForm({...form, name:e.target.value})} className="w-full p-2 border rounded mb-2" />
        <label className="block text-xs text-gray-600">Email</label>
        <input value={form.email} onChange={e=>setForm({...form, email:e.target.value})} className="w-full p-2 border rounded mb-2" />
        <label className="block text-xs text-gray-600">Phone</label>
        <input value={form.phone} onChange={e=>setForm({...form, phone:e.target.value})} className="w-full p-2 border rounded mb-2" />
        <label className="block text-xs text-gray-600">Dietary Preference</label>
        <select value={form.diet} onChange={e=>setForm({...form, diet:e.target.value})} className="w-full p-2 border rounded mb-4">
          <option value="">No preference</option>
          <option value="Vegan">Vegan</option>
          <option value="Vegetarian">Vegetarian</option>
          <option value="Halal">Halal</option>
        </select>

        <div className="flex gap-2">
          <button onClick={save} className="bg-green-600 text-white px-4 py-2 rounded">Save</button>
          <button onClick={signOut} className="px-4 py-2 border rounded">Sign out</button>
        </div>
      </div>
    </div>
  );
}
