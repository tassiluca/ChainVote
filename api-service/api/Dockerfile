FROM node:20-alpine
WORKDIR /usr/src/app
COPY . .
COPY ./script.sh /
RUN chmod 700 /usr/src/app/script.sh
EXPOSE 8180
CMD [ "/usr/src/app/script.sh" ]