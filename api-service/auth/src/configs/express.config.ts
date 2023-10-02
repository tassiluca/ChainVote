import express, { Application } from "express"
import bodyParser from "body-parser"
import authRouter from "../routes/authRoute";

import {defaultErrorHandler, JwtHandler} from "core-components";
import {resolve} from "path";
import MongooseConfig from "./mongoose.config";


const ExpressConfig = (): Application => {
    JwtHandler.config({
        ATPrivateKeyPath: resolve("./secrets/at_private.pem"),
        RTPrivateKeyPath: resolve("./secrets/rt_private.pem"),
        RTPublicKeyPath: resolve("./secrets/rt_public.pem")
    });

    MongooseConfig();
    const app = express();

    // Express configurations
    app.use(express.json());
    app.use(bodyParser.json());


    // Routes setup
    app.use('/auth', authRouter);

    // Error handler
    app.use(defaultErrorHandler);

    return app
}
  
  export default ExpressConfig