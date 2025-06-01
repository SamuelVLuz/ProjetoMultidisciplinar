const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');

const app = express();
const port = 3000;

app.use(cors({
  origin: 'http://localhost:8080',
  methods: ['GET', 'POST', 'OPTIONS'],
  allowedHeaders: ['Content-Type'],
  credentials: true
}));

app.use(express.json());

app.options('*', cors());

// Configure DB connection
const pool = mysql.createPool({
  host: 'mysql', // Docker service name
  user: 'entropyuser',
  password: 'entropypassword',
  database: 'entropydb',
  waitForConnections: true,
  connectionLimit: 10
});

// Get all entropy values
app.get('/entropy', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT entropy FROM entropy_stats');
    res.json(rows);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Calculate percentile
app.post('/percentile', async (req, res) => {
  const { entropy } = req.body;
  if (typeof entropy !== 'number') {
    return res.status(400).json({ error: 'Entropy must be a number.' });
  }

  try {
    const [rows] = await pool.query('SELECT entropy FROM entropy_stats');
    const entropies = rows.map(r => r.entropy);

    const countBelow = entropies.filter(e => e < entropy).length;
    const percentile = (countBelow / entropies.length) * 100;

    res.json({ percentile: percentile.toFixed(2) });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});


// Add new entropy (optional)
app.post('/entropy', async (req, res) => {
  const { entropy } = req.body;
  if (typeof entropy !== 'number') {
    return res.status(400).json({ error: 'Entropy must be a number.' });
  }

  try {
    await pool.query('INSERT INTO entropy_stats (entropy) VALUES (?)', [entropy]);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.listen(port, () => {
  console.log(`Entropy API running on port ${port}`);
});
