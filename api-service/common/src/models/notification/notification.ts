import mongoose, {Model, Schema, Types} from "mongoose"
import {ReadNotification} from "../read-notification/read.notification";

export enum EventType {
    OPENING_ELECTION = "opening-election",
    CLOSING_ELECTION = "closing-election",
}

export interface INotification{
    date: Date,
    type: EventType,
    user?: Types.ObjectId
    text?: string
}

interface NotificationDocumentType extends Model<INotification> {
    getNotificationsForUser(userId: Types.ObjectId): Promise<INotification[]>;
}

const Notification = new Schema<INotification, NotificationDocumentType>({
    date: {
        type: Date,
        required: true,
    },
    type: {
        type: String,
        required: true,
        enum: EventType,
    },
    user: {
        type: Schema.Types.ObjectId,
        ref: "Users",
        required: false,
    },
    text: {
        type: String,
        required: false,
    }
});

Notification.static('getNotificationsForUser', async function getNotificationsForUser(userId: Types.ObjectId){
    const notificationQueryPromise = this.find({
        $or: [
            { user: userId },
            { user: { $exists: false } }
        ]
    }).lean().exec();
    const readNotificationIdsPromise = ReadNotification.find({ user: userId })
        .distinct('notification')
        .exec();
    const [notifications, readNotificationIds] = await Promise.all([notificationQueryPromise, readNotificationIdsPromise])
    const readNotificationsIdsString = readNotificationIds.map(id => id.toString());
    return notifications.map(notification => {
        notification["isRead"] = readNotificationsIdsString.includes(notification._id.toString());
        return notification;
    });
});

const NotificationModel = mongoose.model<INotification, NotificationDocumentType>("Notification", Notification);
export {NotificationModel as Notification};
