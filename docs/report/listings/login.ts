export async function login(req: Request, res: Response, next: NextFunction) {
    const email = req.body.email;
    const password = req.body.password;
    try {
        const user = await User.findOne({email: email});
        //...
        user.comparePassword(password, async function(error, isMatch) {
            // If password is ok, create token pair and send it to client
            const tokens = await Jwt.createTokenPair(user);
            res.locals.code = StatusCodes.OK;
            res.locals.data = {
                accessToken: tokens.accessToken,
                refreshToken: tokens.refreshToken
            };
            //...
        });
    } catch(error) {
       //...
    }
}
