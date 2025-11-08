import React, { useState } from "react";
import { ArrowLeft, Search, Star, UserCircle } from "lucide-react";
import { useParams, Link } from "react-router-dom";

/**
 * Component for submitting a new user review and rating for a specific eatery.
 *
 * <p>The `LeaveReview` page allows authenticated users to rate an eatery and write feedback.
 * It includes a 5-star interactive rating selector, text input area, and a sample
 * existing review display for visual context. Once the form is submitted, the user's
 * rating and review text are logged (placeholder for backend integration).</p>
 *
 * <p>Key functionalities include:
 * <ul>
 *   <li>Dynamic route handling via React Router's {@link useParams} (to identify the eatery ID)</li>
 *   <li>Interactive rating component using state hooks for hover and selection</li>
 *   <li>Form submission handler that logs data to console (mock-up stage)</li>
 *   <li>Static example section showing how submitted reviews will appear</li>
 *   <li>Navigation link back to the eatery details page</li>
 * </ul>
 * </p>
 *
 * @component
 * @example
 * // Used in routing within HealthyAura frontend
 * <Route path="/eatery/:id/review" element={<LeaveReview />} />
 *
 * @returns {JSX.Element} The rendered form and UI for submitting an eatery review.
 *
 * @since 2025-11-07
 * @version 1.0
 */
export default function LeaveReview() {
  const { id } = useParams();

  const [rating, setRating] = useState(0);
  const [hover, setHover] = useState(0);
  const [reviewText, setReviewText] = useState("");

  /**
   * Handles review submission.
   *
   * <p>Currently logs review data to the console.
   * In a complete implementation, this would call the backend API
   * (e.g., `POST /reviews`) to persist the review in the database.</p>
   *
   * @param {React.FormEvent} e - The form submission event.
   * @returns {void}
   */
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
