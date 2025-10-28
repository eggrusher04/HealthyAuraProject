import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import BottomNav from "../components/BottomNav";
import API from "../services/api";

import L from "leaflet";
import "leaflet/dist/leaflet.css";

// fix default marker paths
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

export default function DetailsPage() {
  const { id } = useParams(); // /details/:id
  const navigate = useNavigate();
  const [stall, setStall] = useState(null);
  const [mapObj, setMapObj] = useState(null);

  // 1. fetch stall details from backend on mount
  useEffect(() => {
    const fetchStall = async () => {
      try {
        const res = await API.get(`/api/eateries/${id}`);
        setStall(res.data);
      } catch (err) {
        console.error("Failed to load eatery:", err);
      }
    };
    fetchStall();
  }, [id]);

  // 2. init map after we have stall
  useEffect(() => {
    if (!stall || !stall.latitude || !stall.longitude) return;

    if (mapObj) {
        mapObj.remove();
    }

    const map = L.map("details-map", {
      center: [stall.latitude, stall.longitude],
      zoom: 17,
    });

    L.tileLayer(
      "https://www.onemap.gov.sg/maps/tiles/Original/{z}/{x}/{y}.png",
      {
        attribution: "Map data © OneMap Singapore",
      }
    ).addTo(map);

    L.marker([stall.latitude, stall.longitude])
      .addTo(map)
      .bindPopup(
        `<b>${stall.name ?? "Selected Location"}</b><br>${stall.address ?? ""}`
      )
      .openPopup();

    setMapObj(map);

    return () => {
      map.remove();
    };
  }, [stall]);

  if (!stall) {
    return (
      <div className="p-4 text-center text-gray-500">
        Loading place details...
      </div>
    );
  }

  const displayName = stall.name || "Eatery";
  const displayAddr = stall.fullAddress || stall.address || "Address not available";
  const displayTags = stall.tags || [];

  return (
    <div className="pb-20">
      {/* top bar */}
      <div className="flex items-center justify-between p-4 border-b">
        <button
          onClick={() => navigate(-1)}
          className="text-green-700 font-medium"
        >
          ← Back
        </button>
        <h1 className="text-green-700 text-lg font-semibold">
          HealthyAura
        </h1>
        <div className="w-10" />
      </div>

      <div className="px-4 pt-4">
        <h2 className="text-xl font-semibold text-gray-900">{displayName}</h2>
        <p className="text-sm text-gray-500 mt-1">{displayAddr}</p>

        {displayTags.length > 0 && (
          <div className="flex flex-wrap gap-2 mt-3">
            {displayTags.map((tag, i) => (
              <span
                key={i}
                className="px-3 py-1 bg-green-100 text-green-700 text-xs rounded-full"
              >
                {tag}
              </span>
            ))}
          </div>
        )}

        {/* meta row */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mt-4 text-xs text-gray-600">

            <div className="flex gap-2 flex-wrap">
              <span className="px-2 py-1 bg-gray-100 rounded">Wait: ~N/A</span>
              <span className="px-2 py-1 bg-gray-100 rounded">Price: N/A</span>
            </div>
        </div>
      </div>

      {/* map */}
      <div className="px-4 mt-5">
        <div className="bg-white rounded-xl shadow-sm border p-3">
          <div
            id="details-map"
            className="w-full h-64 rounded-lg border"
          />
        </div>
      </div>

      {/* call to action */}
      <div className="px-4 mt-4">
        <button className="w-full bg-green-600 text-white py-3 rounded-lg hover:bg-green-700 transition text-sm font-medium">
          Open in Maps
        </button>
      </div>

      <BottomNav />
    </div>
  );
}
