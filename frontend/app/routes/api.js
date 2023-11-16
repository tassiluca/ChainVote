const express = require('express');
const router = express.Router();
const apiController = require("../controllers/apiController");

router.get('/elections', apiController.getAllElections);
router.get('/elections/:electionId', apiController.getElection);
router.get('/elections/:id', apiController.getElection);

router.use('/elections/vote/:electionId', apiController.castVote);
router.post('/elections/code', apiController.createElectionCode);

module.exports = router;
