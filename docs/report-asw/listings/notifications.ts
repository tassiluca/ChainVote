export async function setOpeningElectionNotification(
    io: Server, 
    at: Date, 
    electionId: string, 
    goal: string
) {
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