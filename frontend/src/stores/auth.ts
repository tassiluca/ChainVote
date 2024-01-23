import { defineStore } from 'pinia'
import axios from 'axios'
import router from "@/router";
import { apiEndpoints } from "@/commons/globals";

export enum Role { User = 'user', Admin = 'admin' }

export const useAuthStore = defineStore('auth',  () => {

  /** The url to which redirect the client after a successful login. */
  const returnUrl = '/dashboard';
  /** The timeout for the refresh token. */
  let refreshTokenTimeout: number;

  /** Attempts to log in the user with the given credentials, throwing an exception if the request fails. */
  async function login(role: Role, username: string, password: string) {
    const url = `${apiEndpoints.AUTH_SERVER}/auth/login`;
    const response= await axios.post(url, { email: username, password: password });
    await verifyRole(username, role, response.data.data.accessToken);
    sessionStorage.setItem('username', username);
    sessionStorage.setItem('accessToken', response.data.data.accessToken);
    sessionStorage.setItem('refreshToken', response.data.data.refreshToken);
    sessionStorage.setItem('role', role);
    startRefreshTokenTimer();
    await router.push(returnUrl); // router.push(returnUrl); does not work here since does not reload the navbar!
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

  function startRefreshTokenTimer() {
    const jwtBase64 = accessToken()!.split('.')[1];
    const jwtPayload = JSON.parse(atob(jwtBase64));
    const expires = new Date(jwtPayload.exp * 1000);
    console.debug(expires);
    const timeout = expires.getTime() - Date.now() - (60 * 1000);
    refreshTokenTimeout = setTimeout(regenerateAccessToken, timeout);
  }

  async function regenerateAccessToken() {
    console.debug('Regenerating access token...');
    const url = `${apiEndpoints.AUTH_SERVER}/auth/refresh`;
    try {
      const response= await axios.post(url, { email: username(), refreshToken: refreshToken() });
      sessionStorage.setItem('accessToken', response.data.data.accessToken);
      sessionStorage.setItem('refreshToken', response.data.data.refreshToken);
      startRefreshTokenTimer();
    } catch (error) {
      console.error(error);
      await logout();
    }
  }

  /** Logout the user. */
  async function logout() {
    stopRefreshTokenTimer();
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('refreshToken');
    sessionStorage.removeItem('role');
    console.info("Logged out!");
    await router.push('/login');
  }

  function stopRefreshTokenTimer() {
    clearTimeout(refreshTokenTimeout);
  }

  /** Returns true if the user is logged in, false otherwise. */
  function isLogged(): boolean {
    return accessToken() !== null;
  }

  /** Returns the username of the logged user, or null if the user is not logged in. */
  function username(): string | null {
    return sessionStorage.getItem('username');
  }

  /** Returns the access token of the logged user, or null if the user is not logged in. */
  function accessToken(): string | null {
    return sessionStorage.getItem('accessToken');
  }

  /** Returns the refresh token of the logged user, or null if the user is not logged in. */
  function refreshToken(): string | null {
    return sessionStorage.getItem('refreshToken');
  }

  /** Returns the role of the logged user, or null if the user is not logged in. */
  function role(): Role | null {
    return sessionStorage.getItem('role') as Role;
  }

  return { returnUrl, role, username, accessToken, login, logout, isLogged }
});