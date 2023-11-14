const axiosRequest = require('./utils');

// TODO: just for testing
const token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjU1MzQyOWE3NmU5NjIyZTA3OWQyMjJiIiwiZW1haWwiOiJsdWNoaW5vLnByb3ZhQHRlc3QuaXQiLCJwYXNzd29yZCI6IiQyYiQxMCRpYk55ZGZaREF5WEs4UWtzeEcySy4uZ2FjaDZNNk9rQm1HU2FOYkwzZUUwby53N3VCUGhEaSIsImZpcnN0TmFtZSI6Ikx1Y2hpbm8iLCJzZWNvbmROYW1lIjoiVGFzc29saW5vIiwicm9sZSI6InVzZXIiLCJfX3YiOjB9LCJpYXQiOjE2OTk5NTUzNTcsImV4cCI6MTY5OTk1NjI1NywiYXVkIjoiaHR0cHM6Ly93d3cuY2hhaW52b3RlLmNvbSIsImlzcyI6IkNoYWluVm90ZSJ9.ci6QeGUuAClJHoBklE0X_BZRUZ4aJBOIKAjCFOgaBIPRzMMtC3OSsK-0WqyjZyRAqt_RDkSG4B_rH6v8Y8DeSIgEVKDP5QU92kYVriBB_DlKN9WeOg4e_1v1dxuDTdoVgPR_QEAYK268qJLDNoY6T2ofGMcvb8Jrbx7ltv0bdxGoYbPbd1XnpckW5CLr6DiI55gCpcjveovnCo0FLdhsXXdDcUhRGhAfrDFbsYRYXRfblC1jhDkgyU44W6-tvkqCdwcwmXtZ1FgFALiZjtNjYZFbeg7s8V-0sjk-RnHpemunuAsL1ZduEkcqYIY9VjD73e8F8X3oGsKqvqxOebe6QB7TdZwhNxOmNeKaP0t4t3pspSDlzwwuD0SekOgx8c9gMRX9ZZy_DKbBhSBqaFiA9dTvEnKDYu5ia2z2bHaEi6V6Tbho6yhyGv9FpeL-0du9MbFBdn6P3P5n0mm9yjUTCqLhn_YLkPfOYZPOekkdWfSRWFCkdIO4rVsVY3V9D3fX6vXYYAIgJgQsB-M1bc1S6Hkdhp2HZvrWef6JrS-r921fGh9qsGXhcGmEu-kuanFKMWr_R-tcH4brfOPkL3m-FaFUK5HHyXh-KTpiCK5HKGcyzYykOQZaqLx5UR_x5Ogi7eJf6zeNx5lO9mJQl3duDrm12XU2MSLVB7-I90GYM9I";

const getAllElections = async (req, res) => {
    try {
        const allElectionsUrl = `http://localhost:8080/election/info/all`;
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
}
