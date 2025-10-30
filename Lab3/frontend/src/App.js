import { Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Explore from "./pages/Explore";
import Profile from "./pages/Profile";
import Rewards from "./pages/Rewards";
import DetailsPage from "./pages/DetailsPage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/explore" element={<Explore />} />
      <Route path="/profile" element={<Profile />} />
      <Route path="/rewards" element={<Rewards />} />
      <Route path="/details/:id" element={<DetailsPage />} />
    </Routes>
  );
}
