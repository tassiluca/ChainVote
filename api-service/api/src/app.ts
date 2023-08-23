import ExpressConfig from "./configs/express.config.js"
import MongooseConfig from "./configs/mongoose.config.js";

MongooseConfig();

const app = ExpressConfig();
const PORT = process.env.PORT || 8080;


app.listen(PORT, () => console.log("Server Running on Port " + PORT));