export function apiLimiter(rules: ApiLimiterEntry, storage: ApiLimiterStorage) {
    return async function(req: Request, res: Response, next: NextFunction) {
        const endpoint = getBaseEndpoint(req.path);
        const method = req.method;
        const clientId = `${method}/${endpoint}/${req.ip}`;

        if(!rules[endpoint] || !rules[endpoint][method]) {
            return next();
        }

        const currentEntries = await storage.increaseEntry(clientId);
        if(currentEntries > rules[endpoint][req.method].limit) {
            return next(new TooManyRequests("Api Limit Reached"));
        }

        await storage.setExpiration(clientId, rules[endpoint][method].time);
        return next();
    }
}
