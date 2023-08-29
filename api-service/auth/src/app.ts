import ExpressConfig from "./configs/express.config";
import MongooseConfig from "./configs/mongoose.config";
import { initJWTSystem } from "core-components"
import { resolve } from "path";  

initJWTSystem({
    ATPrivateKeyPath: resolve("./secrets/at_private.pem"),
    RTPrivateKeyPath: resolve("./secrets/rt_private.pem")
});

MongooseConfig();
const app = ExpressConfig();
const PORT = process.env.PORT || 8180;


app.listen(PORT, () => console.log("Server Running on Port " + PORT));