import express, { Application } from "express"
import userRouter from "../routes/userRoute";
import bodyParser from "body-parser"
import { defaultHandler } from "../errors/error.handler";

const ExpressConfig = (): Application => {
  const app = express();

  // Express configurations 
  app.use(express.json());
  app.use(bodyParser.json());
  
  // Routes initialization
  app.use("/users", userRouter);
  app.get("/error", (req, res, next) => {
    throw new Error("test"); 
  })
  app.use(defaultHandler);
  return app
}

export default ExpressConfig