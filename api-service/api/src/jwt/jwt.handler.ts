import { readFileSync } from "fs";
import * as path from "path";
import jwt from 'jsonwebtoken';

export function verifyAccessToken(token: string) {
    const publicKey: string = readFileSync(path.resolve(__dirname, '../../secrets/at_public.pem'), 'utf8');
    return verifyJwt(token, publicKey)
}

export function verifyRefreshToken(token: string) {
    const publicKey: string = readFileSync(path.resolve(__dirname, '../../secrets/rt_public.pem'), 'utf8');
    return verifyJwt(token, publicKey)
}

export function verifyJwt<T>(token: string, pKey: string): T | null {
    return jwt.verify(token, pKey) as T;
}
