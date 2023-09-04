import ExpressConfig from "./configs/express.config.js"
import MongooseConfig from "./configs/mongoose.config.js";
import { initJWTSystem } from "core-components";
import { resolve } from "path"



initJWTSystem({
    ATPublicKeyPath: resolve("./secrets/at_public.pem"),
    RTPublicKeyPath: resolve("./secrets/rt_public.pem")
});

MongooseConfig();
const app = ExpressConfig();
const PORT = process.env.PORT || 8080;

app.listen(PORT, () => console.log("Server Running on Port " + PORT));