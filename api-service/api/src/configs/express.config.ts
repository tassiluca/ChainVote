import express, { Application } from "express"
import userRouter from "../routes/user.route";
import electionRouter from "../routes/election.route";
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
  app.use("/election", electionRouter);

  // Use custom error handler.
  app.use(defaultErrorHandler);
  return app
}

export default ExpressConfig