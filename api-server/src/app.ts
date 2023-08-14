import {Request, Response, NextFunction} from "express";
import ExpressConfig from "./configs/express.config.js"
import MongooseConfig from "./configs/mongoose.config.js";

// Estabilish Mongoose connection
MongooseConfig();

const app = ExpressConfig();
const PORT = process.env.PORT || 8080;



app.get("/", (req: Request, res: Response, next: NextFunction) => { 
    res.json({ name: "Prova" }); 
});




app.listen(PORT, () => console.log("Server Running on Port " + PORT));