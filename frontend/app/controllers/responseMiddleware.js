"use strict";

const responseMiddleware = (req, res, next) => {
    res.locals.user = req.user;

    const data = res.locals.data;
    const view = res.locals.view;

    if (view === undefined) {
        return res.render('not-found'); // Default for errors --> Change in something else
    }

    const responseData = {
        logged: false,
        data: data
    };

    if (req.session) {
        responseData.logged = true;
        responseData.email = req.session.email
    }

    return res.render(view, { responseData: responseData });
}

module.exports = responseMiddleware;