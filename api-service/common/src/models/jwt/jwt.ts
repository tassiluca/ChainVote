import {HydratedDocument, Model, model, Schema, Types} from "mongoose";
import {User} from "../users/users";
import {isEmail} from "validator";
import {JwtHandler} from "../../utils/jwt/jwt.handler";
import {BadRequestError, UnauthorizedError} from "../../errors/errors";

interface IJsonWebToken {
    _id: Types.ObjectId;
    refreshToken: string;
    accessToken: string;
    email: string;
    createdAt?: Date;
    enabled?: boolean;
}

interface IJsonWebTokenMethods {
    validateRefreshToken(requestEmail:string);
    validateAccessToken(requestEmail:string);
}

interface IJsonWebTokenModel extends Model<IJsonWebToken, {}, IJsonWebTokenMethods> {
    createTokenPair(user, expirations?:{accessToken?: string, refreshToken?:string}): Promise<HydratedDocument<IJsonWebToken, IJsonWebTokenMethods>>;
}

const jwtSchema = new Schema<IJsonWebToken, IJsonWebTokenModel, IJsonWebTokenMethods>({
    refreshToken: {
        type: String,
        required: true,
    },
    accessToken: {
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

jwtSchema.static('createTokenPair', async function createTokenPair(user, expirations:{accessToken?: string, refreshToken?:string} = {}): Promise<HydratedDocument<IJsonWebToken, IJsonWebTokenMethods>> {
    const refreshToken = JwtHandler.getInstance().signRefreshToken(user, expirations.refreshToken);
    const accessToken = JwtHandler.getInstance().signAccessToken(user, expirations.accessToken);
    // Other token are disabled
    await this.updateMany({email: user.email}, {enabled: false});
    return new this({refreshToken: refreshToken, accessToken: accessToken, email: user.email}).save();
});


jwtSchema.method('validateRefreshToken', async function validateRefreshToken(requestEmail:string){
    if(!this.enabled) {
        throw new UnauthorizedError("The token is disabled, please login again");
    }
    const validationResponse:any = JwtHandler.getInstance().verifyRefreshToken(this.refreshToken);
    if(this.email !== requestEmail || validationResponse && this.email !== validationResponse.sub.email) {
        throw new UnauthorizedError("Unauthorized user's email");
    }
    return validationResponse;
});


jwtSchema.method('validateAccessToken', async function validateAccessToken(requestEmail:string){
    if(!this.enabled) {
        throw new UnauthorizedError("The token is disabled, please login again");
    }
    const validationResponse:any = JwtHandler.getInstance().verifyAccessToken(this.accessToken);
    if(this.email !== requestEmail || (validationResponse && this.email !== validationResponse.sub.email)) {
        throw new UnauthorizedError("Unauthorized user's email");
    }
    return validationResponse;
});

const JwtModel = model<IJsonWebToken, IJsonWebTokenModel>('Jwt', jwtSchema);
export {JwtModel as Jwt};






