import { Request, Response, NextFunction } from "express";
import { User } from "../models/users";


export async function createUser(req: Request, res: Response, next: NextFunction) {
    const user = new User({
        email: req.body.email,
        password: req.body.password,
        firstName: req.body.firstName,
        secondName: req.body.secondName,
    });
    try {
        await user.save();
        res.status(200).send(user);
    } catch(error) {
        return next(error);
    }
}


export async function login(req: Request, res: Response, next: NextFunction) {
    // const email = req.body.email;
    // const password = req.body.password;
}


