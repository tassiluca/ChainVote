const urlApiServer = process.env.API_SERVER_URL || "http://localhost:8080"

const {
    axiosRequest,
    chaincodeErrorType,
    badRequestErrorCode,
    badRequestErrorMessage,
    castVoteSuccessfulMessage,
    createElectionSuccessfulMessage
} = require('../utils/utils')

const getAllElections = async (req, res, next) => {
    if (req.session && req.session.accessToken) {
        try {
            const allElectionsUrl = urlApiServer + `/election/info/all`;
            const electionsDetailsResponse = await axiosRequest('GET', allElectionsUrl, null, req.session.accessToken);
            const electionsData = electionsDetailsResponse.data;
            for (let i = 0; i < electionsData.length; i++) {
                const entry = reformatDates(electionsData[i]);
                const electionDetail = await axiosRequest(
                    'GET', 
                    urlApiServer + `/election/detail/${electionsData[i].electionId}`, 
                    null, 
                    req.session.accessToken
                );
                entry.open = Object.keys(electionDetail.data.results).length === 0;
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
    if (req.session && req.session.accessToken) {
        try {
            const electionId = req.params.electionId;
            const electionDetailsUrl = urlApiServer + `/election/detail/${electionId}`;
            const electionInfoDetailsUrl = urlApiServer + `/election/info/detail/${electionId}`;
            const electionDetailsResponse = await axiosRequest('GET', electionDetailsUrl, null, req.session.accessToken);
            const electionInfoResponse = await axiosRequest('GET', electionInfoDetailsUrl, null, req.session.accessToken);
            const electionData = reformatDates(electionDetailsResponse.data);
            electionData.open = Object.keys(electionData.results).length === 0;
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
    if (req.session && req.session.accessToken) {
        try {
            const electionId = req.params.electionId;
            const electionDetailsUrl = urlApiServer + `/election/detail/${electionId}`;
            const electionInfoDetailsUrl = urlApiServer + `/election/info/detail/${electionId}`;
            const electionDetailsResponse = await axiosRequest('GET', electionDetailsUrl, null, req.session.accessToken);
            const electionInfoResponse = await axiosRequest('GET', electionInfoDetailsUrl, null, req.session.accessToken);
            const electionData = reformatDates(electionDetailsResponse.data);
            const isOpen = Object.keys(electionData.results).length === 0;
            if (isOpen) {
                electionData.choices = electionInfoResponse.data.choices;
                electionData.electionId = electionId;
                electionData.goal = electionInfoResponse.data.goal;
                res.locals.view = 'cast-vote';
                res.locals.data = electionData;
            } else {
                res.locals.view = 'election-closed';
            }
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
            message: castVoteSuccessfulMessage,
            url: redirectUrl
        });
    } catch (error) {
        let message;
        if (error.response.data.error.type === chaincodeErrorType) {
            message = error.response.data.error.message;
        } else {
            message = error.response.data.error.name;
        }
        res.status(error.response.data.code).json(
            {message: message}
        );
    }
};

const getCreateElection = async (req, res, next) => {
    if (req.session && req.session.accessToken) {
        if (req.session.role !== 'admin') {
            res.locals.view = 'no-permission';
        } else {
            res.locals.view = 'create-election';
        }
    }
    next();
};

const postCreateElection = async (req, res) => {
    if (req.body.goal && req.body.voters &&
        req.body.startDate && req.body.endDate && req.body.choices) {
        try {
            const urlCreateElection = urlApiServer + "/election";
            const urlCreateElectionInfo = urlApiServer + "/election/info";
            const data = {
                goal: req.body.goal,
                voters: req.body.voters,
                startDate: new Date(req.body.startDate).toISOString(),
                endDate: new Date(req.body.endDate).toISOString(),
                choices: req.body.choices
            }
            const responseElectionInfo = await axiosRequest('POST', urlCreateElectionInfo, data, req.session.accessToken);
            if (responseElectionInfo.success) {
                const electionId = responseElectionInfo.data.electionId;
                const responseElection = await axiosRequest('POST', urlCreateElection, {electionId: electionId}, req.session.accessToken);
                if (responseElection.success) {
                    const redirectUrl = '/elections';
                    res.status(responseElectionInfo.code).json({
                        success: true,
                        message: createElectionSuccessfulMessage,
                        url: redirectUrl
                    });
                } else {
                    res.status(responseElectionInfo.code).json({
                        message: responseElection.error.name
                    });
                }
            } else {
                res.status(responseElectionInfo.code).json({
                    message: responseElectionInfo.error.name
                });
            }
        } catch (error) {
            let message;
            if (error.response.data.error.type === chaincodeErrorType) {
                message = error.response.data.error.message;
            } else {
                message = error.response.data.error.name;
            }
            res.status(error.response.data.code).json({
                message: message
            });
        }
    } else {
        res.status(badRequestErrorCode).json({
            message: badRequestErrorMessage
        });
    }
};

const createElectionCode = async (req, res) => {
    if (req.body.electionId) {
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
            let message;
            if (error.response.data.error.type === chaincodeErrorType) {
                message = error.response.data.error.message;
            } else {
                message = error.response.data.error.name;
            }
            res.status(error.response.data.code).json({
                message: message
            });
        }
    } else {
        res.status(badRequestErrorCode).json({
            message: badRequestErrorMessage
        });
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
