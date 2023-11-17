const axiosRequest = require('./utils');
require('dotenv').config()

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
             req.body.email && req.body.password && req.body.role) {
            const {name, surname, email, password, role} = req.body;
            const response = {success: true, body: {
                    code: 200,
                    data: "data-test"
                }}
                /*
                await axiosRequest('POST', process.env.SIGN_UP_URL, {
                name: name, surname: surname, email: email, password: password, role: role
            });
            */
            if (response.success) {
                res.status(response.body.code).json({
                    message: "User successfully created.",
                    data: response.body.data
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
        console.log(req.body)
        if (req.body.email && req.body.password) {
            const {email, password} = req.body
            const response = {success: true, data: {
                    accessToken: "an access token",
                    refreshToken: "a refresh token"
                }, body: {code: 200, error: {name: 'an error', message: "an error message"}}}
                /*
                await axiosRequest('POST', process.env.SIGN_IN_URL, {email: email, password: password});
            console.log(response)
                 */
            if (response.success) {
                const redirectUrl = '/';
                if (req.session && req.session.accessToken) {
                    console.log('Destroying session...')
                    req.session.destroy((err) => {
                        if (err) {
                            console.error('Error destroying session: ', err);
                        }
                    });
                }
                req.session.accessToken = response.data.accessToken
                req.session.refreshToken = response.data.refreshToken

                res.redirect(response.body.code, redirectUrl);
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
