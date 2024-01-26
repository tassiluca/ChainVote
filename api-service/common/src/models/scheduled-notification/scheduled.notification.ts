import mongoose, {Model, Schema} from "mongoose"
import {EventType} from "../notification/notification";


export interface IScheduledNotification{
    when: Date,
    type: EventType,
    data?: Schema.Types.Mixed
}

type ScheduledNotificationDocumentType = Model<IScheduledNotification>
const ScheduledNotification = new Schema<IScheduledNotification, ScheduledNotificationDocumentType>({
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

