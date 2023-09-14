import ExpressConfig from "./configs/express.config";
import MongooseConfig from "./configs/mongoose.config";
import { JwtHandler } from "core-components"
import { resolve } from "path";

JwtHandler.config({
    ATPrivateKeyPath: resolve("./secrets/at_private.pem"),
    RTPrivateKeyPath: resolve("./secrets/rt_private.pem"),
    RTPublicKeyPath: resolve("./secrets/rt_public.pem")
});

MongooseConfig();
const app = ExpressConfig();
const PORT = process.env.PORT || 8180;

app.listen(PORT, () => console.log("Server Running on Port " + PORT));