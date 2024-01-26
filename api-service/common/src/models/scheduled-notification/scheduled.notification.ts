import mongoose, {Model, Schema, Types} from "mongoose"

export enum EventType {
    OPENING_ELECTION = "opening-election",
    CLOSING_ELECTION = "closing-election",
}

export interface IScheduledNotification{
    when: Date,
    type: EventType,
    data?: Schema.Types.Mixed
}

type PendingNotificationDocumentType = Model<IScheduledNotification>
const ScheduledNotification = new Schema<IScheduledNotification, PendingNotificationDocumentType>({
    when: {
        type: Date,
        required: true,
    },
    type: {
        type: String,
        required: true,
        enum: EventType,
    },
    data: {
        type: Schema.Types.Mixed,
    }
});

const ScheduledNotificationModel = mongoose.model<IScheduledNotification>("ScheduledNotification", ScheduledNotification);
export {ScheduledNotificationModel as ScheduledNotification};

