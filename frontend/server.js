const express = require('express');
const app = express();
const port = 3000;
const apiRoutes = require("./app/routes/api");
const bodyParser = require('body-parser');

const axios = require('axios');
const SERVER_URL = 'http://localhost:3000';

app.use(express.json());
app.use(bodyParser.json());

app.use("/", apiRoutes);
app.set('view engine', 'ejs');
app.set('views', __dirname + '/app/public/views');
app.use(express.static('app/public'));

app.get('/', (req, res) => res.render('index'))
    .get('/sign-up', (req, res) => res.render('sign-up'))
    .get('/sign-in', (req, res) => res.render('sign-in'))

app.use((req, res) => res.render('not-found'))

app.listen(port, () => {
    console.log(`App listening on port ${port}!`);
});
