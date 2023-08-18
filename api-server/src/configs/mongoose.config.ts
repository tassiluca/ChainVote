import mongoose from "mongoose"

const MONGODB_CONNECTION_STRING = "mongodb://user:pass@mongodb:27017/?authMechanism=DEFAULT"; 
async function MongooseConfig() {
    try {
        await mongoose.connect(MONGODB_CONNECTION_STRING);
    } catch(error) {
        throw error;
    }
}

export default MongooseConfig;