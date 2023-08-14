import express, { Application } from "express"
import userRouter from "../routes/userRoute";
import bodyParser from "body-parser"

const ExpressConfig = (): Application => {
  const app = express();

  // Express configurations 
  app.use(express.json());
  app.use(bodyParser.json());
  
  // Routes initialization
  app.use("/users", userRouter);
  
  return app
}

export default ExpressConfig