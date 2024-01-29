import {NextFunction, Request, Response} from "express";
import { Notification, ReadNotification } from 'core-components'
import { StatusCodes } from 'http-status-codes'
import mongoose from 'mongoose'

export async function getAllNotifications(req: Request, res: Response, next: NextFunction) {
  try {
    const userNotifications: any = await Notification.getNotificationsForUser(res.locals.user._id);
    console.debug(userNotifications);
    res.locals.code = StatusCodes.OK;
    res.locals.data = userNotifications.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
  } catch (error) {
    return next(error);
  }
  return next();
}

export async function readNotification(req: Request, res: Response, next: NextFunction) {
  try {
    const readNotification = new ReadNotification({
      user: new mongoose.Types.ObjectId(res.locals.user._id),
      notification: new mongoose.Types.ObjectId(req.params.notificationId),
    });
    await readNotification.save();
    res.locals.code = StatusCodes.CREATED;
    res.locals.data = true;
  } catch (error) {
    return next(error);
  }
  return next();
}
