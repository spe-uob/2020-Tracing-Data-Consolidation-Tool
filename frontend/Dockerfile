FROM node:lts

# TODO turn into multi-stage dockerfile to save space/memory
RUN npm install -g serve

WORKDIR /usr/app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

EXPOSE 5000

CMD [ "serve", "-s", "build" ]
