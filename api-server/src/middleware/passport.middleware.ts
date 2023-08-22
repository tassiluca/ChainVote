import { Strategy as LocalStrategy } from 'passport-local';
import { User } from '../models/users';
import { NotFoundError, UnauthorizedError } from '../errors/errors';

export default (passport: any) => {
    passport.use(
        new LocalStrategy(async (username, password, next) => {
          try {
            const user = await User.findOne({ username: username });
    
            if (!user) {
              let error = new NotFoundError("User not found", undefined);
              return next(error, false);
            }

            let passwordMatch: boolean = await user.comparePassword(password)
            if (!passwordMatch) {
              let error = new UnauthorizedError("Invalid credentials", undefined);
              return next(error, false);
            }
            
            return next(null, user);
          } catch (error) {
            return next(error);
          }
        })
    );
}