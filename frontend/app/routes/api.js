const express = require('express');
const router = express.Router();
const apiController = require("../controllers/apiController");

router.get('/endpoint', apiController.getEndpoint);

module.exports = router;
