const axiosRequest = require('./utils');
require('dotenv').config()

const urlBackendAPI = process.env.API_URL || "http://localhost:8080"
const urlLogin = process.env.AUTH_URL || "http://localhost:8180"
const urlSignIn = urlLogin + "/auth/login"
const urlSignUp = urlBackendAPI + "/users"

const getSignUp = async (req, res, next) => {
    res.locals.view = 'sign-up';
    next();
};

const postSignUp = async (req, res) => {
    try {
        if (req.body.name && req.body.surname &&
             req.body.email && req.body.password && req.body.role) {
            const {name, surname, email, password, role} = req.body;
            const response = await axiosRequest('POST', urlSignUp, {
                name: name, surname: surname, email: email, password: password, role: role
            })

            if (response.success) {
                const redirectUrl = '/';
                res.status(response.body.code).json({
                    success: true,
                    message: "User successfully created.",
                    data: response.body.data,
                    url: redirectUrl
                })
            } else {
                res.status(response.body.code).json({
                    name: response.body.error.name,
                    message: response.body.error.message
                })
            }
        } else {
            res.status(403).json({message: "Bad credentials"})
        }
    } catch (error) {
        res.status(500).json(
            {message: error}
        );
    }
};

const getSignIn = async (req, res, next) => {
    res.locals.view = 'sign-in';
    next();
};

const postSignIn = async (req, res) => {
    try {
        if (req.body.email && req.body.password) {
            const {email, password} = req.body
            const response = await axiosRequest('POST', urlSignIn, {email: email, password: password});
            if (response.success) {
                const redirectUrl = '/';
                if (req.session && req.session.accessToken) {
                    req.session.regenerate(function(err) {
                        if (err) {
                            res.status(500).json(
                                {message: err}
                            );
                        }
                    })
                }

                req.session.accessToken = response.data.accessToken
                req.session.refreshToken = response.data.refreshToken
                req.session.email = req.body.email

                res.status(response.body.code).json({
                    success: true,
                    message: "User successfully logged in.",
                    url: redirectUrl
                });
            } else {
                res.status(response.body.code).json({
                    name: response.body.error.name,
                    message: response.body.error.message
                })
            }
        } else {
            res.status(403).json({
                message: "Bad credentials."
            })
        }
    } catch (error) {
        res.status(500).json(
            {message: error}
        );
    }
};

const getUserArea = async (req, res, next) => {
    try {
        const userAreaUrl = `http://api-server:8080/users/${req.session.email}}`;
        const userData = await axiosRequest('GET', userAreaUrl, null, rq.session.accessToken);
        res.locals.view = 'user-area';
        res.locals.data = userData;
    } catch (error) {
        res.locals.view = 'sign-in';
    }
    next();
}

module.exports = {
    getSignIn,
    postSignIn,
    getSignUp,
    postSignUp,
    getUserArea,
}
