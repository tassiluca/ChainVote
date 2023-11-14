const axiosRequest = require('./utils');

// TODO: just for testing
const token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjU1MzQ1ZjIxZTM4NWEyODhkOGJjN2E0IiwiZW1haWwiOiJwcm92YS5wcm92YUB0ZXN0Lml0IiwicGFzc3dvcmQiOiIkMmIkMTAkb2QvQ1ZKa3pQRm9xb0tpUEdqdW0zZU4uNi9vTzVNZk02dFVzMFBDMjV3NUlLY1NYNjFBUWkiLCJmaXJzdE5hbWUiOiJHaW9wYWluIiwic2Vjb25kTmFtZSI6Ik5vR2FpbiIsInJvbGUiOiJ1c2VyIiwiX192IjowfSwiaWF0IjoxNjk5OTU2NTE4LCJleHAiOjE2OTk5NTc0MTgsImF1ZCI6Imh0dHBzOi8vd3d3LmNoYWludm90ZS5jb20iLCJpc3MiOiJDaGFpblZvdGUifQ.plRW9IR1WPLvKapCHKHugRCiupM0WEIKIDDiRtw5FlRjknr5FuznpEbJ9WMWBgSbFjaaAFaU8Sya2Qap3kADm9s1diU92H8fY1t4Ecv4i9yu96OTlALuo8lQTvlIfXuOAiTUu8NOq-oYBECa82PQDQHFuXHMBv2F-abetziWzN2ov9xn0ZqcCXOoC9_QPQzR-gV-OJMUtosNyQyqY3jHseuIAmFxiTDJdlafH0EqvgBM4kUBnU4zuWjKNMF84h-tfnxYFcKHUUhzixZPUD4-D-U-dvbIdMIF1rqv8gnNxF7r02fl1HORgr3zegP3di2Cf9vob3I4EQBoKQ4AlXZQ2X6eKfnIZtikAany-rSPb5am5v4qK4UCYsxdVq-7x_MUUjt8V7NHxR72dJ9BN_gyjEvxeGv6y96-I5lUb-X7KnZCDUc_nR2i7UZvtJbU47Pxr6QmLgzQa957J6vAwSxO6P41Z0enSh7CX29FNUdQtspyKQtt29JgHJiUA-9cAQRntB3tOs4EgTYvukpnwh8utPGHcNYrYL0adYd8h0Nt_XSvkGUORYe0mJEX-iQKnHfr5AYlLCdGI7bHLhQDWSA94ZhIVj-DWAc3XnWJ6S_NZv_QZCiUraqbg3EJWiowFGZQbeZyvyQDVBUyLHkFmk0mZCyUPhu325IaGQAXa1KHCPY";

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
            const voteUrl = `http://localhost:8080/election/vote/${electionId}`;
            const voteResponse = await axiosRequest('PUT', voteUrl, data, token);
            return res.send({
                success: voteResponse.success,
            });
        }

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
    createELectionCode
}
