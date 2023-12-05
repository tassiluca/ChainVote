import { Router } from "express";
import {login, logout, refreshToken} from "../controllers/auth";
import {apiLimiter, authenticationHandler, validationHandler} from "core-components";
import { ApiLimiterEntry } from "core-components";
import RedisLimiterStorage from "../configs/redis.config";
import {body} from "express-validator";


const API_LIMITER_RULES: ApiLimiterEntry = {
    "/login": {
        "POST": {
            time: 20,
            limit: 100
        }
    },
    "/logout": {
        "POST": {
            time: 20,
            limit: 100
        }
    },
    "/refresh": {
        "POST": {
            time: 20,
            limit: 100
        }
    }
}

const limitStorage = new RedisLimiterStorage();

const authRouter = Router();

authRouter.use(apiLimiter(API_LIMITER_RULES, limitStorage));

/**
 * @openapi
 *
 * paths:
 *   /auth/login:
 *      post:
 *          summary: Login
 *          description: Login in the application reiceving a valid access token and a refresh token.
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              email:
 *                                  type: string
 *                                  description: The email of the user.
 *                              password:
 *                                  type: string
 *                                  description: The password of the user.
 *
 *          responses:
 *              '200':
 *                  description: Ok
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
 *                                  - type: object
 *                                    properties:
 *                                      data:
 *                                          type: boolean
 *                                          description: Return true if the code is still valid, false otherwise
 *                                          example: true
 *              '400':
 *                  description: Bad Request
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/BadRequestError'
 *
 *              '429':
 *                  description: Too many requests
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/TooManyRequestError'
 *              '500':
 *                  description: Generic server error
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/InternalServerError'
 */
authRouter.post(
    "/login",
    validationHandler([
        body("email").exists().isEmail(),
        body("password").exists().isString()
    ]),
    login
);

/**
 * @openapi
 *
 * paths:
 *   /auth/logout:
 *      post:
 *          summary: Logout
 *          description: Logout a user from the system disabling the passed jwt token
 *          responses:
 *              '200':
 *                  description: Ok
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
 *                                  - type: object
 *                                    properties:
 *                                      data:
 *                                          type: boolean
 *                                          description: Return true if the code is still valid, false otherwise
 *                                          example: true
 *              '400':
 *                  description: Bad Request
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/BadRequestError'
 *
 *              '429':
 *                  description: Too many requests
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/TooManyRequestError'
 *              '500':
 *                  description: Generic server error
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/InternalServerError'
 */
authRouter.post(
    "/logout",
    authenticationHandler,
    logout
);

/**
 * @openapi
 *
 * paths:
 *   /auth/refresh:
 *      post:
 *          summary: Refresh tokens
 *          description: Return a new refresh and access token pair.
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              email:
 *                                  type: string
 *                                  description: The email of the user.
 *                              refreshToken:
 *                                  type: string
 *                                  description: The refresh token of the user.
 *          responses:
 *              '200':
 *                  description: Ok
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
 *                                  - type: object
 *                                    properties:
 *                                      data:
 *                                          type: boolean
 *                                          description: Return true if the code is still valid, false otherwise
 *                                          example: true
 *              '400':
 *                  description: Bad Request
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/BadRequestError'
 *
 *              '429':
 *                  description: Too many requests
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/TooManyRequestError'
 *              '500':
 *                  description: Generic server error
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/InternalServerError'
 */
authRouter.post(
    "/refresh",
    validationHandler([
        body("email").exists().isEmail(),
        body("refreshToken").exists().isJWT()
    ]),
    refreshToken
);

export default authRouter;
