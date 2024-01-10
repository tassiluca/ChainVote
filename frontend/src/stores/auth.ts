import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth',  () => {

  /** The url to which redirect the client after a successful login. */
  const returnUrl = '/';
  /** The role of the user, i.e. 'user' or 'admin'. */
  const role = "";
  /** The user object. */
  const user = localStorage.getItem("user");

  async function login(username: string, password: string) {
    try {
      const url = "http://auth-server:8180/auth/login";
      console.log(`auth request: ${username} - ${password} - ${url}}`);
      const response= await axios.post(
        url,
        { email: username, password: password }
      );
      console.log(response.data);
      // localStorage.setItem("user", JSON.stringify(response.data));
      // role.value = ...;
    } catch (error) {
      console.log(`Axios error: ${error}`);
    }
  }

  function logout() {

  }

  async function refreshToken() {

  }

  function startRefreshTokenTimer() {

  }

  function stopRefreshTokenTimer() {

  }

  return { returnUrl, role, user, login, logout }
});