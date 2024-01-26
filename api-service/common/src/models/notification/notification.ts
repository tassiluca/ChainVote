import mongoose, {Model, Schema, Types} from "mongoose"

export enum EventType {
    OPENING_ELECTION = "opening-election",
    CLOSING_ELECTION = "closing-election",
}

export interface INotification{
    date: Date,
    type: EventType,
    userId?: Types.ObjectId
    text?: string
}

type NotificationDocumentType = Model<INotification>
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
    userId: {
        type: Schema.Types.ObjectId,
        ref: "Users",
        required: false,
    },
    text: {
        type: String,
        required: false,
    }
});

const NotificationModel = mongoose.model<INotification>("Notification", Notification);
export {NotificationModel as Notification};
