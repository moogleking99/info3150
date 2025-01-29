const express = require('express');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const router = express.Router();

// Mock database
const users = [];

// Register Route
router.post('/register', async (req, res) => {
    const { username, password } = req.body;

    // Check if user exists
    if (users.find(user => user.username === username)) {
        return res.status(400).json({ error: 'User already exists' });
    }

    // Hash the password
    const hashedPassword = await bcrypt.hash(password, 10);

    // Save user
    users.push({ username, password: hashedPassword });
    res.status(201).json({ message: 'User registered successfully' });
});

// Login Route
router.post('/login', async (req, res) => {
    const { username, password } = req.body;

    // Find user
    const user = users.find(user => user.username === username);
    if (!user) {
        return res.status(400).json({ error: 'Invalid credentials' });
    }

    // Check password
    const validPassword = await bcrypt.compare(password, user.password);
    if (!validPassword) {
        return res.status(400).json({ error: 'Invalid credentials' });
    }

    // Generate token
    const token = jwt.sign({ username: user.username }, process.env.JWT_SECRET, { expiresIn: '1h' });
    res.json({ message: 'Login successful', token });
});

module.exports = router;
