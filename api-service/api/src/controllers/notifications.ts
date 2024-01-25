import {NextFunction, Request, Response} from "express";
import { Notification, ReadNotification } from 'core-components'
import { StatusCodes } from 'http-status-codes'

export async function getAllNotifications(req: Request, res: Response, next: NextFunction) {
  try {
    const userNotifications: any = await Notification.getNotificationsForUser(res.locals.user._id);
    console.debug(userNotifications);
    res.locals.code = StatusCodes.OK;
    res.locals.data = userNotifications.map((n: any) => ({
      text: n.text,
      date: n.date,
      read: n.isRead,
      type: n.type
    })).sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
    for (const notification of userNotifications.filter((n: any) => !n.isRead)) {
      await new ReadNotification({
        user: res.locals.user._id,
        notification: notification._id,
      }).save();
    }
  } catch (error) {
    return next(error);
  }
  return next();
}
