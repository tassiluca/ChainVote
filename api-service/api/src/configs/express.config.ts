import express, { Application } from "express"
import userRouter from "../routes/user.route";
import electionInfoRouter from "../routes/election.info.route";
import bodyParser from "body-parser"
import {defaultErrorHandler} from "core-components";
import MongooseConfig from "./mongoose.config";


const ExpressConfig = (): Application => {
  MongooseConfig();

  const app = express();

  // Express configurations 
  app.use(express.json());
  app.use(bodyParser.json());
  
  // Routes initialization
  app.use("/users", userRouter);
  app.use("/election/info", electionInfoRouter);

  // Use custom error handler.
  app.use(defaultErrorHandler);
  return app
}

export default ExpressConfig