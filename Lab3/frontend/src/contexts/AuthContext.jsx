import React, { createContext, useContext, useEffect, useState } from 'react';
import API from "../services/api";

const AuthContext = createContext();
export const useAuth = () => useContext(AuthContext);

/**
 * Authentication provider component for managing user sessions, login state, and secure API access.
 *
 * <p>The `AuthProvider` component wraps the application and exposes authentication-related
 * methods and state (user, loading, signIn, signOut, etc.) via React Context.
 * It maintains persistent sessions through `localStorage` and integrates JWT-based
 * authentication for all authorized API requests.</p>
 *
 * <p>Key functionalities include:
 * <ul>
 *   <li>JWT-based token management and persistence</li>
 *   <li>Automatic user profile fetching and caching</li>
 *   <li>Account lockout logic after failed login attempts</li>
 *   <li>Global `useAuth()` hook for child components</li>
 * </ul>
 * </p>
 *
 * @component
 * @example
 * // Wraps the entire app to provide authentication state
 * <AuthProvider>
 *   <App />
 * </AuthProvider>
 *
 * // Access inside any child component
 * const { user, signIn, signOut, signUp } = useAuth();
 *
 * @param {Object} props - React component props.
 * @param {React.ReactNode} props.children - Child components that require authentication context.
 * @returns {JSX.Element} The authentication provider context wrapper.
 *
 * @since 2025-11-07
 * @version 1.0
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [lockInfo, setLockInfo] = useState({});
  const [loadingUser, setLoadingUser] = useState(true); // controls global auth loading

  /**
   * Fetches the authenticated user's full profile from the backend.
   *
   * <p>This method merges basic authentication details with additional
   * profile information retrieved from `/profile/me`.</p>
   *
   * @async
   * @param {string} token - The JWT authentication token.
   * @param {Object} basicUserData - Basic user data (username, role, token).
   * @returns {Promise<void>}
   */
  const fetchUserProfile = async (token, basicUserData) => {
    try {
      API.defaults.headers.common["Authorization"] = `Bearer ${token}`;
      const res = await API.get("/profile/me");
      const fullUserData = { ...basicUserData, ...res.data, token };
      localStorage.setItem("healthyaura_user", JSON.stringify(fullUserData));
      setUser(fullUserData);
    } catch (err) {
      console.error("Error fetching full user profile:", err);
      // fallback to minimal data
      setUser(basicUserData);
    } finally {
      setLoadingUser(false);
    }
  };

  /**
   * Automatically restores user session from local storage when the app loads.
   * If a valid token exists, refreshes user data from the backend.
   */
  useEffect(() => {
    const token = localStorage.getItem("token");
    const savedUser = localStorage.getItem("healthyaura_user");

    if (token && savedUser) {
      try {
        const parsed = JSON.parse(savedUser);
        if (parsed && parsed.username) {
          setUser(parsed); // show cached user immediately
          fetchUserProfile(token, parsed); // refresh in background
        } else {
          localStorage.removeItem("healthyaura_user");
          setLoadingUser(false);
        }
      } catch (e) {
        console.error("Error parsing saved user:", e);
        localStorage.removeItem("healthyaura_user");
        setLoadingUser(false);
      }
    } else {
      setLoadingUser(false);
    }
  }, []);

  /**
   * Attempts to authenticate the user with the provided credentials.
   *
   * <p>Implements a basic account lockout policy after three failed attempts.
   * On successful login, stores the token and user info in local storage and sets
   * the Authorization header for future requests.</p>
   *
   * @async
   * @param {string} username - The username of the user.
   * @param {string} password - The user's password.
   * @throws {Error} If authentication fails or account is locked.
   * @returns {Promise<Object>} The authenticated user's basic information.
   */
  const signIn = async (username, password) => {
    const info = lockInfo[username];
    if (info && info.lockedUntil && new Date(info.lockedUntil) > new Date()) {
      throw new Error("Account locked. Try later.");
    }

    try {
      const res = await API.post("/auth/login", { username, password });
      const { token, username: uname, role } = res.data;

      if (!token) throw new Error("Invalid server response");

      const basicUserData = { username: uname, role: role || "USER", token };
      localStorage.setItem("token", token);

      await fetchUserProfile(token, basicUserData);

      setLockInfo((s) => ({ ...s, [username]: { attempts: 0 } }));
      return basicUserData;
    } catch (err) {
      console.error("Login error:", err);
      setLockInfo((s) => {
        const curr = s[username] || { attempts: 0 };
        const attempts = curr.attempts + 1;
        const lockedUntil =
          attempts >= 3
            ? new Date(Date.now() + 30 * 60 * 1000).toISOString()
            : curr.lockedUntil;
        return { ...s, [username]: { attempts, lockedUntil } };
      });

      if (err.response && err.response.data) {
        throw new Error(err.response.data.message || "Login failed");
      } else {
        throw new Error("The username or password is incorrect. Please try again.");
      }
    }
  };

  /**
   * Logs the user out by clearing stored credentials and removing
   * the authorization header from all API requests.
   *
   * @returns {void}
   */
  const signOut = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("healthyaura_user");
    delete API.defaults.headers.common["Authorization"];
    setUser(null);
  };

  /**
   * Registers a new user in the system.
   *
   * <p>This function calls the backend `/auth/signup` endpoint
   * and returns the server’s response for post-registration handling.</p>
   *
   * @async
   * @param {Object} payload - Registration form data.
   * @returns {Promise<Object>} The server response after signup.
   */
  const signUp = async (payload) => {
    const res = await API.post("/auth/signup", payload);
    console.log("Signup Response:", res.data);
    return res.data;
  };

  return (
    <AuthContext.Provider value={{ user, signIn, signOut, signUp, loadingUser }}>
      {children}
    </AuthContext.Provider>
  );
}
