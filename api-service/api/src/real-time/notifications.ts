import {Server} from "socket.io";
import {EventType, ScheduledNotification, Notification} from "core-components";

const notificationEvent = "new-notification"

/** The number of milliseconds before the closing of an election at which send a notification. */
const noticeTime: number = 60_000;

export async function loadAllScheduledNotifications() {
  // TODO: load all ScheduledNotifications at server startup
  console.debug("Loading all scheduled notifications")
}

export async function setClosingElectionNotification(io: Server, at: Date, electionId: string, goal: string) {
  const noticeClosingNotification = new ScheduledNotification({
    when: new Date(at.getTime() - noticeTime),
    type: EventType.CLOSING_ELECTION,
    data: {electionId, goal},
  });
  await noticeClosingNotification.save();
  setTimeout(() => {
    emitNotification(io, EventType.CLOSING_ELECTION, `The election ${electionId}: ${goal} is closing.`)
  }, at.getTime() - Date.now() - noticeTime);
  const closedNotification = new ScheduledNotification({
    when: at,
    type: EventType.CLOSING_ELECTION,
    data: {electionId, goal},
  });
  await closedNotification.save();
  setTimeout(() => {
    io.to("election-" + electionId).emit("closed");
  }, at.getTime() - Date.now());
}

export async function setOpeningElectionNotification(io: Server, at: Date, electionId: string, goal: string) {
  const openingNotification = new ScheduledNotification({
    when: at,
    type: EventType.OPENING_ELECTION,
    data: {electionId, goal},
  });
  await openingNotification.save();
  setTimeout(() => {
    emitNotification(io, EventType.OPENING_ELECTION, `The election ${electionId}: ${goal} has been opened.`)
    io.to("election-" + electionId).emit("opened");
  }, at.getTime() - Date.now());
}

async function emitNotification(io: Server, event: EventType, body: string) {
  io.emit(notificationEvent, body)
  const notification = new Notification({
    date: new Date(),
    type: event,
    text: body,
  })
  await notification.save();
}
