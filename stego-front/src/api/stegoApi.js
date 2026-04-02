import axios from "axios";

const API = "https://stegosecure-ai-production.up.railway.app";

// ✅ ENCODE
export const encodeImage = (formData) =>
  axios.post(`${API}/encode`, formData, {
    responseType: "blob",
    withCredentials: false,
  });

// ✅ DECODE
export const decodeImage = (formData) =>
  axios.post(`${API}/decode`, formData, {
    withCredentials: false,
  });