import {destroyConnection, dropCollectionsInDb, setupConnection} from "../../utils/local.db";
import {ReadNotification} from "./read.notification";
import {EventType, Notification} from "../notification/notification";
import {User} from "../users/users";
import {mongo} from "mongoose";

const correctUserData = {
    email: "claudio.rossi@email.it",
    password: "PassworD1!",
    firstName: "Claudio",
    secondName: "Rossi"
};

beforeAll(async () => {
    await setupConnection();
});

afterAll(async () => {
    await destroyConnection();
});

afterEach(async () => {
    await dropCollectionsInDb();
});

describe("Read notification insertion in database", () => {
    test("Should save a read notification successfully in database", async () => {
        const user = await new User(correctUserData).save();
        const notification = await new Notification({
            date: new Date(),
            type: EventType.OPENING_ELECTION,
            user: user._id,
            text: "Test text"
        }).save();

        const readNotification = await new ReadNotification({
            user: user._id,
            notification: notification._id
        }).save();
    });

    test("Can't save a read notification with a combination of user and notification that already exists", async () => {
        const user = await new User(correctUserData).save();
        const notification = await new Notification({
            date: new Date(),
            type: EventType.OPENING_ELECTION,
            user: user._id,
            text: "Test text"
        }).save();

        const readNotification = await new ReadNotification({
            user: user._id,
            notification: notification._id
        }).save();

        try {
            await new ReadNotification({
                user: user._id,
                notification: notification._id
            }).save();
        } catch(error) {
            expect(error).toBeDefined();
            expect(error).toBeInstanceOf(mongo.MongoServerError);
        }
    });
});

