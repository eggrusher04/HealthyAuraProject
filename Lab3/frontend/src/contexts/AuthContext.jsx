import React, { createContext, useContext, useEffect, useState } from 'react';
import API from "../services/api";

const AuthContext = createContext();
export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [lockInfo, setLockInfo] = useState({});
    const [loadingUser, setLoadingUser] = useState(true); // New state to manage loading

    const fetchUserProfile = async (token, basicUserData) => {
        try {
            API.defaults.headers.common["Authorization"] = `Bearer ${token}`;
            const res = await API.get("/profile/me");
            const fullUserData = { ...basicUserData, ...res.data, token }; // Merge basic and full data
            localStorage.setItem("healthyaura_user", JSON.stringify(fullUserData));
            setUser(fullUserData);
        } catch (err) {
            console.error("Error fetching full user profile:", err);
            // If fetching fails, still use the basic data from the token/localStorage to avoid redirecting
            setUser(basicUserData);
        }
    };

    useEffect(() => {
        const token = localStorage.getItem('token');
        const savedUser = localStorage.getItem('healthyaura_user');
        
        if (token && savedUser) {
            try {
                const parsed = JSON.parse(savedUser);
                if (parsed && parsed.username) {
                    // Set basic user data immediately
                    setUser(parsed);
                    // Fetch full profile (including email, etc.)
                    fetchUserProfile(token, parsed);
                } else {
                    localStorage.removeItem('healthyaura_user');
                    setLoadingUser(false);
                }
            } catch (e) {
                console.error("Error parsing saved user:", e);
                localStorage.removeItem('healthyaura_user');
                setLoadingUser(false);
            }
        } else {
            setLoadingUser(false);
        }
    }, []);

    const signIn = async (username, password) => {
        // client-side check for lockout
        const info = lockInfo[username];
        if (info && info.lockedUntil && new Date(info.lockedUntil) > new Date()) {
            throw new Error('Account locked. Try later.');
        }

        try {
            const res = await API.post("/auth/login", { "username": username, "password": password });
            console.log("Login response", res.data);
            // Assuming res.data contains { token, username, role }
            const { token, username: uname, role } = res.data;

            if(!token) throw new Error("Invalid response from server");

            // Basic user data from login response
            const basicUserData = { 
                username: uname, 
                role: role || 'USER', 
                token: token 
            };
            
            localStorage.setItem("token", token);
            
            // Fetch the full profile to get email/preferences and update state/local storage
            await fetchUserProfile(token, basicUserData); 

            setLockInfo((s) => ({ ...s, [username]: { attempts: 0 } }));
            return basicUserData;
        } catch(err) {
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
                throw new Error(err.response.data.message || err.response.data || "Login failed");
            } else if (err.message) {
                throw new Error(err.message);
            } else {
                throw new Error("The username or password is incorrect. Please try again.");
            }
        }
    };

    const signOut = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("healthyaura_user");
        localStorage.removeItem("adminToken");
        localStorage.removeItem("adminUsername");
        delete API.defaults.headers.common["Authorization"];
        setUser(null);
    };

    const signUp = async (payload) => {
        const res = await API.post("/auth/signup", payload);
        console.log("Signup response:", res.data);

        const { token, username, role } = res.data;
        const basicUserData = { username, role: role || 'USER', token };

        localStorage.setItem("token", token);
        
        // Fetch full profile after signup
        await fetchUserProfile(token, basicUserData); 

        return res.data;
    };

    // Only expose loadingUser (or a renamed 'loading' state) if needed outside, otherwise, Profile.jsx handles its own loading.
    return (
        <AuthContext.Provider value={{ user, signIn, signOut, signUp, loadingUser }}>
            {children}
        </AuthContext.Provider>
    );
}