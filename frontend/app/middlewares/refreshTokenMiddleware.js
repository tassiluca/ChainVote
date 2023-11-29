"use strict";
const axiosRequest = require('../utils/utils');
const urlAuthServer = process.env.AUTH_SERVER_URL || "http://localhost:8180"


const refreshTokenMiddleware = async (req, res, next) => {
    try {
        if (req.session && req.session.accessToken) {
            const dateNow = Date.now();
            const dateExpire = req.session.expire;
            if (dateNow > dateExpire) {
                console.log("Refreshing token...");
                const urlRefreshToken = urlAuthServer + "/auth/refresh";
                const refreshToken = req.session.refreshToken;
                const data = {
                    email: req.session.email,
                    refreshToken: refreshToken
                };
                const refreshResponse = await axiosRequest('POST', urlRefreshToken, data);
                if(refreshResponse.success) {
                    req.session.accessToken = refreshResponse.data.accessToken;
                    req.session.refreshToken = refreshResponse.data.refreshToken;
                    req.session.expire = Date.now() + 15 * 60 * 1000;
                } else {
                    req.session.destroy();
                }
            }
        }
    } catch (error) {
        console.log(error);
        throw error;
    }
    return next();
}

module.exports = refreshTokenMiddleware;