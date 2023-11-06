import swaggerJSDoc from 'swagger-jsdoc';
import fs from 'fs';

const options = {
    definition: {
        openapi: '3.0.0',
        info: {
            title: 'Chainvote Auth API documentation',
            description: 'This is the API documentation for the Chainvote Authentication API',
            version: '1.0.0',
        },
        servers: [
            {
                url: 'http://localhost:8180',
                description: 'Local server',
            },
        ],
    },
    apis: ['./src/routes/*.ts'],
};

const openapiSpecification = swaggerJSDoc(options);
fs.writeFileSync('swagger.json', JSON.stringify(openapiSpecification, null, 2));

export default openapiSpecification;
