import express, { Application } from "express"
import userRouter from "../routes/userRoute";
import bodyParser from "body-parser"
import defaultErrorHandler from "../middleware/error.middleware";
import { InternalServerError } from "../errors/errors";

const ExpressConfig = (): Application => {
  const app = express();

  // Express configurations 
  app.use(express.json());
  app.use(bodyParser.json());
  
  // Routes initialization
  app.use("/users", userRouter);
  app.get("/error", (req, res, next) => {
    throw new InternalServerError("Prova provona");
  })
  
  // Use custom error handler.
  app.use(defaultErrorHandler);
  return app
}

export default ExpressConfig