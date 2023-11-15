const axiosRequest = require('./utils');
require('dotenv').config()

// TODO: just for testing
const token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjU0Y2JhOTIyOTA2NjlkMzFhOTlmMDBiIiwiZW1haWwiOiJhZG1pbkB0ZXN0Lml0IiwicGFzc3dvcmQiOiIkMmIkMTAkQzJwaVU2ZmZIdHFsY3ZkSmVnN25JT1FIdldBcUhiUE1uV0lJRU5oa28zVjZzMUtnc0NkL3UiLCJmaXJzdE5hbWUiOiJhZG1pbmlzdHJhdG9yIiwic2Vjb25kTmFtZSI6ImFkbWluIiwicm9sZSI6ImFkbWluIiwiX192IjowfSwiaWF0IjoxNjk5NTU5NjU4LCJleHAiOjE2OTk1NjA1NTgsImF1ZCI6Imh0dHBzOi8vd3d3LmNoYWludm90ZS5jb20iLCJpc3MiOiJDaGFpblZvdGUifQ.LRsgSbvs2WuZ_IW8g3i-cWYo01qiZjroDeJ6HYOIopW399JWrFBbVqd05NxYPYUuiLwbG6Rvxlp2sq-eMbecWUNZbQdkTDIL3EKbig3aAxyN1IHPbjhtYaY6nQ8jeHG9NOKGB3oRX6TlVhOxx4m-oUMGrWTNXXkFve75WDyO7ZOyvPL-hqmAHkXCh5FUInVtXQdUQ1BqXrH2_mOu3Xb39hf0NEqW16mpdnaSzui_zWSO65SCke8XvuKWQpt9brsPkdTLvE_tEgbggR9w1MqZyvaQjBqjlyR-h2dSGI--ZlJacdnCiNUNx8Y_tZ47spFL7Wp8-MIJBRm6ec2BoMY37ev_y4F7tQnjNK0pa10XvNyqZSe78zlukkHeZ1ixveYSfkR09QY0Lqy9Nl-yH-QCW2rZKuXJLomOeAlszGVPGm69KkfL66-h19NgfFFvepjkWpiC1s-DRC6Azy5OJlnqdf5I4PuLHYjeTSTmLE5d186B9Sa-vs7a_bu5-WVhy9LOclzji7Dt3dt7Dk_WBsU3X14B-0fxnfjUTsNEgRNpQvcGT9u8af3s428TVELBOtCSLk8cibdjBVnnoWAH4Qsup_n6RVRbBIkwe-30S-W37nzpH88AT9204gulKokzNoTPzPadTCfEGLCBmJgz-_oDFd9H8s0mVRfimRZFrNkPkS4";

const getAllElections = async (req, res) => {
    try {
        const allElectionsUrl = process.env.ALL_ELECTIONS_URL;
        const electionsDetailsResponse = await axiosRequest('GET', allElectionsUrl, null, token);
        const electionsData = electionsDetailsResponse.data;
        for (let i = 0; i < electionsData.length; i++) {
            const entry = reformatDates(electionsData[i]);
            entry.open = new Date(entry.endDate) > Date.now();
        }
        res.render('dashboard', { electionsData });
    } catch (error) {
        res.render('not-found');
    }
};

const getElection = async (req, res) => {
    try {
        const electionId = req.params.electionId;
        const electionDetailsUrl = process.env.ELECTION_DETAIL + `/${electionId}`;
        const electionInfoDetailsUrl = process.env.ELECTION_DETAIL + `/${electionId}`;
        const electionDetailsResponse = await axiosRequest('GET', electionDetailsUrl, null, token);
        const electionInfoResponse = await axiosRequest('GET', electionInfoDetailsUrl, null, token);
        const electionData = reformatDates(electionDetailsResponse.data);
        electionData.choices = electionInfoResponse.data.choices;
        res.render('election-info', { electionData });
    } catch (error) {
        res.render('not-found');
    }
};

function reformatDates(electionData) {
    electionData.formattedStartDate = formatDate(electionData.startDate);
    electionData.formattedEndDate = formatDate(electionData.endDate);
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
}
