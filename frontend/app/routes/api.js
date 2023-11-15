const express = require('express');
const router = express.Router();
const apiController = require("../controllers/apiController");
const userController = require("../controllers/routerUser");

router.get('/elections', apiController.getAllElections);
router.get('/elections/:electionId', apiController.getElection);
router.get('/sign-in', userController.getSignIn);
router.post('/sign-in', userController.postSignIn);
router.get('/sign-up', userController.getSignUp);
router.post('/sign-up', userController.postSignUp);
router.get('/forgot-credentials', userController.getForgotCredentials);
router.post('/forgot-credentials', userController.postForgotCredentials);

router.get('/', (req, res) => res.render('index'));

module.exports = router;
