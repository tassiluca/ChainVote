import { Request, Response, NextFunction } from "express";
import {MongoServerError} from "mongodb"
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
        if(error instanceof MongoServerError) {
            res.status(405).send(error.errmsg);
        } else {
            res.status(500).send(error);      
        }  
    }
}


