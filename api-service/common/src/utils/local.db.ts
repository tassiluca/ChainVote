/**
 * This files contains all the util functions for create a local database for 
 * testing all the models logic of the organization
 */
import mongoose, { MongooseOptions, mongo } from "mongoose";
import { MongoMemoryServer } from "mongodb-memory-server";


let mongodb: MongoMemoryServer
/**
 * Estabilish a new connection to the local mongodb server
 */
export async function setupConnection() {
    mongodb = await MongoMemoryServer.create();
    await mongoose.connect(mongodb.getUri());
};  

/**
 * Destroy the connection of the local mongodb server
 */
export async function destroyConnection() {
    if (mongodb) {
        await mongoose.connection.dropDatabase();
        await mongoose.connection.close();
        await mongodb.stop();
    }
}

/**
 * Drop the data from the various collection of the database
 */
export async function dropCollectionsInDb() {
    if(mongodb) {
        const collections = mongoose.connection.collections;
        for (const key in collections) {
            const collection = collections[key];
            await collection.deleteMany();
        }
    }
}