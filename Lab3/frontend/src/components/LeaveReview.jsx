import React, { useState } from "react";
import { ArrowLeft, Search, Star, UserCircle } from "lucide-react";
import { useParams, Link } from "react-router-dom";

export default function LeaveReview() {
  const { id } = useParams();

  const [rating, setRating] = useState(0);
  const [hover, setHover] = useState(0);
  const [reviewText, setReviewText] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Submitted Review:", { rating, reviewText });
    setReviewText("");
    setRating(0);
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* Header */}
      <header className="bg-white shadow-sm flex items-center justify-between px-4 py-3 border-b">
        <div className="flex items-center gap-2 text-green-700">
          <ArrowLeft size={22} />
          <h1 className="text-xl font-semibold text-green-700">HealthyAura</h1>
        </div>
        <Search size={22} className="text-green-700" />
      </header>

      {/* Content */}
      <main className="flex-1 p-4">
        <h2 className="text-xl font-semibold text-gray-800 text-center mb-1">
          Leave a Review
        </h2>
        <h3 className="text-lg font-bold text-green-700 text-center mb-4">
          Green Eats
        </h3>

        {/* Static example rating (top stars) */}
        <div className="flex justify-center mb-6">
          {[...Array(5)].map((_, i) => (
            <Star
              key={i}
              size={26}
              className={`${i < 4 ? "text-green-500 fill-green-500" : "text-gray-300"}`}
            />
          ))}
        </div>

        {/* Review Form */}
        <form onSubmit={handleSubmit} className="bg-white rounded-xl p-4 shadow-sm">
          <label className="block text-gray-700 font-medium mb-1">
            Your review
          </label>
          <textarea
            className="w-full border border-gray-300 rounded-lg p-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            rows="3"
            placeholder="Write your review here..."
            value={reviewText}
            onChange={(e) => setReviewText(e.target.value)}
          ></textarea>

          <label className="block text-gray-700 font-medium mt-4 mb-1">
            Rating
          </label>
          <div className="flex mb-4">
            {[...Array(5)].map((_, i) => {
              const ratingValue = i + 1;
              return (
                <button
                  type="button"
                  key={i}
                  onClick={() => setRating(ratingValue)}
                  onMouseEnter={() => setHover(ratingValue)}
                  onMouseLeave={() => setHover(rating)}
                  className="focus:outline-none"
                >
                  <Star
                    size={28}
                    className={`${
                      ratingValue <= (hover || rating)
                        ? "text-green-500 fill-green-500"
                        : "text-gray-300"
                    }`}
                  />
                </button>
              );
            })}
          </div>

          <button
            type="submit"
            className="w-full bg-green-600 text-white font-semibold py-2 rounded-lg hover:bg-green-700 transition"
          >
            Submit
          </button>
        </form>

        {/* Example Review Display */}
        <div className="mt-6 bg-white rounded-xl p-4 shadow-sm flex items-start gap-3">
          <UserCircle size={40} className="text-green-600 flex-shrink-0" />
          <div>
            <p className="font-semibold text-gray-800">Alex P.</p>
            <p className="text-gray-600 text-sm">
              This place has great healthy food options!
            </p>
            <div className="flex mt-2">
              {[...Array(5)].map((_, i) => (
                <Star
                  key={i}
                  size={18}
                  className="text-green-500 fill-green-500"
                />
              ))}
            </div>
          </div>
        </div>

        {/* Back link */}
        <Link to={`/eatery/${id}`} className="text-green-600 underline mt-4 block">
          ← Back to Eatery Details
        </Link>
      </main>
    </div>
  );
}
