var express = require('express'),
    bodyParser = require('body-parser'),
    oAuth = require('./mock_server/oauth');

var app = express();
app.locals.title = 'Mock Admin Site';
app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({extended: true})); // for parsing application/x-www-form-urlencoded

app.use('/admin', express.static(__dirname + '/src'));
app.use('/vendor', express.static(__dirname + '/vendor'));
app.use('/i18n', express.static(__dirname + '/i18n'));

app.post('/api/oauth/admin', oAuth.admin);

app.listen(3000);