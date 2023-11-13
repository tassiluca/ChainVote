const axiosRequest = require('./utils');

// TODO: just for testing
const token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjU1MjliNzk1MmU0MzVmODZkMGU3YTUxIiwiZW1haWwiOiJwcm92YS5wcm92YTVAdGVzdC5pdCIsInBhc3N3b3JkIjoiJDJiJDEwJDJYd25DalJSQzduZlRUOW95blEvYy5kLmRvcUI3QzRaYU14VUxCRGJOLkp3UGNYUDlxaW5LIiwiZmlyc3ROYW1lIjoiR2lvcGFpbiIsInNlY29uZE5hbWUiOiJOb0dhaW4iLCJyb2xlIjoidXNlciIsIl9fdiI6MH0sImlhdCI6MTY5OTkxMjYwNywiZXhwIjoxNjk5OTEzNTA3LCJhdWQiOiJodHRwczovL3d3dy5jaGFpbnZvdGUuY29tIiwiaXNzIjoiQ2hhaW5Wb3RlIn0.mWQudiaYMV-K5Cpad10we28lqzXSJ7g5q6afIyvhpWAtbcfQ0ZHwKBwnKP-N-NNBULVLHTe9B4pRqF6d3NKXMob0oZ8pAByTNrgh52kl0jyOzR50dF6G5G9L_oKPAsXVMUiM8CPtsVraBf38NBW-KDCYd8zHk-0dXglnBHJWFlHI0wOSkIF49i0KT5Jm30jQqhRBmGywvRcssGBdlYafxl11rFsUpPJK71-0sdDHab1iaqPucmgIQXpbBP5q97n_59ioV8A4a6dls4P-I4Cd6bjAKrYTYj-zFM3ERyyOsfC7lWwFFQ0Bg3MWeBDpq53gEWonk7dy_FcfY7ln6FbCnppmHqPQsGQX0ZLu1WAwc0mSGBkqZhqiDN68-_JX5oE9vFOXhUYQjyOuX1dBN6bfClOw5cCEUbaGHGARSnPAfx1eHYxj1TlkvzQ4tFWPwJyEm1a8pd1T4sXdUwjFQ81mOQdLDC7C9YKmxv9dUqEx6jozL_oWm83FB_UBxkXGUjr4H2-c_UgpAv8PwK8aREYa42HNu17qcRdbi5TsVn-AseNMiNQuC0RNEDqF5cnHIKqm7JAgQVcbEij7F72ByphENlKSGWj5LVjHsooYB_s53sdskLt65mZtx0HlP7e4eI9o9m4PV9J9qdnzrRqi1_cd_7EIw_hnSV0SIQWWm7vtlLc";

const getAllElections = async (req, res) => {
    try {
        const allElectionsUrl = `http://localhost:8080/election/info/all`;
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
        const electionDetailsUrl = `http://localhost:8080/election/detail/${electionId}`;
        const electionInfoDetailsUrl = `http://localhost:8080/election/info/detail/${electionId}`;
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

        //TODO: if the request is a post process the data for the election
        

        const electionDetailsUrl = `http://localhost:8080/election/detail/${electionId}`;
        const electionInfoDetailsUrl = `http://localhost:8080/election/info/detail/${electionId}`;
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


const createELectionCode = async (req, res) => {
    const electionId = req.body.electionId;
    const userId = req.body.userId;
    const data = {
        electionId: electionId,
        userId: userId
    }
    const electionCodeRequest = "http://localhost:8080/code/generate";
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
    castVote,
    createELectionCode
}
