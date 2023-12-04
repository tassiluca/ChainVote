const express = require('express');
const router = express.Router();
const apiController = require("../controllers/apiController");
const userController = require("../controllers/userController");
const responseMiddleware = require("../middlewares/responseMiddleware");
const refreshTokenMiddleware = require("../middlewares/refreshTokenMiddleware");

router.get('/', userController.getDefault, responseMiddleware);
router.get('/sign-in', userController.getSignIn, responseMiddleware);
router.post('/sign-in', userController.postSignIn);
router.get('/sign-up', userController.getSignUp, responseMiddleware);
router.post('/sign-up', userController.postSignUp);

router.use(refreshTokenMiddleware);

router.get('/elections', apiController.getAllElections, responseMiddleware);
router.get('/elections/:electionId', apiController.getElection, responseMiddleware);
router.get('/elections/vote/:electionId', apiController.getCastVote, responseMiddleware);
router.post('/elections/vote/:electionId', apiController.postCastVote);
router.post('/elections/code', apiController.createElectionCode);
router.get('/user-area', userController.getUserArea, responseMiddleware);
router.get('/logout', userController.logout);
router.get('/elections/create', apiController.getCreateElection, responseMiddleware);
router.post('/elections/create', apiController.postCreateElection);


module.exports = router;
