import mongoose, {Model, Schema, Types} from "mongoose"

export interface IReadNotification{
    user: Types.ObjectId,
    notification: Types.ObjectId
}

type ReadNotificationDocumentType = Model<IReadNotification>
const ReadNotification = new Schema<IReadNotification, ReadNotificationDocumentType>({
    user: {
        type: Schema.Types.ObjectId,
        ref: "Users",
        required: true,
    },
    notification: {
        type: Schema.Types.ObjectId,
        ref: "Notification",
        required: true,
    }
});

ReadNotification.index({ user: 1, notification: 1 }, { unique: true });

const ReadNotificationModel = mongoose.model<IReadNotification>("ReadNotification", ReadNotification);
export {ReadNotificationModel as ReadNotification};