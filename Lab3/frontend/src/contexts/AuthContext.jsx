import React, { createContext, useContext, useEffect, useState } from 'react';
import API from "../services/api";

const AuthContext = createContext();
export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [lockInfo, setLockInfo] = useState({});
  const [loadingUser, setLoadingUser] = useState(true); // controls global auth loading

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

  const signOut = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("healthyaura_user");
    delete API.defaults.headers.common["Authorization"];
    setUser(null);
  };

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
