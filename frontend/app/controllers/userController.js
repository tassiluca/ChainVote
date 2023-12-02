const {
    chaincodeErrorCode,
    badRequestErrorCode,
    badRequestErrorMessage,
    axiosRequest
} = require('../utils/utils')

const urlApiServer = process.env.API_SERVER_URL || 'http://localhost:8080'
const urlAuthServer = process.env.AUTH_SERVER_URL || "http://localhost:8180"
const urlSignIn = urlAuthServer + "/auth/login"
const urlSignUp = urlApiServer + "/users"

const getDefault = async (req, res, next) => {
    res.locals.view = 'index';
    next();
};

const getSignUp = async (req, res, next) => {
    res.locals.view = 'sign-up';
    next();
};

const postSignUp = async (req, res) => {
    if (req.body.name && req.body.surname &&
        req.body.email && req.body.password && req.body.role) {
        try {
            const {name, surname, email, password, role} = req.body;
            const response = await axiosRequest('POST', urlSignUp, {
                firstName: name, secondName: surname, email: email, password: password, role: role
            })
            if (response.success) {
                const redirectUrl = '/';
                res.status(response.code).json({
                    success: true,
                    message: "User successfully created.",
                    data: response.data,
                    url: redirectUrl
                })
            } else {
                res.status(response.code).json({
                    name: response.error.name,
                    message: response.error.message
                })
            }
        } catch (error) {
            res.status(error.response.data.code).json(
                {message: error.response.data.error.message}
            );
        }
    } else {
        res.status(badRequestErrorCode).json({message: badRequestErrorMessage})
    }
};

const getSignIn = async (req, res, next) => {
    if (req.session && req.session.accessToken) {
        res.redirect('/');
    } else {
        res.locals.view = 'sign-in';
        next();
    }
};

const postSignIn = async (req, res) => {
    if (req.body.email && req.body.password) {
        try {
            const {email, password} = req.body;
            const response = await axiosRequest('POST', urlSignIn, {email: email, password: password});

            if (response.success) {
                const redirectUrl = '/elections';
                if (req.session && req.session.accessToken) {
                    req.session.regenerate(function(err) {
                        if (err) {
                            res.status(500).json({
                                message: err
                            });
                        }
                    })
                }
                
                req.session.accessToken = response.data.accessToken;
                req.session.refreshToken = response.data.refreshToken;
                req.session.email = req.body.email;

                req.session.expire = Date.now() + 15 * 60 * 1000;

                const responseRole = await axiosRequest('GET',
                    urlSignUp + `/${req.session.email}`,
                    null,
                    req.session.accessToken
                );

                if (responseRole.success) {
                    req.session.role = responseRole.data.role;
                } else {
                    res.status(responseRole.code).json({
                        name: responseRole.error.name,
                        message: responseRole.error.message
                    });
                }

                res.status(response.code).json({
                    success: true,
                    message: "User successfully logged in.",
                    url: redirectUrl
                });
            } else {
                res.status(response.code).json({
                    name: response.error.name,
                    message: response.error.message
                });
            }
        } catch (error) {
            console.log(error);
            res.status(error.response.data.code).json({
                message: error.response.data.error.message
            });
        }
    } else {
        res.status(badRequestErrorCode).json({
            message: badRequestErrorMessage
        });
    }
};

const getUserArea = async (req, res, next) => {
    if (req.session && req.session.accessToken) {
        try {
            const userAreaUrl = urlApiServer + `/users/${req.session.email}`;
            const userData = await axiosRequest('GET', userAreaUrl, null, req.session.accessToken);
            res.locals.view = 'user-area';
            res.locals.data = userData;
        } catch (error) {
            res.locals.view = 'error';
        }
    }
    next();
}

const logout = async (req, res) => {
    const urlLogout = urlAuthServer + "/auth/logout"
    try {
        await axiosRequest('POST', urlLogout, null, req.session.accessToken);
    } catch (error) {
        console.error(error.data);
    }
    req.session.destroy((error) => {
        if (error) {
            console.log("Error destroying session:", error);
        }
        res.redirect('/');
    });
}

module.exports = {
    getDefault,
    getSignIn,
    postSignIn,
    getSignUp,
    postSignUp,
    getUserArea,
    logout,
}
