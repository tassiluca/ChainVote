/**
 * Use this file to register any variables or functions that should be available globally
 * (register globals with care, only when it makes sense to be accessible app wide).
 */
export const apiEndpoints = {
  /** The endpoint of the API server. */
  API_SERVER:'http://localhost:8080', // process.env.API_SERVER_URL ||
  /** The endpoint of the authentication server. */
  AUTH_SERVER: "http://localhost:8180", // process.env.AUTH_SERVER_URL ||
}
