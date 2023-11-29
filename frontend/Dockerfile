
FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install

ENV API_URL="http://api-server:8080"

COPY . .
EXPOSE 3000
CMD [ "npm", "start" ]
