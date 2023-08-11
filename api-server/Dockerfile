FROM node:20-alpine

WORKDIR /usr/src/app
COPY . .

RUN npm install
RUN npm run prepare

ENV GRPC_TRACE=all
ENV GRPC_VERBOSITY=DEBUG

EXPOSE 8080

CMD [ "npm", "start" ]