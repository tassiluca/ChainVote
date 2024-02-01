
import mailer from "../configs/mailer.config";


// other parts of the code

const message = {
    from: 'ChainVote',
    to: emailToSend,
    subject: 'Title of the email',
    html: `
       Some HTML content to append to the mail
    `
};

await mailer.sendMail(message).catch((error: any) => next(error));