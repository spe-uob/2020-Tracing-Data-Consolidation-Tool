FROM node:lts AS builder

WORKDIR /usr/app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build


FROM node:lts

WORKDIR /usr/local/app

COPY --from=builder /usr/app/build ./build

RUN npm install -g serve

EXPOSE 5000

CMD [ "serve", "-s", "build" ]
