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

notificationsRoute.get("/all", authenticationHandler, getAllNotifications);

notificationsRoute.put("/:notificationId", authenticationHandler, readNotification);

export default notificationsRoute;
