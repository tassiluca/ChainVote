import ExpressConfig from "./configs/express.config";
import MongooseConfig from "./configs/mongoose.config";

MongooseConfig();
const app = ExpressConfig();
const PORT = process.env.PORT || 8180;


app.listen(PORT, () => console.log("Server Running on Port " + PORT));