const axiosRequest = require('./utils');

const redirectUrlSignUp = "http://localhost:8080/users/"
const redirectUrlSignIn = "http://localhost:8180/auth/login/"

const getSignUp = async (req, res) => {
    try {
        res.render('sign-up');
    } catch (error) {
        res.render('not-found');
    }
};

const postSignUp = async (req, res) => {
    try {
        console.log("Sign up post request")
        if (req.body.name && req.body.surname &&
             req.body.email && req.body.password) {
            console.log(req.body);
            const {name, surname, email, password} = req.body
            const response = await axiosRequest('POST', redirectUrlSignUp, {name: name, surname: surname, email: email, password: password});
            if (response.err) {
                res.status(400).json({message: response.err})
            } else {
                res.status(200).json({
                    message: "User successfully created.",
                    data: response.data
                })
            }
        } else {
            res.status(403).json({message: "Bad credentials"})
        }
    } catch (e) {
        res.status(500).end();
    }
};

const getSignIn = async (req, res) => {
    try {
        console.log("Sign in get request")
        res.render('sign-in');
    } catch (error) {
        res.render('not-found');
    }
};

const postSignIn = async (req, res) => {
    try {
        console.log("Sign in post request")
        console.log(req.session)
        if (req.session.authenticated) {
            console.log(req.session)
            res.json(req.session)
        } else if (req.body.email && req.body.password) {
            const {email, password} = req.body
            const response = await axiosRequest('POST', redirectUrlSignIn, {email: email, password: password});
            console.log(response)
            if (response.err) {
                res.status(403).json({
                    message: response.err + ". Bad credentials."
                })
            } else {
                req.session.authenticated = true;
                req.session.jwt = response.bearer
                res.status(200).json({
                    message: "User successfully logged in.",
                    session: req.session
                })
            }
        } else {
            res.status(403).json({
                message: "Bad credentials."
            })
        }
    } catch (e) {
        res.status(500).end();
    }
};

const getForgotCredentials = async (req, res) => {
    try {
        console.log("forgot credentials get request")
        res.render('forgot-credentials');
    } catch (error) {
        res.render('not-found');
    }
};

const postForgotCredentials = async (req, res) => {
    try {
        console.log("forgot credentials post request")
    } catch (error) {
    }
};

module.exports = {
    getSignIn,
    postSignIn,
    getSignUp,
    postSignUp,
    getForgotCredentials,
    postForgotCredentials
}
