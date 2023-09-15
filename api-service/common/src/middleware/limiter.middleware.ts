import { Request, Response, NextFunction } from "express";
import ApiLimiterStorage from "../utils/limiter_storage/limiter_storage";
import {TooManyRequests} from "../errors/errors";

export interface ApiLimiterRule {
    time: number;
    limit: number;
}

export interface ApiLimiterEntry {
    [endpoint: string]: ApiLimiterRule;
}

export async function apiLimiter(rules: ApiLimiterEntry, storage: ApiLimiterStorage) {
    return async function(req: Request, res: Response, next: NextFunction) {
        const endpoint = req.path;
        const clientId = `${endpoint}/${req.ip}`;

        if(!rules[endpoint]) {
            return next();
        }

        const currentEntries = await storage.increaseEntry(clientId);
        if(currentEntries > rules[endpoint].limit) {
            throw new TooManyRequests("Api Limit Reached");
        }

        await storage.setExpiration(clientId, rules[endpoint].time);
        return next();
    }
}
