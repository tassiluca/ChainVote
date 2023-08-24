FROM node:20-alpine

WORKDIR /usr/src/app
COPY . .

RUN npm install
RUN npm run prepare

EXPOSE 8180

CMD [ "npm", "start" ]