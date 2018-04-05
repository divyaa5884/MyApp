// to avoid all these we can simply write => mongoose.connect('mongodb://localhost/userd');
var config = {};
config.db = {};
config.webhost = 'http://localhost:8000/';

// your MongoDB host and database name
config.db.host = 'localhost';
config.db.name = 'userd';
module.exports = config;
