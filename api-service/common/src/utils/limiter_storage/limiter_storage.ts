export interface ApiLimiterStorage {
    exists(clientId: string): Promise<boolean>;
    increaseEntry(clientId: string): Promise<number>;
    setExpiration(clientId: string, seconds: number): Promise<boolean>;
}