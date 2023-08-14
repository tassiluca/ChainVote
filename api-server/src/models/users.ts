import mongoose, {Schema} from "mongoose"
import bcrypt from "bcrypt"

const SALT_WORK_FACTOR: number = 10

interface IUser {
    email: string;
    password: string;
    firstName: string;
    secondName: string;
}

let User = new Schema<IUser>({
    email: {
        type: String,
        required: true, 
        index: {
            unique: true
        }
    },
    password: { 
        type: String, 
        required: true 
    },
    firstName: String,
    secondName: String
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

User.method('comparePasswords', function (password: string | Buffer, next: (err: Error | undefined, same: any) => any) {
    bcrypt.compare(password, this.password, function(error, isMatch) {
        if (error) {
            return next(error, undefined);
        }
        next(undefined, isMatch);
    });
});

const UserModel = mongoose.model<IUser>('Users', User);


export {UserModel as User}
