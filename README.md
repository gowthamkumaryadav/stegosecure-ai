# 🔐 StegoSecure AI

A full-stack steganography web application to securely hide and extract messages inside images.

---

## 🚀 Features

- 🔒 Encode secret messages into images
- 🔓 Decode hidden messages from images
- 🔐 Password-protected encryption
- 👤 Multi-user support (Admin + Google login)
- 📊 Dashboard with activity tracking
- 🧠 AI-based detection (optional feature)

---

## 🛠 Tech Stack

### Frontend
- React.js
- Tailwind CSS
- Axios

### Backend
- Spring Boot
- Java
- H2 Database

---

📂 Project Structure

steganography-project/
├── stego-front/
│   ├── public/
│   ├── src/
│   │   ├── pages/
│   │   ├── components/
│   │   ├── api/
│   │   ├── App.js
│   │   └── index.js
│   └── package.json
│
├── backend/
│   ├── src/main/java/com/steganography/backend/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/
│   │   └── BackendApplication.java
│   │
│   ├── src/main/resources/
│   │   └── application.properties
│   │
│   └── pom.xml
│
└── README.md
