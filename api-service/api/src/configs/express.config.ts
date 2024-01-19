import express, { Application } from "express"
import bodyParser from "body-parser"
import {defaultErrorHandler, defaultResponseHandler, JwtHandler} from "core-components";
import MongooseConfig from "./mongoose.config";

import userRouter from "../routes/user.route";
import electionInfoRouter from "../routes/election.info.route";
import electionRouter from "../routes/election.route";
import codesRoute from "../routes/codes.route";
import {resolve} from "path";

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

  // Routes initialization
  app.use("/users", userRouter);
  app.use("/election", electionRouter);
  app.use("/election/info", electionInfoRouter);
  app.use("/code", codesRoute);

  // Use custom response handler.
  app.use(defaultResponseHandler);

  // Use custom error handler.
  app.use(defaultErrorHandler);

  return app
}

export default ExpressConfig