const express = require('express');
const app = express();
const port = 3000;
const apiRoutes = require("./app/routes/api");
const session = require('express-session');
const store = new session.MemoryStore();
const bodyParser = require('body-parser');

app.use(session({
    secret: 'my-secret-key',
    cookie: { maxAge: 30000 * 60 },
    saveUninitialized: false,
    resave: false,
    store: store
}));

app.use(express.urlencoded({ extended: false }));
app.use(express.json());
app.use(bodyParser.json());

app.use("/", apiRoutes);
app.set('view engine', 'ejs');
app.set('views', __dirname + '/app/public/views');
app.use(express.static('app/public'));

app.use((req, res) => res.render('not-found'));

app.listen(port, () => {
    console.log(`App listening on port ${port}!`);
});
