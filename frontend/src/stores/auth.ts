import { defineStore } from 'pinia'
import { computed, ref } from "vue";
import axios from 'axios'
import router from "@/router";
import { apiEndpoints } from "@/commons/globals";
import {type Role, toRole} from "@/commons/utils";

export interface User {
  name: string,
  surname: string,
  email: string,
  password: string,
  role: Role,
}

export const useAuthStore = defineStore('auth',  () => {

  /** The url to which redirect the client after a successful login. */
  const returnUrl = '/dashboard';
  /** The user JWT access token or null if not logged. */
  const accessToken = ref(sessionStorage.getItem("accessToken"));
  /** The username of the logged user or null if not logged. */
  const user = ref(sessionStorage.getItem("username"));
  /** The role of the logged user or null if not logged. */
  const userRole = ref(toRole(sessionStorage.getItem("role")));
  /** True if the user is logged, false otherwise. */
  const isLogged = computed(() => accessToken.value !== null)

  /** Attempts to log in the user with the given credentials, throwing an exception if the request fails. */
  async function login(role: Role, username: string, password: string) {
    const url = `${apiEndpoints.AUTH_SERVER}/auth/login`;
    const response= await axios.post(url, { email: username, password: password });
    await verifyRole(username, role, response.data.data.accessToken);
    setUser(username);
    setUserRole(role);
    setAccessToken(response.data.data.accessToken);
    setRefreshToken(response.data.data.refreshToken);
    await router.push(returnUrl);
  }

  async function verifyRole(username: String, role: Role, accessToken: string) {
    const roleVerification = await axios.get(
        `${apiEndpoints.API_SERVER}/users/${username}`,
        { headers: { Authorization: `Bearer ${accessToken}` } }
    )
    if (roleVerification.data.data.role !== role) {
      throw new Error('Login error: Unauthorized');
    }
  }

  async function refreshAccessToken() {
    console.debug('Refreshing access token...');
    const url = `${apiEndpoints.AUTH_SERVER}/auth/refresh`;
    const response= await axios.post(url, {
      email: sessionStorage.getItem("username"),
      refreshToken: sessionStorage.getItem("refreshToken")
    });
    setAccessToken(response.data.data.accessToken);
    setRefreshToken(response.data.data.refreshToken);
  }

  /** Logout the user. */
  async function logout() {
    accessToken.value = null;
    user.value = null;
    userRole.value = null;
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('refreshToken');
    sessionStorage.removeItem('role');
    console.debug("Logged out!");
    // router.push('/login') does not interrupt the execution of the request that triggered the logout
    // causing the caller to not be redirected to the login page.
    window.location.href = '/login';
  }

  function setAccessToken(token: string) {
    accessToken.value = token;
    sessionStorage.setItem('accessToken', token);
    console.debug(`Expiration access token: ${new Date(1000 * JSON.parse(atob(token.split('.')[1])).exp)}`);
  }

  function setRefreshToken(token: string) {
    sessionStorage.setItem('refreshToken', token);
    console.debug(`Expiration refresh token: ${new Date(1000 * JSON.parse(atob(token.split('.')[1])).exp)}`);
  }

  function setUser(username: string) {
    user.value = username;
    sessionStorage.setItem('username', username);
  }

  function setUserRole(role: Role) {
    userRole.value = role;
    sessionStorage.setItem('role', role);
  }

  async function getUserInfo(): Promise<User> {
    const url = `${apiEndpoints.API_SERVER}/users`;
    const response = await axios.get(url);
    return response.data.data;
  }

  async function updateUserInfo(property: Record<string, string>): Promise<User> {
    const url = `${apiEndpoints.API_SERVER}/users`;
    const response = await axios.put(url, property);
    return response.data.data;
  }

  return { returnUrl, accessToken, user, userRole, login, logout, isLogged, refreshAccessToken, getUserInfo, updateUserInfo }
});