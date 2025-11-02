import React from "react";
import { Home, Map, Gift, User } from "lucide-react";

export default function BottomNav() {
  const navItems = [
    { icon: <Home size={22} />, label: "Home" },
    { icon: <Map size={22} />, label: "Explore" },
    { icon: <Gift size={22} />, label: "Rewards" },
    { icon: <User size={22} />, label: "Profile" },
  ];

  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-green-700 text-white flex justify-around items-center py-2 rounded-t-2xl shadow-md">
      {navItems.map((item, idx) => (
        <button key={idx} className="flex flex-col items-center text-xs">
          {item.icon}
          <span>{item.label}</span>
        </button>
      ))}
    </nav>
  );
}
