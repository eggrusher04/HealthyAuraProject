import React, { createContext, useContext, useEffect, useState } from 'react';
import API from "../services/api";

const AuthContext = createContext();
export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [lockInfo, setLockInfo] = useState({}); // {username: {attempts, lockedUntil}}

  useEffect(() => {
    const savedUser = localStorage.getItem('healthyaura_user');
    if (savedUser) {
        const parsed = JSON.parse(savedUser);
        setUser(parsed);
        if(parsed.token){
            API.defaults.headers.common["Authorization"] = 'Bearer ${parsed.token}';
        }
    }
  }, []);

  const signIn = async (username, password) => {
    // client-side check for lockout
    const info = lockInfo[username];
    if (info && info.lockedUntil && new Date(info.lockedUntil) > new Date()) {
      throw new Error('Account locked. Try later.');
    }

    try{
        const res = await API.post("/auth/login", { "username": username, "password": password, });
        console.log("Login response",res.data);
        const { token, username: uname, role } = res.data;

        if(!token) throw new Error("Invalid response from server");

        const userData = { username: uname, role };

        localStorage.setItem("token", token);
        localStorage.setItem("healthyaura_user", JSON.stringify(userData));

        API.defaults.headers.common["Authorization"] = 'Bearer ${token}'
        setUser(userData);
        setLockInfo((s) => ({ ...s, [username]: { attempts: 0} }));
        return userData;
    } catch(err){
        // Failed login attempt → increment
              setLockInfo((s) => {
                const curr = s[username] || { attempts: 0 };
                const attempts = curr.attempts + 1;
                const lockedUntil =
                  attempts >= 3
                    ? new Date(Date.now() + 30 * 60 * 1000).toISOString()
                    : curr.lockedUntil;
                return { ...s, [username]: { attempts, lockedUntil } };
              });
              throw new Error("The username or password is incorrect. Please try again.");
    }

  };

  const signOut = () => {
    localStorage.removeItem("token");
    localStorage.removeItem('healthyaura_user');
    delete API.defaults.headers.common["Authorization"];
    setUser(null);
  };

  const signUp = async (payload) => {
    const res = await API.post("/auth/signup", payload);
    console.log("Signup response:", res.data);

    const { token, username, role } = res.data;
    const userData = { username, role };

    localStorage.setItem("token", token);
    localStorage.setItem("healthyaura_user", JSON.stringify(userData));

    API.defaults.headers.common["Authorization"] = 'Bearer ${token}';
    setUser(userData);

    return res.data;
  };

  return (
    <AuthContext.Provider value={{ user, signIn, signOut, signUp }}>
      {children}
    </AuthContext.Provider>
  );
}
