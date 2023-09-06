import {model, Schema, Types} from "mongoose";
import {User} from "../users/users";
import {isEmail} from "validator";

export interface IJsonWebToken {
    _id: Types.ObjectId;
    token: string;
    email: string;
    createdAt: Date;
    enabled?: boolean;
}

const jwtSchema = new Schema<IJsonWebToken>({
    token: {
        type: String,
        required: true,
    },
    email: {
        type: String,
        required: true,
        validate: {
            validator: async function (value) {
                const isValidMail = isEmail(value);
                if(isValidMail) {
                    return await User.exists({email: value});
                }
                return false;
            }
        }
    },
    enabled: {
        type: Boolean,
        default: true
    }
}, {timestamps: { createdAt: true, updatedAt: false }});


const JwtModel = model<IJsonWebToken>('Jwt', jwtSchema);
export {JwtModel as Jwt};






