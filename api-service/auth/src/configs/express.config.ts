import express, { Application } from "express"
import bodyParser from "body-parser"
import authRouter from "../routes/authRoute";
import {defaultErrorHandler} from "core-components";

const ExpressConfig = (): Application => {
    const app = express();

    // Express configurations 
    app.use(express.json());
    app.use(bodyParser.json());


    // Routes setup
    app.use(authRouter);
    app.use(defaultErrorHandler);
    return app
}
  
  export default ExpressConfig