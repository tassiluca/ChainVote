const axiosRequest = require('./utils');

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
    try {
        if (req.body.name && req.body.surname &&
             req.body.email && req.body.password && req.body.role) {
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
        } else {
            res.status(403).json({message: "Bad credentials"})
        }
    } catch (error) {
        res.status(error.response.data.code).json(
            {message: error.response.data.error.message}
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
            const {email, password} = req.body;
            const response = await axiosRequest('POST', urlSignIn, {email: email, password: password});
            if (response.success) {
                const redirectUrl = '/elections';
                if (req.session && req.session.accessToken) {
                    req.session.regenerate(function(err) {
                        if (err) {
                            res.status(500).json(
                                {message: err}
                            );
                        }
                    })
                }

                console.log("prova");
                
                req.session.accessToken = response.data.accessToken;
                req.session.refreshToken = response.data.refreshToken;
                req.session.email = req.body.email;

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
        } else {
            res.status(403).json({
                message: "Bad credentials."
            });
        }
    } catch (error) {
        res.status(error.response.data.code).json(
            {message: error.response.data.error.message}
        );
    }
};

const getUserArea = async (req, res, next) => {
    try {
        const userAreaUrl = urlApiServer + `/users/${req.session.email}`;
        const userData = await axiosRequest('GET', userAreaUrl, null, req.session.accessToken);
        res.locals.view = 'user-area';
        res.locals.data = userData;
    } catch (error) {
        res.locals.view = 'sign-in';
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
