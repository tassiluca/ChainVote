const express = require('express');
const app = express();
const port = 3000;
const apiRoutes = require("./app/routes/api");

app.use(express.json());
app.use("/api", apiRoutes);
app.use(express.static('app/public'));

app.get('/', (req, res) => { 
    res.sendFile(__dirname + '/app/public/views/index.html');
});

app.use(express.static('app/public/views'));

app.listen(port, () => {
    console.log(`App listening on port ${port}!`);
});
