import swaggerJSDoc from 'swagger-jsdoc';
import fs from 'fs';

const options = {
    definition: {
        openapi: '3.0.0',
        info: {
            title: 'Chainvote API documentation',
            version: '1.0.0',
        },
    },
    apis: ['./src/routes/*.ts'],
};

const openapiSpecification = swaggerJSDoc(options);
fs.writeFileSync('swagger.json', JSON.stringify(openapiSpecification, null, 2));

export default openapiSpecification;
