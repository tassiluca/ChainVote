const axios = require('axios');

const urlBackendAPI = process.env.API_URL || "http://localhost:8080"
const urlLogin = process.env.AUTH_URL || "http://localhost:8180"

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
}
