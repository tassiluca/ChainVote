import {apiEndpoints} from "@/commons/globals";
import axios from "axios";
import type {Role} from "@/commons/utils";
import {defineStore} from "pinia";
import { useAuthStore } from '@/stores/auth'

export interface User {
    firstName: string,
    secondName: string,
    email: string,
    password?: string,
    role: Role,
}

export interface UserCreation {
    email: string
    firstName: number
    secondName: string
    password: string
}

export const useUserStore = defineStore('user',  () => {

    const authStore = useAuthStore();

    async function getUserInfo(): Promise<User> {
        const url = `${apiEndpoints.API_SERVER}/users/${authStore.user}`;
        const response = await axios.get(url);
        return response.data.data;
    }

    async function updateUserInfo(property: string, value: string): Promise<User> {
        const url = `${apiEndpoints.API_SERVER}/users/${authStore.user}`;
        const data = { data: {[property]: value} };
        const response = await axios.put(url, data);
        return response.data.data;
    }

    async function passwordResetRequest(email: string) {
        const url = `${apiEndpoints.API_SERVER}/users/password-forgotten`;
        const data = {email: email};
        const response = await axios.post(url, data);
        return response.data.data;
    }

    async function registration(user: UserCreation): Promise<{ success: boolean, msg: string }> {
        const urlCreation = `${apiEndpoints.API_SERVER}/users`;
        const responseResult = await axios.post(urlCreation, user);

        if (responseResult.status !== 201) {
            return { success: false, msg: "Error during registration"};
        }
        return { success: true, msg: "Registration successful"};
    }

    return { getUserInfo, updateUserInfo, passwordResetRequest, registration }
});
