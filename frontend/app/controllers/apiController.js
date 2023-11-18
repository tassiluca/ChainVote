const axiosRequest = require('./utils');
require('dotenv').config()

// TODO: just for testing
const token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjU1MzQyOWE3NmU5NjIyZTA3OWQyMjJiIiwiZW1haWwiOiJsdWNoaW5vLnByb3ZhQHRlc3QuaXQiLCJwYXNzd29yZCI6IiQyYiQxMCRpYk55ZGZaREF5WEs4UWtzeEcySy4uZ2FjaDZNNk9rQm1HU2FOYkwzZUUwby53N3VCUGhEaSIsImZpcnN0TmFtZSI6Ikx1Y2hpbm8iLCJzZWNvbmROYW1lIjoiVGFzc29saW5vIiwicm9sZSI6InVzZXIiLCJfX3YiOjB9LCJpYXQiOjE3MDAwMzY3ODEsImV4cCI6MTcwMDAzNzY4MSwiYXVkIjoiaHR0cHM6Ly93d3cuY2hhaW52b3RlLmNvbSIsImlzcyI6IkNoYWluVm90ZSJ9.ahbq5wIUOnueFqrv46YrlaEjA-DsZQq3hhgonUIRoNKKtnlgSMhZLftJAZDl3Tt8NjsO7-y9gPP2lNo19iwOwBP56__e9pbov6VwvROQ1BhA6zc9zq5vzPFeTtE5RzkcxTVJXJj46JZoMO29h6OzlxTrVqQy208LUa8e1xGbNXVt4uTSQsuYB2XW3o0Xn5ylb9wnERG5zasvvcFE2mv3l2yYtncAFbPS24vsc4V3X-ksHtpBB_jV5lDxr8MIeQmkUxrFigYiotmBhCbY7GPdTSKd3WnoRG-pqtshmhRU4F8NTeRE0UpuhppzGS2grov3s3cJCu8IRn93Fdjq4tIltNR8N4-v5ZNT6kRhuer0BNu4QP9QaQ5oxzwmLmBpd8y9qh7NtQjkDarMSmHWv8ScebIlaIbKUyZ0PpIJ11cRkYNByvfzfYAfozMxAJ_teBVhP1F-r1jUufQYk5-Juu8ZEWeLEndxsjAWTdfOIZyc-MSOpKGCahKWbxS8T-YxBGQhR-j-mckX2xI31OYuCFmvcvX2zfdV7IIzufVofT6kyR8vDl5KfKIOsfHykuqQf4l-rBsQ8j-B4cnt25AemD9EYjouPyLStJlPqxNS-5FabYu2AT9w4f6aT6fTxDooHefTv78a0oJeU3SrhVbMGSC5P0ajNUh0OPo5sQ2ZsGAJXYw";

const urlBackendAPI = process.env.API_URL || "http://localhost:8080/"
const urlAuthAPI = process.env.AUTH_URL || "http://localhost:8081/"



const getAllElections = async (req, res) => {
    try {
        const allElectionsUrl = `http://api-server:8080/election/info/all`;
        const electionsDetailsResponse = await axiosRequest('GET', allElectionsUrl, null, token);
        const electionsData = electionsDetailsResponse.data;
        for (let i = 0; i < electionsData.length; i++) {
            const entry = reformatDates(electionsData[i]);
            entry.open = new Date(`${entry.endDate}Z`) > Date.now();
        }

    
        res.render('dashboard', { electionsData });
    } catch (error) {
        res.render('not-found');
    }
};

const getElection = async (req, res) => {
    try {
        const electionId = req.params.electionId;
        const electionDetailsUrl = `http://api-server:8080/election/detail/${electionId}`;
        const electionInfoDetailsUrl = `http://api-server:8080/election/info/detail/${electionId}`;
        const electionDetailsResponse = await axiosRequest('GET', electionDetailsUrl, null, token);
        const electionInfoResponse = await axiosRequest('GET', electionInfoDetailsUrl, null, token);
        const electionData = reformatDates(electionDetailsResponse.data);
        electionData.choices = electionInfoResponse.data.choices;
        res.render('election-info', { electionData });
    } catch (error) {
        res.render('not-found');
    }
};

const castVote = async (req, res) => {
    try {
        const electionId = req.params.electionId;
        if(req.method === 'POST') {
           const data = {
                code: req.body.code,
                userId: req.body.userId,
                choice: req.body.choice
            }
            console.log(data);
            const voteUrl = `http://api-server:8080/election/vote/${electionId}`;
            const voteResponse = await axiosRequest('PUT', voteUrl, data, token);
            return res.send({
                success: voteResponse.success,
            });
        }

        const electionDetailsUrl = `http://api-server:8080/election/detail/${electionId}`;
        const electionInfoDetailsUrl = `http://api-server:8080/election/info/detail/${electionId}`;
        const electionDetailsResponse = await axiosRequest('GET', electionDetailsUrl, null, token);
        const electionInfoResponse = await axiosRequest('GET', electionInfoDetailsUrl, null, token);
        const electionData = reformatDates(electionDetailsResponse.data);

        electionData.choices = electionInfoResponse.data.choices;
        electionData.electionId = electionId;
        electionData.goal = electionInfoResponse.data.goal;
        res.render('cast-vote', {electionData});
    } catch (error) {
        res.render('not-found');
    }
}


const createElectionCode = async (req, res) => {
    const electionId = req.body.electionId;
    const userId = req.body.userId;
    const data = {
        electionId: electionId,
        userId: userId
    }
    const electionCodeRequest = "http://api-server:8080/code/generate";
    const electionDetailsResponse = await axiosRequest('POST', electionCodeRequest, data, token);
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
    castVote,
    createElectionCode
}
