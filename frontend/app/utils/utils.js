const axios = require('axios');
const chaincodeErrorCode = '500'
const badRequestErrorCode = '400'
const badRequestErrorMessage = 'Bad request'

const axiosRequest = async (method, url, data = null, token = null) => {
    try {
        const config = {
            method: method,
            url: url,
            headers: {},
        };
        if (data) {
            config.data = data;
        }
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        const response = await axios(config);
        return response.data;
    } catch (error) {
        console.error(`Error making API call to ${url}:`, error.response.data);
        throw error;
    }
};

module.exports = {
    axiosRequest,
    chaincodeErrorCode,
    badRequestErrorCode,
    badRequestErrorMessage
}
