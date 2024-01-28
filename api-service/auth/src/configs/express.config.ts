import express, { Application } from "express"
import bodyParser from "body-parser"
import authRouter from "../routes/auth.route";

import {defaultErrorHandler, defaultResponseHandler, JwtHandler} from "core-components";
import {resolve} from "path";
import MongooseConfig from "./mongoose.config";


const ExpressConfig = (): Application => {
    JwtHandler.config({
        ATPrivateKeyPath: resolve("./secrets/at_private.pem"),
        RTPrivateKeyPath: resolve("./secrets/rt_private.pem")
    });

    MongooseConfig();
    const app = express();

    // Express configurations
    app.use(express.json());
    app.use(bodyParser.json());
    app.use((req, res, next) => {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, PATCH");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        next();
    })

    // Routes setup
    app.use('/auth', authRouter);

    // Use custom response handler.
    app.use(defaultResponseHandler);

    // Error handler
    app.use(defaultErrorHandler);

    return app
}
  
  export default ExpressConfig