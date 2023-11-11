const express = require('express');
const app = express();
const port = 3000;
const apiRoutes = require("./app/routes/api");

const axios = require('axios');

app.use(express.json());
app.use("/", apiRoutes);
app.set('view engine', 'ejs');
app.set('views', __dirname + '/app/public/views');
app.use(express.static('app/public'));

app.get('/', (req, res) => res.render('index'))
    .get('/sign-up', (req, res) => res.render('sign-up'))
    .get('/sign-in', (req, res) => res.render('sign-in'))
    .get('/cast-vote/:electionId', async (req, res) => {

        //TODO: get electionId from req.params
        const electionId = req.params.electionId;

        //TODO: return the election info from the API
        const election = await axios.get(`http://localhost:3000/api/elections/${electionId}`);


        //TODO: render the cast-vote view with the election info
    });

app.use((req, res) => res.render('not-found'))

app.listen(port, () => {
    console.log(`App listening on port ${port}!`);
});
