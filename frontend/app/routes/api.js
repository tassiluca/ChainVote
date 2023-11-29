const express = require('express');
const router = express.Router();
const apiController = require("../controllers/apiController");
const userController = require("../controllers/userController");
const responseMiddleware = require("../middlewares/responseMiddleware");
const refreshTokenMiddleware = require("../middlewares/refreshTokenMiddleware");

router.use(refreshTokenMiddleware);

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
router.get('/logout', userController.logout);
router.get('/create-election', apiController.getCreateElection, responseMiddleware);
router.post('/create-election', apiController.postCreateElection);

router.get('/', userController.getDefault, responseMiddleware);

module.exports = router;
