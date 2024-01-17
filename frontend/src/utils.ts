import axios, {type AxiosError, type AxiosResponse} from 'axios';

const badRequestErrorCode = 400
const badRequestErrorMessage = 'Bad request'
const castVoteSuccessfulMessage = "Vote cast successfully."
const createElectionSuccessfulMessage = "Election created successfully."
const signUpSuccessfulMessage = "User successfully created."
const signInSuccessfulMessage = "User successfully logged in."

const axiosRequest = async (method: string, url: string, data: any = null, token = null) => {
    try {
        const config: any = {
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
    } catch (error: any) {
        console.error(`Error making API call to ${url}:`, error.response.data);
        throw error;
    }
};

function makeAjaxPostRequest(url: string,
                             requestData: any,
                             then: (res: any) => any,
                             errorCatch: (error: any) => any
                            ): Promise<any> {
    const headers = {
        'Content-Type': 'application/json',
    };

    return axios.post(url, requestData, { headers })
        .then((response: AxiosResponse<any>) => then(response.data))
        .catch((error: AxiosError<any>) => errorCatch(error));
}

function getBackendError(error: any) {
    return {
        message: error.response.data.error.message
    }
}

export {
    axiosRequest,
    makeAjaxPostRequest,
    getBackendError,
    badRequestErrorCode,
    badRequestErrorMessage,
    castVoteSuccessfulMessage,
    createElectionSuccessfulMessage,
    signUpSuccessfulMessage,
    signInSuccessfulMessage,
}
