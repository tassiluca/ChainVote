import swaggerAutogen from 'swagger-autogen';

const doc = {
    info: {
        title: 'Chainvote API',
        description: 'Api documentation for the Chainvote project',
    },
    host: 'localhost:3000'
};

const outputFile = './swagger-output.json';
const routes = [
    './src/routes/codes.route.ts',
    './src/routes/election.info.route.ts',
    './src/routes/election.route.ts',
    './src/routes/user.route.ts',
];

swaggerAutogen()(outputFile, routes, doc);