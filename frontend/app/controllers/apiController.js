const axiosRequest = require('./utils');

const token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjU0YjcwY2JiMDgwODAxMjExNTZkNGMzIiwiZW1haWwiOiJlbWFpbC5wcm92YUB0ZXN0Lml0IiwicGFzc3dvcmQiOiIkMmIkMTAkd2U1ak5QYWQ1S0RRYlQzZXc5MlJvdWtMeThYNmxKbi5NTS5EcTV5SWg0WUp5YmlrRkxpbi4iLCJmaXJzdE5hbWUiOiJHaW9wYWluIiwic2Vjb25kTmFtZSI6Ik5vR2FpbiIsInJvbGUiOiJ1c2VyIiwiX192IjowfSwiaWF0IjoxNjk5NTQyMjQ3LCJleHAiOjE2OTk1NDMxNDcsImF1ZCI6Imh0dHBzOi8vd3d3LmNoYWludm90ZS5jb20iLCJpc3MiOiJDaGFpblZvdGUifQ.nPLWPqgu8eWJPuNb_XKopANn-Gyq0NkdDLkCRjDmNqy4o5se3-otKd9uQDLr5ifQxnFWL7ANEGye3PdmdAMSgppltmK2nzAaueJfU7lFWCsV6DpwuArASk1I1JCac2YQ229C9nMoRpOiiFBe3y2xDcwd9tmwyHOOpnXrkuoGYQLnNXpL6pqaTNfO4-PpsN9DNg2s5N_Q7vURHITdiy4D4zrBkX7nMak7BoURapubEkc3nv8vghbWiHhNiMI8HFcB7qrHFDH7qSBFCAQRvr2YCUG03pFfA-yHW6zK_05vjf8ni0Dcsa3XI5pJELBkIHlyTA5iCEVj5jbtA-GX1ZAmIgKjdqz2SplFHW0iyrekpJz2DrqT2SZM5runZDToClLFLrMlpoJpUc7wqjVn_cGZN4-430Z0PrV88BkaaB9K-ZP0E7PgtQdDqiIGUok-2wDJQNBLpvQesdFAT_sfUBq2ksPC_CPZjJTpTY1ySChmw76bWFmX7ZlQekOxbtqAXhzxAVm-WFkyrvDUeKx9fx2HIH1mbEX8Rosumj3HzbFH6pFgf0m81eu1pNFlHM7iIdqm0r9w7CCY-nRkO-Ii3jsoR0PZqAa4SK2HCAyXrTUBI1MWW7heYsnPai61CZqhEuui2BqcNBiyJZnAkzBLb7VCuoa-M_gtobJBKmjZ6N-gZHs";

const getAllElections = async (req, res) => {
    try {
        const allElectionsUrl = `http://localhost:8080/election/info/all`;
        // const electionDetailsResponse = await axiosRequest('GET', allElectionsUrl, null, token);
        // console.log(electionDetailsResponse);
        res.render('dashboard');
    } catch (error) {
        res.render('not-found');
    }
};

const getElection = async (req, res) => {
    try {
        const electionId = req.params.electionId;
        const electionDetailsUrl = `http://localhost:8080/election/detail/${electionId}`;
        // const electionInfoDetailsUrl = `http://localhost:8080/election/info/detail/${electionId}`;
        const electionDetailsResponse = await axiosRequest('GET', electionDetailsUrl, null, token);
        const electionData = electionDetailsResponse.data;
        console.log(electionData);
        res.render('election-info', { electionData });
    } catch (error) {
        res.render('not-found');
    }
};

module.exports = {
    getAllElections,
    getElection,
}
