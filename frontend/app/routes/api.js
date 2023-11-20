const express = require('express');
const router = express.Router();
const apiController = require("../controllers/apiController");
const userController = require("../controllers/routerUser");
const responseMiddleware = require("../controllers/responseMiddleware");

router.get('/elections', apiController.getAllElections, responseMiddleware);
router.get('/elections/:electionId', apiController.getElection, responseMiddleware);
router.get('/elections/vote/:electionId', apiController.getCastVote, responseMiddleware);
router.post('/elections/vote/:electionId', apiController.postCastVote);
router.post('/elections/code', apiController.createElectionCode);
router.get('/sign-in', userController.getSignIn, responseMiddleware);
router.post('/sign-in', userController.postSignIn);
router.get('/sign-up', userController.getSignUp, responseMiddleware);
router.post('/sign-up', userController.postSignUp);
router.get('/user-area', userController.getUserArea, responseMiddleware);
router.get('/create-election', apiController.getCreateElection, responseMiddleware);

router.get('/', userController.getDefault, responseMiddleware);

module.exports = router;
