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
    ATPublicKeyPath: resolve("./secrets/at_public.pem"),
    RTPublicKeyPath: resolve("./secrets/rt_public.pem")
  });

  MongooseConfig();

  const app = express();

  // Express configurations 
  app.use(express.json());
  app.use(bodyParser.json());

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