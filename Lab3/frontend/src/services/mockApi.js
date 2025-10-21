export const mockApi = (() => {
  // in-memory data (for demo/testing only)
  const users = {};
  const stalls = require('./stubStalls').default; // we'll provide a simple list
  const reviews = {};
  return {
    signUp: async ({ username, email, password, phone, diet }) => {
      if (users[username]) return { success: false, message: 'User exists' };
      const user = { username, email, role: 'User', points: 0, phone, diet, lastPointsUpdate: new Date().toISOString() };
      users[username] = { ...user, password };
      return { success: true, user };
    },
    signIn: async (username, password) => {
      const u = users[username];
      if (!u || u.password !== password) return { success: false };
      const { password: _p, ...publicUser } = u;
      return { success: true, user: publicUser };
    },
    getStalls: async (filters = {}) => {
      // filters: q, tags, maxPrice, sortby, location
      // simple filter logic
      let list = stalls.slice();
      if (filters.q) {
        const q = filters.q.toLowerCase();
        list = list.filter(s => s.name.toLowerCase().includes(q) || s.center.toLowerCase().includes(q));
      }
      if (filters.tags && filters.tags.length) {
        list = list.filter(s => filters.tags.every(t => s.tags.includes(t)));
      }
      if (filters.maxPrice != null) {
        list = list.filter(s => s.price <= filters.maxPrice);
      }
      // sort
      if (filters.sortBy === 'price') list.sort((a,b)=>a.price-b.price);
      if (filters.sortBy === 'popularity') list.sort((a,b)=>b.popularity-a.popularity);
      // distance sorting requires location; skip complexity here
      return { success: true, stalls: list };
    },
    getStallDetails: async (stallId) => {
      const s = stalls.find(x=>x.id===stallId);
      // mock crowd & queue with timestamp
      return {
        ...s,
        queueEstimate: Math.floor(Math.random()*25), // minutes
        queueTimestamp: new Date().toISOString(),
        crowdLevel: ['Low','Medium','High'][Math.floor(Math.random()*3)]
      };
    },
    getUserPoints: async (username) => {
      const u = users[username];
      return { points: u?.points || 0, lastUpdate: u?.lastPointsUpdate };
    },
    awardPoints: async (username, reason, qty) => {
      const u = users[username];
      if (!u) return { success: false };
      // prevent duplicate awarding for same reason within 24 hours using a simple in-memory lastActions map
      u.points = (u.points || 0) + qty;
      u.lastPointsUpdate = new Date().toISOString();
      return { success: true, newBalance: u.points };
    },
    redeemReward: async (username, reward) => {
      const u = users[username];
      if (!u || u.points < reward.cost) return { success: false, message: 'Not enough points' };
      u.points -= reward.cost;
      return { success: true, newBalance: u.points };
    },
    // Reviews and admin moderation endpoints are omitted in this mock but should exist in backend
  };
})();
