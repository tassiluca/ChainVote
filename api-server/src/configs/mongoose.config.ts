import mongoose from "mongoose"

const connectionString = process.env.MONGODB_CONNECTION_STRING as string
async function MongooseConfig() {
    try {
        await mongoose.connect(connectionString);
    } catch(error) {
        throw error;
    }
}

export default MongooseConfig;