import {Server} from "node:http";
import {Server as SocketIoServer} from "socket.io";

const SocketIoConfig = (httpServer: Server): SocketIoServer => {
    const io = new SocketIoServer(httpServer, {
        cors: {
            origin: "*",
        }
    });
    io.on("connection", (socket) => {
        console.debug(`Socket ${socket.id} connected`);

        /** On joinRoom event, the socket joins the room passed as parameter. */
        socket.on("joinRoom", (room: string) => {
            socket.join(room);
            console.debug(`Socket ${socket.id} joined room ${room}`);
        });

        /** On disconnect event, the socket leaves all the rooms it joined. */
        socket.on("disconnect", () => {
            console.debug(`Socket ${socket.id} disconnected`);
        });
    });
    return io;
}

export default SocketIoConfig;
