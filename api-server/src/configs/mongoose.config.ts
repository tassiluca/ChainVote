import mongoose from "mongoose"

const MONGODB_CONNECTION_STRING = "mongodb://root:password@localhost:27017/?authSource=admin"; 
async function MongooseConfig() {
    try {
        await mongoose.connect(MONGODB_CONNECTION_STRING);
    } catch(error) {
        throw error;
    }
}

export default MongooseConfig;