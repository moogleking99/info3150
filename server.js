const express = require('express');
const bodyParser = require('body-parser');
const authRoutes = require('./routes/auth');
require('dotenv').config(); // Load environment variables

const app = express();
const PORT = 3000;

// Middleware
app.use(bodyParser.json());
app.use(express.static('public')); // Serve static files

// Routes
app.use('/api/auth', authRoutes);

// Start the server
app.listen(PORT, () => console.log(`Server running on http://localhost:${PORT}`));
