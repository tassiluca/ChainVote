import mongoose, {Model, Schema, Types, UpdateQuery} from "mongoose"
import {MongoError} from "mongodb"
import bcrypt from "bcrypt"
import {isEmail, isStrongPassword, isAlpha} from "validator";
import { BadRequestError } from "../../errors/errors"

const SALT_WORK_FACTOR: number = 10

export enum Roles {
  ADMIN="admin",
  USER="user"
}

export interface IUser {
    email: string,
    password: string,
    firstName: string,
    secondName: string,
    role: Roles,
}

interface UserDocumentProps extends IUser, Document {
    comparePassword: (password: string, next: (err: Error | undefined, same: any) => any) => Promise<boolean>;
}

type UserDocumentType = Model<IUser, {}, UserDocumentProps>

let User = new Schema<IUser, UserDocumentType>({
    email: {
        type: String,
        required: true, 
        index: {
            unique: true
        },
        validate: isEmail
    },
    password: { 
        type: String, 
        required: true,
        validate: isStrongPassword
    },
    firstName: {
        type: String,
        validate: isAlpha
    },
    secondName: {
        type: String,
        validate: isAlpha
    },
    role: {
      type: String,
      enum: Roles,
      default: Roles.USER
    }
});


User.pre("save", function (next) {
    const user = this;
    if (user.isModified("password") || user.isNew) {
      bcrypt.genSalt(SALT_WORK_FACTOR, function (error, salt) {
        if (error) {
          return next(error)
        } else {

          bcrypt.hash(user.password, salt, function(error, hash) {
            if (error) {
              return next(error)
            }

            user.password = hash
            next()
          })
        }
      })
    } else {
      return next()
    }
});


User.pre("updateOne", function(next) {
    const update: any = {...this.getUpdate()};
    const context = this;
    if (update.password) {
        bcrypt.genSalt(SALT_WORK_FACTOR, function (error, salt) {
            if (error) {
                return next(error)
            } else {

                bcrypt.hash(update.password, salt, function(error, hash) {
                    if (error) {
                        return next(error)
                    }

                    update.password = hash
                    context.setUpdate(update);
                    next()
                })
            }
        })
    } else {
        next();
    }
});

User.post('save', function(error: Error, doc: Document, next) {
  if(error && error instanceof MongoError) {
    if (error.code === 11000) {
      return next(new BadRequestError('The entered email is already present'));
    }
    next(error);
  }

  next();
});

User.methods.comparePassword = function (password: string | Buffer, next: (err: Error | undefined, same: any) => any) {
  bcrypt.compare(password, this.password, function(error, isMatch) {
      if (error) {
          return next(error, undefined);
      }
      return next(undefined, isMatch);
  });
};

const UserModel = mongoose.model<IUser, UserDocumentType>('Users', User);

export {UserModel as User}

