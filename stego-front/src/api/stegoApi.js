import axios from "axios";

const API = "https://stegosecure-ai-production.up.railway.app";

// ✅ ENCODE
export const encodeImage = (formData) =>
  axios.post(`${API}/stego/encode`, formData, {
    responseType: "blob",
    withCredentials: false,
  });

// ✅ DECODE
export const decodeImage = (formData) =>
  axios.post(`${API}/stego/decode`, formData, {
    withCredentials: false,
  });