const axios = require('axios');

const getAllElections = async (req, res) => {
    console.log("[CONTROLLER] getEndpoint");
    res.render('dashboard');
};

const getElection = async (req, res) => {
    console.log("[CONTROLLER] getElection");
    const electionData = {
        question: "sample question?"
    };
    res.render('election-info', { electionData });
};


module.exports = {
    getAllElections,
    getElection,
}