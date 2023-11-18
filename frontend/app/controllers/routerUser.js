const axiosRequest = require('./utils');
require('dotenv').config()

const getSignUp = async (req, res, next) => {
    res.locals.view = 'sign-up';
    next();
};

const postSignUp = async (req, res) => {
    try {
        console.log("Sign up post request")
        if (req.body.name && req.body.surname &&
             req.body.email && req.body.password && req.body.role) {
            const {name, surname, email, password, role} = req.body;
            const response = await axiosRequest('POST', process.env.SIGN_UP_URL, {
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
        console.log("Sign in post request")
        console.log(req.body)
        if (req.body.email && req.body.password) {
            const {email, password} = req.body
            const response = await axiosRequest('POST', process.env.SIGN_IN_URL, {email: email, password: password});
            console.log(response)
            if (response.success) {
                const redirectUrl = '/';
                if (req.session && req.session.accessToken) {
                    console.log('Regenerating session...')
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


module.exports = {
    getSignIn,
    postSignIn,
    getSignUp,
    postSignUp,
}
