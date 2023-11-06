import ExpressConfig from "./configs/express.config.js"
import {JwtHandler} from "core-components";
import {resolve} from "path";

JwtHandler.config({
    ATPublicKeyPath: resolve("./secrets/at_public.pem"),
    RTPublicKeyPath: resolve("./secrets/rt_public.pem")
});

const app = ExpressConfig();
export default app;
