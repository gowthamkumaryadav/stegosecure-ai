import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function Dashboard() {
  const [stats, setStats] = useState({});
  const [history, setHistory] = useState([]);
  const navigate = useNavigate();

  // ✅ GET USER EMAIL SAFELY
  const getUserEmail = () => {
    const stored = localStorage.getItem("user");
    if (!stored) return null;

    try {
      const parsed = JSON.parse(stored);
      return parsed.email || parsed.username;
    } catch {
      return stored;
    }
  };

  const userEmail = getUserEmail();

  // 🔐 PROTECT ROUTE + LOAD DATA
  useEffect(() => {
    if (!userEmail) {
      console.log("❌ No user found, redirecting...");
      navigate("/");
      return;
    }

    console.log("✅ Logged in as:", userEmail);

    fetchDashboard();
    fetchHistory();

  }, [userEmail]); // ❗ removed navigate dependency (fix loop issues)


  // 🔥 FETCH DASHBOARD COUNTS
  const fetchDashboard = async () => {
    try {
      console.log("📊 Fetching dashboard for:", userEmail);

      const res = await axios.get(
        `https://stegosecure-ai-production.up.railway.app/stego/dashboard?username=${encodeURIComponent(userEmail)}`
      );

      console.log("✅ Dashboard API Response:", res.data);

      setStats(res.data || {});
    } catch (err) {
      console.error("❌ Dashboard Error:", err?.response || err);

      if (err.response?.status === 400) {
        alert("❌ Invalid user request");
      }
    }
  };


  // 🔥 FETCH HISTORY
  const fetchHistory = async () => {
    try {
      console.log("📜 Fetching history for:", userEmail);

      const res = await axios.get(
        `https://stegosecure-ai-production.up.railway.app/stego/history?username=${encodeURIComponent(userEmail)}`
      );

      console.log("✅ History API Response:", res.data);

      setHistory(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.error("❌ History Error:", err?.response || err);
    }
  };


  // ✅ SORT LATEST FIRST
  const sortedHistory = Array.isArray(history)
    ? [...history].sort(
        (a, b) => new Date(b.timestamp) - new Date(a.timestamp)
      )
    : [];

  return (
    <div className="min-h-screen p-6 bg-gray-100 dark:bg-gray-900 text-gray-900 dark:text-white">

      <h1 className="text-3xl font-bold mb-6">Dashboard</h1>

      {/* STATS */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        {[
          { title: "Encoded Images", value: stats.encoded ?? 0 },
          { title: "Decoded Messages", value: stats.decoded ?? 0 },
          { title: "Secure Messages", value: stats.secure ?? 0 },
          { title: "Activity", value: "Active" },
        ].map((card, i) => (
          <motion.div
            key={i}
            whileHover={{ scale: 1.05 }}
            className="p-4 rounded-xl bg-white/20 dark:bg-white/10 backdrop-blur-md shadow-lg"
          >
            <h2 className="text-lg opacity-80">{card.title}</h2>
            <p className="text-2xl font-bold">{card.value}</p>
          </motion.div>
        ))}
      </div>

      {/* ACTIONS */}
      <div className="flex gap-4 mb-8">
        <button
          onClick={() => navigate("/encode")}
          className="px-5 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
        >
          Encode
        </button>

        <button
          onClick={() => navigate("/decode")}
          className="px-5 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600"
        >
          Decode
        </button>
      </div>

      {/* RECENT ACTIVITY */}
      <div className="bg-white/20 dark:bg-white/10 p-5 rounded-xl shadow mb-8">
        <h2 className="text-xl font-semibold mb-4">Recent Activity</h2>

        {sortedHistory.length === 0 ? (
          <p className="text-gray-400">No activity yet</p>
        ) : (
          <ul>
            {sortedHistory.slice(0, 10).map((item, i) => (
              <li key={i} className="flex justify-between border-b py-2">
                <span>{item.fileName || "No Name"}</span>
                <span className="text-sm font-semibold">{item.type}</span>
              </li>
            ))}
          </ul>
        )}
      </div>

      {/* LAST ENCODE */}
      <div className="bg-white dark:bg-gray-800 p-5 rounded-xl shadow">
        <h2 className="text-xl font-semibold mb-4">Last Encoded Image</h2>

        {sortedHistory.some((item) => item.type?.includes("ENCODE")) ? (
          <p className="text-green-500">
            {
              sortedHistory.find((item) =>
                item.type?.includes("ENCODE")
              )?.fileName
            }
          </p>
        ) : (
          <p className="text-gray-400">No encoded images yet</p>
        )}
      </div>
    </div>
  );
}