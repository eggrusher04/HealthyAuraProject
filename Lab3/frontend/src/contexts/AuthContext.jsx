import React, { createContext, useContext, useEffect, useState } from 'react';
import { mockApi } from '../services/mockApi';

const AuthContext = createContext();
export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [lockInfo, setLockInfo] = useState({}); // {username: {attempts, lockedUntil}}

  useEffect(() => {
    const raw = localStorage.getItem('healthyaura_user');
    if (raw) setUser(JSON.parse(raw));
  }, []);

  const signIn = async (username, password) => {
    // client-side check for lockout
    const info = lockInfo[username];
    if (info && info.lockedUntil && new Date(info.lockedUntil) > new Date()) {
      throw new Error('Account locked. Try later.');
    }

    const res = await mockApi.signIn(username, password);
    if (res.success) {
      setUser(res.user);
      localStorage.setItem('healthyaura_user', JSON.stringify(res.user));
      // reset attempts
      setLockInfo((s) => ({ ...s, [username]: { attempts: 0 } }));
      return res.user;
    } else {
      // increment attempts and possibly lock
      setLockInfo((s) => {
        const curr = s[username] || { attempts: 0 };
        const attempts = curr.attempts + 1;
        const lockedUntil = attempts >= 3 ? new Date(Date.now() + 30*60*1000).toISOString() : curr.lockedUntil;
        return { ...s, [username]: { attempts, lockedUntil } };
      });
      throw new Error('The username or password is incorrect. Please try again.');
    }
  };

  const signOut = () => {
    setUser(null);
    localStorage.removeItem('healthyaura_user');
  };

  const signUp = async (payload) => {
    const res = await mockApi.signUp(payload);
    if (res.success) {
      setUser(res.user);
      localStorage.setItem('healthyaura_user', JSON.stringify(res.user));
    }
    return res;
  };

  return (
    <AuthContext.Provider value={{ user, signIn, signOut, signUp }}>
      {children}
    </AuthContext.Provider>
  );
}
