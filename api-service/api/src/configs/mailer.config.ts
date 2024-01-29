import nodemailer from 'nodemailer';

let secretValue = "";

secretValue= "vmck idvj ewbi etzk";

const config = {
    service: 'gmail',
    auth: {
        user: "antonioni.giovanni9@gmail.com",
        pass: secretValue
    }
}

export default nodemailer.createTransport(config);
