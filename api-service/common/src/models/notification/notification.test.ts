import {destroyConnection, dropCollectionsInDb, setupConnection} from "../../utils/local.db";
import {EventType, Notification} from "./notification";
import {User} from "../users/users";
import {ReadNotification} from "../read-notification/read.notification";

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

describe("Notification insertion in database", () => {
    test("Should save a notification successfully in database", async () => {
        const user = await new User(correctUserData).save();
        const notification = await new Notification({
            date: new Date(),
            type: EventType.OPENING_ELECTION,
            user: user._id,
            text: "Test text"
        }).save();
    });

    test("Should check all notifications already readed by a user with isRead field", async () => {
        const user = await new User(correctUserData).save();
        const notification = await new Notification({
            date: new Date(),
            type: EventType.OPENING_ELECTION,
            user: user._id,
            text: "Test text"
        }).save();

        const notification2 = await new Notification({
            date: new Date(),
            type: EventType.OPENING_ELECTION,
            user: user._id,
            text: "Test text"
        }).save();

        const notification3 = await new Notification({
            date: new Date(),
            type: EventType.OPENING_ELECTION,
            user: user._id,
            text: "Test text"
        }).save();

        const readNotification = await new ReadNotification({
            user: user._id,
            notification: notification._id
        }).save();


        const notifications: any = await Notification.getNotificationsForUser(user._id);
        expect(notifications.length).toBe(3);
        expect(notifications[0].isRead).toBe(true);
        expect(notifications[1].isRead).toBe(false);
        expect(notifications[2].isRead).toBe(false);
    });

});