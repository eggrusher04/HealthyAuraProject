import React from "react";
import BottomNav from "./BottomNav";

export default function DetailsPage() {
//Hard coded values based off UI, make sure to call api where required  when integrating 

  const places = [
    {
      name: "Green Eats Hawker",
      address: "123 Jurong East St., Singapore 60023",
      tags: ["Vegan", "Budget Friendly"],
      wait: "~10 mins wait",
      price: "$",
      img: "https://images.unsplash.com/photo-1604908177225-83c9e8a6a2b4?auto=format&fit=crop&w=400&q=80"
    },
    {
      name: "Healthy Bites",
      address: "456 Bukit Merah Lane 1, Singapore 150456",
      tags: ["Vegan", "Gluten-Free"],
      wait: "~5 mins wait",
      price: "$$",
      img: "https://images.unsplash.com/photo-1601050690597-df8ffcd1d14a?auto=format&fit=crop&w=400&q=80"
    },
    {
      name: "NutriHub",
      address: "789 Kallang Rd., Singapore 339328",
      tags: ["Vegan", "HALAL"],
      wait: "~8 mins wait",
      price: "$$",
      img: "https://images.unsplash.com/photo-1589307004391-2f9b4a18a38a?auto=format&fit=crop&w=400&q=80"
    },
  ];

  return (
    <div className="pb-20">
      <h1 className="text-center text-green-700 text-2xl font-semibold mt-4">HealthyAura</h1>

      <div className="px-4 mt-4">
        <input
          type="text"
          placeholder="Search by stall name, hawker centre, or location"
          className="w-full border border-gray-300 rounded-lg p-2 focus:outline-green-500"
        />

        {/* Tags */}
        <div className="flex flex-wrap gap-2 mt-3">
          {["Vegetarian", "High Protein", "Vegan", "Budget Friendly", "Healthy Options"].map((tag, i) => (
            <span key={i} className="px-3 py-1 bg-green-100 text-green-700 text-sm rounded-full">
              {tag}
            </span>
          ))}
        </div>

        {/* Sort and Filter */}
        <div className="flex justify-between mt-4">
          <select className="border border-gray-300 rounded-lg p-2 text-sm">
            <option>Sort by</option>
            <option>Distance</option>
            <option>Wait Time</option>
          </select>
          <button className="border border-gray-300 rounded-lg px-3 py-2 text-sm">Filter ▾</button>
        </div>

        {/* Food Places */}
        <div className="mt-5 space-y-4">
          {places.map((place, idx) => (
            <div key={idx} className="bg-white rounded-xl shadow-sm border p-3">
              <div className="flex gap-3">
                <img
                  src={place.img}
                  alt={place.name}
                  className="w-20 h-20 object-cover rounded-lg"
                />
                <div className="flex-1">
                  <h2 className="font-semibold">{place.name}</h2>
                  <p className="text-xs text-gray-500">{place.address}</p>
                  <p className="text-xs text-gray-600 mt-1">
                    {place.tags.join(", ")}
                  </p>
                  <p className="text-xs text-gray-500 mt-1">
                    {place.price} • {place.wait}
                  </p>
                </div>
              </div>
              <button className="w-full bg-green-600 text-white mt-3 py-2 rounded-lg hover:bg-green-700 transition">
                Get Directions
              </button>
            </div>
          ))}
        </div>
      </div>

      <BottomNav />
    </div>
  );
}
