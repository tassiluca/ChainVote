FROM node:20-alpine

WORKDIR /usr/src/app
COPY package.json ./
COPY tsconfig.json ./
COPY src ./src
COPY secrets ./secrets

RUN ls -al
RUN npm install --legacy-peer-deps --verbose --registry http://host.docker.internal:4873/
RUN npm run build


FROM node:20-alpine

WORKDIR /usr/src/app
COPY package.json ./
RUN npm install --legacy-peer-deps --verbose --only=production --registry http://host.docker.internal:4873/
COPY --from=0 /usr/src/app .
RUN npm install pm2 -g --registry http://host.docker.internal:4873/
EXPOSE 8180

CMD ["pm2-runtime","dist/app.js"]