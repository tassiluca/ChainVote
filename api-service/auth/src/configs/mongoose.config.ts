import mongoose from "mongoose"

const connectionString = process.env.MONGODB_CONNECTION_STRING as string || 
"mongodb://user:pass@localhost:27017/?authMechanism=DEFAULT"
async function MongooseConfig() {
    try {
        await mongoose.connect(connectionString);
    } catch(error) {
        throw error;
    }
}

export default MongooseConfig;