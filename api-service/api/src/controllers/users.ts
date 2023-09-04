import { Request, Response, NextFunction } from "express";
import { StatusCodes } from "http-status-codes";
import { BadRequestError, NotFoundError, UnauthorizedError, User } from "core-components";
import { ac } from "../configs/accesscontrol.config";

async function setWorkData(req: Request, res: Response, next:NextFunction, isAllowed:boolean) {
    let user = res.locals.user;
    const email = req.params.email;
    
    if(email && email !== user.email) {
        try {
            if(!isAllowed) {
                throw new UnauthorizedError("Can't access to the resource");
            }
            user = await User.findOne({email: email});
            if(user == undefined || user == null) {
                throw new NotFoundError("Can't find the user");
            }
        } catch(error) {
            throw error; 
        }
    }
    
    return user;
}

export async function createUser(req: Request, res: Response, next: NextFunction) {
    const user = new User({
        email: req.body.email,
        password: req.body.password,
        firstName: req.body.firstName,
        secondName: req.body.secondName,
    });
    try {
        await user.save();
        res.status(StatusCodes.OK).send(user);
    } catch(error) {
        return next(error);
    }
}

export async function getProfile(req: Request, res: Response, next: NextFunction) {
    let user;
    const isAllowed = ac.can(res.locals.user.role).readAny('users').granted;
    try {
        user = await setWorkData(req, res, next, isAllowed);
        return res.status(StatusCodes.OK).send({
            email: user.email,
            firstName: user.firstName,
            secondName: user.secondName
        });
    } catch (error) {
        return next(error);
    }
}

export async function editProfile(req: Request, res: Response, next: NextFunction) {
    let user;
    const isAllowed = ac.can(res.locals.user.role).updateAny('users').granted;
    try {
        user = await setWorkData(req, res, next, isAllowed);
    } catch (error) {
        return next(error);
    }

    const data = req.body.data;
    if(data === null || data === undefined) {
        return next(new BadRequestError("Request need to have a \"data\" field"));
    }

    try {
        await User.updateOne({ email: user.email }, data);
        return res.status(StatusCodes.OK).send({ message: "User updated successfully" });
    } catch(error) {
        return next(error);
    }
}

export async function deleteProfile(req: Request, res: Response, next: NextFunction) {
    let user;
    const isAllowed = ac.can(res.locals.user.role).deleteAny('users').granted;
    try {
        user = await setWorkData(req, res, next, isAllowed);
        await User.deleteOne({ email: user.email });
        return res.status(StatusCodes.OK).send({ 
            message: "User deleted successfully" 
        });
    } catch (error) {
        return next(error);
    }
}
