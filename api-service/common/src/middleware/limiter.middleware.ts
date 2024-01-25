import { Request, Response, NextFunction } from "express";
import {ApiLimiterStorage} from "../utils/limiter_storage/limiter_storage";
import {TooManyRequests} from "../errors/errors";

export interface ApiLimiterRule {
    [method:string]: {
        time: number;
        limit: number;
    }
}

export interface ApiLimiterEntry {
    [endpoint: string]: ApiLimiterRule;
}

/**
 * Return the base endpoint of an endpoint with params.
 * @param endpoint
 */
function getBaseEndpoint(endpoint: string): string {
    return endpoint.replace(/\/\d+/g, '');
}

/**
 * Return a middleware that limit the number of request of some endpoints.
 * @param rules
 * @param storage
 */
export function apiLimiter(rules: ApiLimiterEntry, storage: ApiLimiterStorage) {
    return async function(req: Request, res: Response, next: NextFunction) {
        const endpoint = getBaseEndpoint(req.path);
        const method = req.method;
        const clientId = `${method}/${endpoint}/${req.ip}`;
        if (!rules[endpoint] || !rules[endpoint][method]) {
            return next();
        }
        const currentEntries = await storage.increaseEntry(clientId);
        if (currentEntries > rules[endpoint][req.method].limit) {
            return next(new TooManyRequests("Api Limit Reached"));
        }
        await storage.setExpiration(clientId, rules[endpoint][method].time);
        return next();
    }
}
