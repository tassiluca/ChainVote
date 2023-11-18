const express = require('express');
const router = express.Router();
const apiController = require("../controllers/apiController");
const userController = require("../controllers/routerUser");
const responseMiddleware = require("../controllers/responseMiddleware");

router.get('/elections', apiController.getAllElections, responseMiddleware);
router.get('/elections/:electionId', apiController.getElection, responseMiddleware);
router.get('/elections/:id', apiController.getElection, responseMiddleware);
router.use('/elections/vote/:electionId', apiController.castVote);
router.post('/elections/code', apiController.createElectionCode);
router.get('/sign-in', userController.getSignIn, responseMiddleware);
router.post('/sign-in', userController.postSignIn);
router.get('/sign-up', userController.getSignUp, responseMiddleware);
router.post('/sign-up', userController.postSignUp);
router.get('/forgot-credentials', userController.getForgotCredentials, responseMiddleware);
router.post('/forgot-credentials', userController.postForgotCredentials);

router.get('/', (req, res) => res.render('index'),  responseMiddleware);

module.exports = router;
