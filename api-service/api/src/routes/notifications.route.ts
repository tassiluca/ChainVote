import RedisLimiterStorage from "../configs/redis.config";
import { Router } from "express";
import { apiLimiter, ApiLimiterEntry, authenticationHandler } from 'core-components'
import { getAllNotifications, readNotification } from "../controllers/notifications";

const notificationsRoute = Router();
const limitStorage = new RedisLimiterStorage();
const API_LIMITER_RULES: ApiLimiterEntry = {
  "/all": {
    "GET": {
      time: 10,
      limit: 100
    }
  }
}

notificationsRoute.use(apiLimiter(API_LIMITER_RULES, limitStorage));

/**
 * @openapi
 *
 * paths:
 *   /notifications/all:
 *      get:
 *          summary: Return all the notifications for the user
 *          responses:
 *              '200':
 *                  description: Ok
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
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
 *
 */
notificationsRoute.get("/all", authenticationHandler, getAllNotifications);

/**
 * @openapi
 *
 * paths:
 *   /notifications/{notificationId}:
 *      put:
 *          summary: Mark a notification as read
 *          parameters:
 *              - name: notificationId
 *                in: path
 *                description: The id of the notification to mark as read
 *                required: true
 *                schema:
 *                    type: string
 *          responses:
 *              '201':
 *                  description: Created
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
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
 *
 */
notificationsRoute.put("/:notificationId", authenticationHandler, readNotification);

export default notificationsRoute;
