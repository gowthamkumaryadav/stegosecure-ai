import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { FaLock, FaRobot, FaImage, FaBolt } from "react-icons/fa";
import { useState, useEffect } from "react";

export default function Home() {
  const navigate = useNavigate();
  const [step, setStep] = useState(0);
  const [typedText, setTypedText] = useState("");
  const message = "Secret Message";
  // Step animation loop
  useEffect(() => {
    const interval = setInterval(() => {
      setStep((prev) => (prev < 2 ? prev + 1 : 0));
    }, 1800);

    return () => clearInterval(interval);
  }, []);

  // Typing effect
  useEffect(() => {
    if (step !== 1) return;

    let index = 0;
    setTypedText("");

    const interval = setInterval(() => {
      if (index <= message.length) {
        setTypedText(message.slice(0, index));
        index++;
      } else {
        clearInterval(interval);
      }
    }, 100);

    return () => clearInterval(interval);
  }, [step, message]);

  // Binary converter
  const toBinary = (text) => {
    return text
      .split("")
      .map((char) => char.charCodeAt(0).toString(2))
      .join(" ");
  };
  const originalImg =
    "https://images.unsplash.com/photo-1518770660439-4636190af475?w=300";
  const encodedImg =
    "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=300";

  return (
    <div className="bg-gray-50 min-h-screen text-gray-800">
      {/* 🔥 NAVBAR */}
      <nav className="flex justify-between items-center px-8 py-4 bg-white shadow-sm">
        <h1 className="text-xl font-bold">🔒 StegoSecure AI</h1>
        <div className="flex gap-4">
          <button
            onClick={() => navigate("/login")}
            className="text-gray-600 hover:text-black"
          >
            Login
          </button>
          <button
            onClick={() => navigate("/encode")}
            className="bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800"
          >
            Get Started
          </button>
        </div>
      </nav>

      {/* 🔥 HERO SECTION */}
      <section className="text-center py-20 px-6">
        <motion.h1
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-4xl md:text-5xl font-bold mb-6"
        >
          Hide Data Inside Images 🔐
        </motion.h1>

        <p className="text-gray-600 max-w-xl mx-auto mb-8">
          Secure your messages using powerful AES encryption and AI-based hidden
          data detection.
        </p>

        <div className="flex justify-center gap-4">
          <button
            onClick={() => navigate("/encode")}
            className="bg-blue-500 text-white px-6 py-3 rounded-lg shadow hover:bg-blue-600 transition"
          >
            Encode Message
          </button>

          <button
            onClick={() => navigate("/decode")}
            className="bg-green-500 text-white px-6 py-3 rounded-lg shadow hover:bg-green-600 transition"
          >
            Decode Message
          </button>
        </div>
      </section>

      {/* 🔥 FEATURES */}
      <section className="py-16 px-8 bg-white">
        <h2 className="text-2xl font-semibold text-center mb-10">
          Powerful Features
        </h2>

        <div className="grid md:grid-cols-4 gap-6">
          <Feature
            icon={<FaLock />}
            title="AES Encryption"
            desc="Secure data with military-grade encryption."
          />
          <Feature
            icon={<FaRobot />}
            title="AI Detection"
            desc="Detect hidden messages using AI."
          />
          <Feature
            icon={<FaImage />}
            title="Multi Format"
            desc="Supports PNG, JPG, JPEG images."
          />
          <Feature
            icon={<FaBolt />}
            title="Fast Processing"
            desc="Instant encoding & decoding."
          />
        </div>
      </section>

      {/* 🔥 HOW IT WORKS */}
      <section className="py-16 px-8 bg-gray-50 text-center">
        <h2 className="text-2xl font-semibold mb-10">How It Works</h2>

        <div className="grid md:grid-cols-3 gap-6">
          <Step number="1" text="Upload Image" />
          <Step number="2" text="Enter Message" />
          <Step number="3" text="Download Secure Image" />
        </div>
      </section>

      {/* 🔥 LIVE DEMO */}
      <section className="py-20 px-8 bg-gray-50 text-center">
        <h2 className="text-3xl font-bold mb-12">Live Demo</h2>

        <div className="grid md:grid-cols-3 gap-10 items-center">
          {/* ORIGINAL IMAGE */}
          <div className="bg-white p-6 rounded-xl shadow-md">
            <p className="mb-3 font-semibold text-gray-700">Original Image</p>

            <img
              src={originalImg}
              alt="original"
              className={`mx-auto rounded-lg transition-all duration-700 ${
                step >= 0 ? "opacity-100 scale-100" : "opacity-0 scale-90"
              }`}
            />
          </div>

          {/* 🔥 CENTER ANIMATION */}
          <div className="flex flex-col items-center justify-center">
            {/* ARROW */}
            <motion.div
              animate={{ x: [0, 30, 0], opacity: [0.6, 1, 0.6] }}
              transition={{ repeat: Infinity, duration: 1 }}
              className="text-4xl"
            >
              ➡️
            </motion.div>

            {/* STEP 1 → TYPING MESSAGE */}
            {step === 1 && (
              <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className="mt-4 text-center"
              >
                <p className="text-blue-500 font-medium animate-pulse">
                  🔐 Encoding message...
                </p>

                <div className="bg-black text-green-400 text-sm mt-3 p-4 rounded w-72 font-mono">
                  {typedText}
                </div>
              </motion.div>
            )}

            {/* STEP 2 → BINARY + SUCCESS */}
            {step === 2 && (
              <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className="mt-4 text-center"
              >
                <p className="text-purple-500 font-medium mb-2">
                  Converting to binary...
                </p>

                <div className="bg-black text-green-400 text-xs p-4 rounded w-72 font-mono shadow-lg">
                  {toBinary(message)}
                </div>

                <p className="text-green-600 mt-3 font-medium">
                  ✅ Message hidden successfully
                </p>
              </motion.div>
            )}
          </div>

          {/* ENCODED IMAGE */}
          <div className="bg-white p-6 rounded-xl shadow-md">
            <p className="mb-3 font-semibold text-gray-700">Encoded Image</p>

            <img
              src={encodedImg}
              alt="encoded"
              className={`mx-auto rounded-lg transition-all duration-700 ${
                step >= 2
                  ? "opacity-100 scale-100 blur-0"
                  : "opacity-30 scale-95 blur-sm"
              }`}
            />
          </div>
        </div>

        {/* DESCRIPTION */}
        <p className="text-gray-500 mt-10 max-w-xl mx-auto">
          Your secret message is invisibly embedded into the image using
          advanced steganography techniques.
        </p>
      </section>

      {/* 🔥 USE CASES */}
      <section className="py-16 px-8 bg-white text-center">
        <h2 className="text-2xl font-semibold mb-10">Use Cases</h2>

        <div className="grid md:grid-cols-4 gap-6 text-gray-600">
          <p>🔐 Secure Communication</p>
          <p>🛡️ Cybersecurity</p>
          <p>📄 Data Protection</p>
          <p>🖼️ Digital Watermarking</p>
        </div>
      </section>

      {/* 🔥 CTA */}
      <section className="py-20 text-center bg-gray-900 text-white">
        <h2 className="text-3xl font-bold mb-4">Ready to Secure Your Data?</h2>
        <p className="mb-6 text-gray-400">
          Start hiding messages securely inside images today.
        </p>

        <button
          onClick={() => navigate("/encode")}
          className="bg-white text-black px-6 py-3 rounded-lg hover:bg-gray-200 transition"
        >
          Start Encoding
        </button>
      </section>

      {/* 🔥 FOOTER */}
      <footer className="bg-gray-900 text-gray-300 py-10 px-6">
        <div className="max-w-6xl mx-auto grid md:grid-cols-3 gap-8">
          {/* BRAND */}
          <div>
            <h2 className="text-lg font-semibold text-white mb-2">
              🔒 StegoSecure AI
            </h2>
            <p className="text-sm text-gray-400">
              Secure your data inside images using advanced steganography and
              AI.
            </p>
          </div>

          {/* LINKS */}
          <div className="flex flex-col gap-2">
            <h3 className="text-white font-medium mb-2">Legal</h3>

            <a href="/privacy" className="hover:text-white transition">
              Privacy Policy
            </a>

            <a href="/terms" className="hover:text-white transition">
              Terms & Conditions
            </a>

            <a href="/contact" className="hover:text-white transition">
              Contact Us
            </a>
          </div>

          {/* CONTACT INFO */}
          <div>
            <h3 className="text-white font-medium mb-2">Contact</h3>
            <p className="text-sm">📧 gowthamkumaryadav13@gmail.com</p>
            <p className="text-sm">🌐 India</p>
          </div>
        </div>

        <div className="text-center text-gray-500 text-sm mt-8">
          © 2026 StegoSecure AI. All rights reserved.
        </div>
      </footer>
    </div>
  );
}

/* 🔥 COMPONENTS */

function Feature({ icon, title, desc }) {
  return (
    <div className="p-6 bg-gray-50 rounded-xl shadow hover:shadow-md transition text-center">
      <div className="text-2xl mb-3">{icon}</div>
      <h3 className="font-semibold mb-2">{title}</h3>
      <p className="text-sm text-gray-600">{desc}</p>
    </div>
  );
}

function Step({ number, text }) {
  return (
    <div className="p-6 bg-white rounded-xl shadow">
      <div className="text-2xl font-bold text-blue-500 mb-2">{number}</div>
      <p>{text}</p>
    </div>
  );
}
