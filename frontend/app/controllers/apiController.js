const axiosRequest = require('./utils');

const urlApiServer = process.env.API_SERVER_URL || "http://api-server:8080"

const getAllElections = async (req, res, next) => {
    try {
        const allElectionsUrl = urlApiServer + `/election/info/all`;
        const electionsDetailsResponse = await axiosRequest('GET', allElectionsUrl, null, req.session.accessToken);
        const electionsData = electionsDetailsResponse.data;
        for (let i = 0; i < electionsData.length; i++) {
            const entry = reformatDates(electionsData[i]);
            entry.open = new Date(`${entry.endDate}Z`) > Date.now();
        }
        res.locals.data = electionsData;
        res.locals.view = 'dashboard';
    } catch (error) {
        res.locals.view = 'sign-in';
    }
    next();
};

const getElection = async (req, res, next) => {
    try {
        const electionId = req.params.electionId;
        const electionDetailsUrl = urlApiServer + `/election/detail/${electionId}`;
        const electionInfoDetailsUrl = urlApiServer + `/election/info/detail/${electionId}`;
        const electionDetailsResponse = await axiosRequest('GET', electionDetailsUrl, null, req.session.accessToken);
        const electionInfoResponse = await axiosRequest('GET', electionInfoDetailsUrl, null, req.session.accessToken);
        const electionData = reformatDates(electionDetailsResponse.data);
        electionData.choices = electionInfoResponse.data.choices;
        res.locals.data = electionData;
        res.locals.view = 'election-info';
    } catch (error) {
        res.locals.view = 'sign-in';
    }
    next();
};

const getCastVote = async (req, res, next) => {
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
        res.locals.view = 'sign-in';
    }
    next();
}

const postCastVote = async (req, res) => {
    const data = {
        code: req.body.code,
        userId: req.body.userId,
        choice: req.body.choice
    }
    const voteUrl = urlApiServer + `/election/vote/${electionId}`;
    const voteResponse = await axiosRequest('PUT', voteUrl, data, req.session.accessToken);
    return res.send({
        success: voteResponse.success,
    });
}

const createElectionCode = async (req, res) => {
    const electionId = req.body.electionId;
    const userId = req.body.userId;
    const data = {
        electionId: electionId,
        userId: userId
    }
    const electionCodeRequest = urlApiServer + "/code/generate";
    const electionDetailsResponse = await axiosRequest('POST', electionCodeRequest, data, req.session.accessToken);
    console.log(electionDetailsResponse);
    if (electionDetailsResponse.success) {
        return res.send({
            success: true,
            code: electionDetailsResponse.data
        });
    }
    const error = electionDetailsResponse.error;
    throw new Error("Error generating code: " + error.message);
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
    createElectionCode,
    postCastVote
}
