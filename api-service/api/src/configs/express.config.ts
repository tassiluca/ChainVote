import express, { Application } from "express"
import userRouter from "../routes/userRoute";
import bodyParser from "body-parser"
import defaultErrorHandler from "../middleware/error.middleware";

const ExpressConfig = (): Application => {
  const app = express();

  // Express configurations 
  app.use(express.json());
  app.use(bodyParser.json());
  
  // Routes initialization
  app.use("/users", userRouter);

  // Use custom error handler.
  app.use(defaultErrorHandler);
  return app
}

export default ExpressConfig