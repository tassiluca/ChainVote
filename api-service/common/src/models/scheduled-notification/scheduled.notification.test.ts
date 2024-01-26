import {destroyConnection, dropCollectionsInDb, setupConnection} from "../../utils/local.db";
import {ScheduledNotification} from "./scheduled.notification";
import {EventType} from "../notification/notification";

beforeAll(async () => {
    await setupConnection();
});

afterAll(async () => {
    await destroyConnection();
});

afterEach(async () => {
    await dropCollectionsInDb();
});

describe("Scheduled notification insertion in database", () => {
  test ("Should save a scheduled notification successfully in database", async () => {

      const insertionDate = new Date();
      const newScheduledNotification = await new ScheduledNotification({
          when: insertionDate,
          type: EventType.OPENING_ELECTION,
          data: {
              electionId: "1234567890",
              goal: "Test goal"
          }
      }).save();

      // Get the scheduled notification from the database using electionId and goal
      const scheduledNotification = await ScheduledNotification.findOne({
          "data.electionId": "1234567890",
          "data.goal": "Test goal"
      });
      expect(scheduledNotification).toBeDefined();
  });

  test("Should save a notification with different data elements successfully in database", async () => {
      const newScheduledNotification = await new ScheduledNotification({
          when: new Date(),
          type: EventType.OPENING_ELECTION,
          data: {
              electionId: "1234567890",
              goal: "Test goal",
              test: "test",
              test2: "test2",
              test3: {
                    test4: "test4"
              }
          }
      }).save()

      const scheduledNotification = await ScheduledNotification.findOne({
          "data.electionId": "1234567890",
          "data.goal": "Test goal"
      });
      expect(scheduledNotification).toBeDefined();
  });
});