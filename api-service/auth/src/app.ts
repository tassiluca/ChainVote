import ExpressConfig from "./configs/express.config";

const app = ExpressConfig();
const PORT = process.env.PORT || 8180;

app.listen(PORT, () => console.log("Server Running on Port " + PORT));
export default app;