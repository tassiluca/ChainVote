import nodemailer from 'nodemailer';
import fs from "fs";

const path = "/run/secrets/google_api_secret";
let secretValue = "";

fs.readFile(path, 'utf8', (err, data) => {
    if (err) {
        console.error(`Error reading secret file: ${err.message}`);
        process.exit(1);
    }
    secretValue = data.trim();
});

const config = {
    service: 'gmail',
    auth: {
        user: process.env.MAIL_USER,
        pass: secretValue
    }
}

export default nodemailer.createTransport(config);
