import axios from "axios";

type Method = "GET" | "POST" | "PUT" | "DELETE";

export function makeRequest(url: string, method: Method, jwtToken: any = null, data: any = null) {

    const headers: any = {
        "Content-Type": "application/json",
    };

    if (jwtToken) {
        headers["Authorization"] = `Bearer ${jwtToken}`;
    }

    return axios({
        method,
        url,
        data,
        headers,
    });
}