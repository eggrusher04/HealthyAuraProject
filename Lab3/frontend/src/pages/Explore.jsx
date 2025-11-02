import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../services/api";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

export default function Explore() {
  const [q, setQ] = useState("");
  const [stalls, setStalls] = useState([]);
  const [tags, setTags] = useState([]);
  const [sortBy, setSortBy] = useState("distance");
  const [selectedStall, setSelectedStall] = useState(null);
  const [map, setMap] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    search();
  }, []);

  const toggleTag = (t) => {
    setTags((prev) =>
      prev.includes(t) ? prev.filter((x) => x !== t) : [...prev, t]
    );
  };

  const search = async () => {
    try {
      const params = {};
      if (q) params.query = q;
      if (tags.length > 0) params.tags = tags;

      const res = await API.get("/api/eateries/fetchDb", { params });

      if (Array.isArray(res.data)) {
        console.log("Fetched eateries:", res.data.length);
        setStalls(res.data);
      } else {
        console.warn("Unexpected data shape:", res.data);
        setStalls([]);
      }
    } catch (err) {
      console.error("Error fetching eateries:", err);
      setStalls([]);
    }
  };



  const showMap = (stall) => {
    setSelectedStall(stall);

    setTimeout(() => {
      if (map) map.remove();

      const newMap = L.map("onemap", {
        center: [stall.latitude, stall.longitude],
        zoom: 17,
      });

      L.tileLayer(
        "https://www.onemap.gov.sg/maps/tiles/Original/{z}/{x}/{y}.png",
        { attribution: "Map data © OneMap Singapore" }
      ).addTo(newMap);

      L.marker([stall.latitude, stall.longitude])
        .addTo(newMap)
        .bindPopup(`<b>${stall.name}</b><br>${stall.fullAddress || stall.address}`)
        .openPopup();

      setMap(newMap);
    }, 200);
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-semibold mb-4 text-green-800">
        Explore Eateries
      </h1>

      {/* Search bar */}
      <div className="flex gap-3 mb-3">
        <input
          value={q}
          onChange={(e) => setQ(e.target.value)}
          placeholder="Search by name or location"
          className="flex-1 p-2 border rounded"
        />
        <button
          onClick={search}
          className="bg-green-600 text-white px-4 rounded hover:bg-green-700"
        >
          Search
        </button>
      </div>

      {/* Tag filters and sorting */}
      <div className="flex flex-wrap items-center gap-3 mb-4">
        <div className="space-x-2">
          {["Vegan", "Vegetarian", "Halal", "Healthy", "High Protein"].map(
            (t) => (
              <button
                key={t}
                onClick={() => toggleTag(t)}
                className={`px-2 py-1 border rounded ${
                  tags.includes(t)
                    ? "bg-green-100 text-green-700 border-green-500"
                    : "bg-white text-gray-700"
                }`}
              >
                {t}
              </button>
            )
          )}
        </div>

        {tags.length > 0 && (
          <button
            onClick={search}
            className="ml-2 bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"
          >
            Apply Filters
          </button>
        )}
      </div>

      {/* Results */}
      {Array.isArray(stalls) && stalls.length > 0 ? (
        <div className="grid gap-3">
          {stalls.map((s) => (
            <div
              key={s.id}
              className="bg-white p-4 rounded shadow flex justify-between items-center hover:shadow-md transition"
            >
              <div>
                <div className="font-semibold text-green-800">{s.name}</div>
                <div className="text-xs text-gray-500">
                  {s.address || s.fullAddress}
                </div>

                {/* Tag display */}
                {Array.isArray(s.tags) && s.tags.length > 0 && (
                  <div className="mt-2 flex flex-wrap gap-1">
                    {s.tags.map((tag, i) => (
                      <span
                        key={i}
                        className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full"
                      >
                        {typeof tag === "string" ? tag : tag.tag}
                      </span>
                    ))}
                  </div>
                )}
              </div>

              <div className="text-right">
                <button
                  onClick={() => {
                    console.log("Navigating to /details/" + s.id);
                    navigate(`/details/${s.id}`);
                  }}
                  className="mt-2 bg-green-600 text-white text-xs px-3 py-1 rounded hover:bg-green-700"
                >
                  Get Directions
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="text-gray-500 text-sm">No results found.</div>
      )}

      {/* Map Display */}
      {selectedStall && (
        <div className="mt-6">
          <h2 className="text-lg font-semibold mb-2 text-green-800">
            Directions to {selectedStall.name}
          </h2>
          <div id="onemap" className="w-full h-96 rounded border shadow"></div>
        </div>
      )}
    </div>
  );
}
