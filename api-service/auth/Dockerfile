FROM node:20-alpine


EXPOSE 8180


WORKDIR /usr/src/app
COPY . .
COPY ./script.sh /
RUN chmod 700 /usr/src/app/script.sh
CMD [ "/usr/src/app/script.sh" ]