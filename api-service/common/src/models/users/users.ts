import mongoose, {Model, Schema, Types} from "mongoose"
import {MongoError} from "mongodb"
import bcrypt from "bcrypt"
import {isEmail, isStrongPassword, isAlpha} from "validator";
import { BadRequestError } from "../../errors/errors"

const SALT_WORK_FACTOR: number = 10

export enum Roles {
  ADMIN="admin",
  USER="user"
}

interface IJwtToken {
    _id: Types.ObjectId;
    token: string,
    createdAt: Date,
    updatedAt: Date
}
interface IUser {
    email: string,
    password: string,
    firstName: string,
    secondName: string,
    role: Roles,
    tokens: IJwtToken[]
}

type UserDocumentProps = {
    tokens: Types.DocumentArray<IJwtToken>;
};

interface UserDocumentMethods {
    comparePassword: (password: string, next: (err: Error | undefined, same: any) => any) => Promise<boolean>;
}

type UserDocumentType = Model<IUser, {}, UserDocumentProps, UserDocumentMethods>

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
    },
    tokens: {
        type: [new Schema<IJwtToken>({
            token: {
                type: String,
                required: true,
                index: { unique: true}
            }
        },{ timestamps: true })]
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

