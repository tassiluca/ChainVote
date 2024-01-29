import axios from "axios";
import {useAuthStore} from "@/stores/auth";

export default function axiosSetup() {
  const authStore = useAuthStore();

  /* Intercepts the axios request and add Authorization header if user logged int */
  axios.interceptors.request.use(
    (config) => {
      config.headers["Content-Type"] = "application/json";
      const token = authStore.accessToken;
      if (token) {
        console.log("Setting authorization header");
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error) => Promise.reject(error)
  )

  /* Intercepts the axios response and refresh access token if expired. */
  axios.interceptors.response.use(
    (response) => response,
    async function (error) {
      console.debug(error);
      const originalRequest = error.config;
      if (error.response.data.code === 400 && originalRequest.url.includes("/auth/refresh")) {
        await authStore.logout();
      } else if (error.response.data.code === 400 && error.response.data.error.message === "jwt expired") {
        await authStore.refreshAccessToken();
        return axios(originalRequest);
      }
      return Promise.reject(error);
    }
  )
}
