import { defineStore } from 'pinia'
import axios from 'axios'
import router from "@/router";

export enum Role { User = 'user', Admin = 'admin' }

export const useAuthStore = defineStore('auth',  () => {

  /** The url to which redirect the client after a successful login. */
  const returnUrl = '/dashboard';
  /** The timeout for the refresh token. */
  let refreshTokenTimeout: number;

  async function login(role: Role, username: string, password: string) {
    const url = "http://localhost:8180/auth/login";
    const response= await axios.post(url, { email: username, password: password });
    sessionStorage.setItem('username', username);
    sessionStorage.setItem('accessToken', response.data.data.accessToken);
    sessionStorage.setItem('refreshToken', response.data.data.refreshToken);
    sessionStorage.setItem('role', role);
    startRefreshTokenTimer();
    await router.push(returnUrl); // router.push(returnUrl); does not work here since does not reload the navbar!
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
    const url = "http://localhost:8180/auth/refresh";
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

  async function logout() {
    stopRefreshTokenTimer();
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('refreshToken');
    sessionStorage.removeItem('role');
    console.debug("Logged out!");
    await router.push('/login');
  }

  function stopRefreshTokenTimer() {
    clearTimeout(refreshTokenTimeout);
  }

  function isLogged(): boolean {
    return accessToken() !== null;
  }

  function username(): string | null {
    return sessionStorage.getItem('username');
  }

  function accessToken(): string | null {
    return sessionStorage.getItem('accessToken');
  }

  function refreshToken(): string | null {
    return sessionStorage.getItem('refreshToken');
  }

  function role(): Role | null {
    return sessionStorage.getItem('role') as Role;
  }

  return { returnUrl, role, username, accessToken, refreshToken, login, logout, isLogged }
});