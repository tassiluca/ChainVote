import swaggerJSDoc from 'swagger-jsdoc';
import fs from 'fs';

const options = {
    definition: {
        openapi: '3.0.0',
        info: {
            title: 'Chainvote API documentation',
            description: 'This is the API documentation for the Chainvote API',
            version: '1.0.0',
        },
        servers: [
            {
                url: 'http://localhost:8080',
                description: 'Local server',
            },
        ],
        components: {
            schemas: {
                CommonResponse: {
                    type: 'object',
                    properties: {
                        success: {
                            type: 'boolean',
                            description: 'True if the request was successful',
                            example: true,
                        },
                        date: {
                            type: 'string',
                            description: 'The date of the request',
                            example: '2021-06-01T16:00:00.000Z',
                        },
                        data: {
                            type: 'object',
                            description: 'The data returned by the request',
                        },
                        code: {
                            type: 'integer',
                            description: 'The HTTP status code of the request',
                            example: 200,
                        },
                    },
                },
                TooManyRequestError: {
                    type: 'object',
                    properties: {
                        success: {
                            type: 'boolean',
                            value: false,
                        },
                        date: {
                            type: 'string',
                            description: 'The date of the request',
                            example: '2021-06-01T16:00:00.000Z',
                        },
                        error: {
                            type: 'object',
                            description: 'The error returned by the request',
                            properties: {
                                name: {
                                    type: 'string',
                                    value: 'Too Many Requests',
                                },
                                description: {
                                    type: 'string',
                                    value: 'Api limit reached',
                                },
                                type: {
                                    type: 'string',
                                    value: 7,
                                },
                                code: {
                                    type: 'integer',
                                    value: 409,
                                },
                            },
                        },
                    },
                },
                BadRequestError: {
                    type: 'object',
                    properties: {
                        success: {
                            type: 'boolean',
                            value: false,
                        },
                        date: {
                            type: 'string',
                            description: 'The date of the request',
                            example: '2021-06-01T16:00:00.000Z',
                        },
                        error: {
                            type: 'object',
                            description: 'The error returned by the request',
                            properties: {
                                name: {
                                    type: 'string',
                                    value: 'Bad Request',
                                },
                                description: {
                                    type: 'string',
                                    example: 'Some validation errors occurred on the request.',
                                },
                                type: {
                                    type: 'string',
                                    value: 4,
                                },
                                code: {
                                    type: 'integer',
                                    value: 400,
                                },
                            },
                        },
                    },
                },
                InternalServerError: {
                    type: 'object',
                    properties: {
                        success: {
                            type: 'boolean',
                            value: false,
                        },
                        date: {
                            type: 'string',
                            description: 'The date of the request',
                            example: '2021-06-01T16:00:00.000Z',
                        },
                        error: {
                            type: 'object',
                            description: 'The error returned by the request',
                            properties: {
                                name: {
                                    type: 'string',
                                    value: 'Internal server error',
                                },
                                description: {
                                    type: 'string',
                                    example: 'Error while processing the request',
                                },
                                type: {
                                    type: 'string',
                                    value: 7,
                                },
                                code: {
                                    type: 'integer',
                                    value: 500,
                                },
                            },
                        },
                    },
                },
            },
        },
    },
    apis: ['./src/routes/*.ts'],
};

const openapiSpecification = swaggerJSDoc(options);
fs.writeFileSync('swagger.json', JSON.stringify(openapiSpecification, null, 2));

export default openapiSpecification;
