import {apiEndpoints} from "@/commons/globals";
import axios from "axios";
import type {Role} from "@/commons/utils";
import {defineStore} from "pinia";

export interface User {
    name: string,
    surname: string,
    email: string,
    password: string,
    role: Role,
}

export const useUserStore = defineStore('user',  () => {

    async function getUserInfo(): Promise<User> {
        const url = `${apiEndpoints.API_SERVER}/users`;
        const response = await axios.get(url);
        return response.data.data;
    }

    async function updateUserInfo(property: string, value: any): Promise<User> {
        const url = `${apiEndpoints.API_SERVER}/users`;
        const data = {property: value};
        const response = await axios.put(url, data);
        return response.data.data;
    }

    return { getUserInfo, updateUserInfo }
});