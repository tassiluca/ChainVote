import {createClient, RedisClientType} from "redis";
import {ApiLimiterStorage} from "core-components";

let redisClient: RedisClientType;

const host = process.env.REDIS_HOST || "localhost";
const port = parseInt(process.env.REDIS_PORT as string) || 6379;

(async () => {
    redisClient = createClient({
        socket: {
            host:host,
            port:port
        }
    });

    await redisClient.connect();
})();

export default class RedisLimiterStorage implements ApiLimiterStorage {

    async exists(clientId: string): Promise<boolean> {
        const exists = await redisClient.exists(clientId);
        return exists === 1;
    }

    async increaseEntry(clientId: string): Promise<number> {
        return await redisClient.incr(clientId);
    }

    async setExpiration(clientId: string, seconds: number): Promise<boolean> {
        return redisClient.expire(clientId, seconds);
    }
}


