const axiosRequest = require('../utils/utils');

const urlApiServer = process.env.API_SERVER_URL || "http://localhost:8080"

const getAllElections = async (req, res, next) => {
    if (typeof req.session === 'undefined' || typeof req.session.accessToken === 'undefined') {
        res.locals.view = 'sign-in';
    } else {
        try {
            const allElectionsUrl = urlApiServer + `/election/info/all`;
            const electionsDetailsResponse = await axiosRequest('GET', allElectionsUrl, null, req.session.accessToken);
            const electionsData = electionsDetailsResponse.data;
            for (let i = 0; i < electionsData.length; i++) {
                const entry = reformatDates(electionsData[i]);
                entry.open = Date.now() > new Date(`${entry.startDate}Z`) && new Date(`${entry.endDate}Z`) > Date.now();
            }
            res.locals.data = electionsData;
            res.locals.view = 'dashboard';
        } catch (error) {
            res.locals.view = 'error';
        }
    }
    next();
};

const getElection = async (req, res, next) => {
    if (typeof req.session === 'undefined' || typeof req.session.accessToken === 'undefined') {
        res.locals.view = 'sign-in';
    } else {
        try {
            const electionId = req.params.electionId;
            const electionDetailsUrl = urlApiServer + `/election/detail/${electionId}`;
            const electionInfoDetailsUrl = urlApiServer + `/election/info/detail/${electionId}`;
            const electionDetailsResponse = await axiosRequest('GET', electionDetailsUrl, null, req.session.accessToken);
            const electionInfoResponse = await axiosRequest('GET', electionInfoDetailsUrl, null, req.session.accessToken);
            const electionData = reformatDates(electionDetailsResponse.data);
            electionData.choices = electionInfoResponse.data.choices;
            electionData.electionId = electionId;
            res.locals.data = electionData;
            res.locals.view = 'election-info';
        } catch (error) {
            res.locals.view = 'not-found';
        }
    }
    next();
};

const getCastVote = async (req, res, next) => {
    if (typeof req.session === 'undefined' || typeof req.session.accessToken === 'undefined') {
        res.locals.view = 'sign-in';
    } else {
        try {
            const electionId = req.params.electionId;
            const electionDetailsUrl = urlApiServer + `/election/detail/${electionId}`;
            const electionInfoDetailsUrl = urlApiServer + `/election/info/detail/${electionId}`;
            const electionDetailsResponse = await axiosRequest('GET', electionDetailsUrl, null, req.session.accessToken);
            const electionInfoResponse = await axiosRequest('GET', electionInfoDetailsUrl, null, req.session.accessToken);
            const electionData = reformatDates(electionDetailsResponse.data);

            electionData.choices = electionInfoResponse.data.choices;
            electionData.electionId = electionId;
            electionData.goal = electionInfoResponse.data.goal;

            res.locals.view = 'cast-vote';
            res.locals.data = electionData;
        } catch (error) {
            res.locals.view = 'error';
        }
    }
    next();
};

const postCastVote = async (req, res) => {
    try {
        const electionId = req.params.electionId;
        const data = {
            code: req.body.code,
            choice: req.body.choice
        }
        const voteUrl = urlApiServer + `/election/vote/${electionId}`;
        const voteResponse = await axiosRequest('PUT', voteUrl, data, req.session.accessToken);
        const redirectUrl = '/';
        return res.send({
            success: voteResponse.success,
            message: "Vote casted successfully.",
            url: redirectUrl
        });
    } catch (error) {
        res.status(error.response.data.code).json(
            {message: error.response.data.error.message}
        );
    }
};

const getCreateElection = async (req, res, next) => {
    if (typeof req.session === 'undefined' || typeof req.session.accessToken === 'undefined') {
        res.locals.view = 'sign-in';
    } else if (req.session.role !== 'admin') {
        res.locals.view = 'no-permission';
    } else {
        res.locals.view = 'create-election';
    }
    next();
};

const postCreateElection = async (req, res) => {
    try {
        const urlCreateElection = urlApiServer + "/election";
        const urlCreateElectionInfo = urlApiServer + "/election/info";

        if (req.body.goal && req.body.voters && req.body.startDate && req.body.endDate && req.body.choices) {
            const goal = req.body.goal;
            const voters = req.body.voters;
            const startDate = new Date(req.body.startDate).toISOString();
            const endDate = new Date(req.body.endDate).toISOString();
            const choices = req.body.choices;

            const responseElectionInfo = await axiosRequest('POST', urlCreateElectionInfo, {
                goal: goal, voters: voters, startDate: startDate, endDate: endDate, choices: choices
            }, req.session.accessToken);
            if (responseElectionInfo.success) {
                const electionId = responseElectionInfo.data.electionId;
                const responseElection = await axiosRequest('POST', urlCreateElection, {electionId: electionId}, req.session.accessToken);
                if (responseElection.success) {
                    const redirectUrl = '/elections';
                    res.status(responseElectionInfo.code).json({
                        success: true,
                        message: "Election created successfully.",
                        url: redirectUrl
                    });
                } else {
                    res.status(responseElectionInfo.code).json({
                        name: responseElection.error.name,
                        message: responseElection.error.message
                    });
                }
            } else {
                res.status(responseElectionInfo.code).json({
                    name: responseElectionInfo.error.name,
                    message: responseElectionInfo.error.message
                });
            }
        } else {
            res.status(403).json({
                message: "Bad attributes."
            });
        }
    } catch (error) {
        res.status(error.response.data.code).json(
            {message: error.response.data.error.message}
        );
    }
};

const createElectionCode = async (req, res) => {
    try {
        const electionId = req.body.electionId;
        const data = {
            electionId: electionId
        }
        const electionCodeRequest = urlApiServer + "/code/generate";
        const electionDetailsResponse = await axiosRequest('POST', electionCodeRequest, data, req.session.accessToken);
        if (electionDetailsResponse.success) {
            return res.send({
                success: true,
                code: electionDetailsResponse.data
            });
        }
    } catch (error) {
        res.status(error.response.data.code).json(
            {message: error.response.data.error.message}
        );
    }
};

function reformatDates(electionData) {
    electionData.formattedStartDate = formatDate(`${electionData.startDate}Z`);
    electionData.formattedEndDate = formatDate(`${electionData.endDate}Z`);
    return electionData;
}

function formatDate(date) {
    return new Date(date).toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric',
        hour: 'numeric', 
        minute: 'numeric', 
        second: 'numeric', 
        timeZoneName: 'short' 
    });
}

module.exports = {
    getAllElections,
    getElection,
    getCastVote,
    postCastVote,
    createElectionCode,
    getCreateElection,
    postCreateElection,
}
