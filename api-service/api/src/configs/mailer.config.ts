import nodemailer from 'nodemailer';
import fs from "fs";

const path = "/run/secrets/google_api_secret";
const secretValue = fs.readFileSync(path, 'utf8').trim();

const config = {
    service: 'gmail',
    auth: {
        user: process.env.MAIL_USER,
        pass: secretValue
    }
}

export default nodemailer.createTransport(config);
