import axios from "axios";

type Method = "GET" | "POST" | "PUT" | "DELETE";

export function makeRequest(url: string, method: Method, data: any = null, jwtToken: any = null) {

    const headers: any = {
        "Content-Type": "application/json",
    };

    if (jwtToken) {
        headers["Authorization"] = `Bearer ${jwtToken}`;
    }

    return axios({
        method: method,
        url: url,
        data: data,
        headers: headers,
    });
}