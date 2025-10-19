import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080", // change if your backend uses another prefix
});

api.interceptors.request.use((config) => {
  const user = JSON.parse(localStorage.getItem("healthyaura_user"));
  if (user && user.token) {
    config.headers.Authorization = `Bearer ${user.token}`;
  }
  return config;
});

export default api;
