var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var bcrypt = require('bcrypt');
var session = require('express-session');
var User = require('./models/index');
var MongoStore = require('connect-mongo')(session);
// create a connection to our MongoDB
//mongoose.connect('mongodb://' + config.db.host + '/' + config.db.name);

mongoose.connect('mongodb://localhost/userd');
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open',function(){
	console.log("Connected");
});
/*
app.get("/", function(req,res) {
	res.send("Helloooo");
}); 

//__dirname + "/test.html" => will redirect to LoginApi/test.html folder and thus will show html form
app.get("/", function(req, res) {
  res.sendFile(__dirname + "/test.html");
});
*/
//use sessions for tracking logins
app.use(session({
  secret: 'Kitty',
  resave: true,
  saveUninitialized: false,
  store: new MongoStore({
    mongooseConnection: db
  })
}));

// handles JSON bodies
app.use(bodyParser.json());
// handles URL encoded bodies
app.use(bodyParser.urlencoded({ extended: true }));


app.post('/register', function(req,res){
	User.findOne({eid:req.body.eid},function(err,user){
		if(err)
		{
			console.log(err);
			res.setHeader('Content-Type', 'application/json');
    		res.send(JSON.stringify({val:"err1"}));
		}
		if(user)
		{
			//emailId already registered.
			res.setHeader('Content-Type', 'application/json');
    		res.send(JSON.stringify({val:"eidpresent"}));
		}
		else if(req.body.name && req.body.username && req.body.eid && req.body.mob && req.body.pwd)
		{
			var UserData = {
				name : req.body.name,
				username : req.body.username,
				eid : req.body.eid,
				mob : req.body.mob,
				pwd : req.body.pwd,
			}
			User.create(UserData,function(err, doc) {
				if(err){
					console.log(err);
					res.setHeader('Content-Type', 'application/json');
    				res.send(JSON.stringify({val:"err1"}));
				}
				else{
					console.log("Item saved to database "+doc._id);
					res.setHeader('Content-Type', 'application/json');
	    			res.send(JSON.stringify({val:"Saved"}));
				}
			});
		}
		else
		{
			res.setHeader('Content-Type', 'application/json');
	    	res.send(JSON.stringify({val:"empty"}));	
		}
	});

	
});
app.post('/login',function(req,res){
	if(req.body.username && req.body.pwd)
	{
		User.findOne({username:req.body.username}, function(err,user){
			if(err)
			{
				console.log(err);
			}
			else if(!user)
			{
				//console.log("User not found");
				res.setHeader('Content-Type', 'application/json');
    			res.send(JSON.stringify({val:"notpresent"}));
			}
			if(user)
			{
			    bcrypt.compare(req.body.pwd, user.pwd, function(err, doc) {
			    	if(err){
						res.setHeader('Content-Type', 'application/json');
    					res.send(JSON.stringify({val:"err"}));			    	
    				}
    				if(doc) {
    					// Passwords match
    					res.setHeader('Content-Type', 'application/json');
    					res.send(JSON.stringify({val:"gotit",name:user.name}));
    				}
					else {
						// Passwords match
						name = user.name;
						eid = user.eid;
						//console.log(name);
						res.setHeader('Content-Type', 'application/json');
    					res.send(JSON.stringify({val:"wpwd"}));
						//req.session.userId = user._id;
						//res.redirect('/profile');
						// will start session.
					}
					
				});
			}

		});
	}
	else
	{
		res.setHeader('Content-Type', 'application/json');
    	res.send(JSON.stringify({val:"empty"}));	
	}
});
// GET route after registering
app.get('/profile', function (req, res, next) {
  User.findById(req.session.userId)
    .exec(function (error, user) {
      if (error) {
        return next(error);
      } else {
        if (user === null) {
          var err = new Error('Not authorized! Go back!');
          err.status = 400;
          return next(err);
        } else {
          return res.send('<h1>Name: </h1>' + user.name + '<h2>Mail: </h2>' + user.eid + '<br><a type="button" href="/logout">Logout</a>')
        }
      }
    });
});

// GET for logout logout
app.get('/logout', function (req, res, next) {
  if (req.session) {
    // delete session object
    req.session.destroy(function (err) {
      if (err) {
        return next(err);
      } else {
        return res.redirect('/');
      }
    });
  }
});
app.listen(8000,function(){
	console.log("Server is running !");
});