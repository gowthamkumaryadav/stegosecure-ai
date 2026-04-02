import axios from "axios";

const API = "https://stegosecure-ai-production.up.railway.app";

// ✅ ENCODE
export const encodeImage = (formData) =>
  axios.post(`${API}/encode`, formData, {
    responseType: "blob",
    headers: {
      "Content-Type": "multipart/form-data",
    },
    withCredentials: false, // 🔥 IMPORTANT (prevents 401)
  });

// ✅ DECODE
export const decodeImage = (formData) =>
  axios.post(`${API}/decode`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
    withCredentials: false, // 🔥 IMPORTANT
  });