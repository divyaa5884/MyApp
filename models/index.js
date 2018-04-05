var mongoose = require('mongoose');
var bcrypt = require('bcrypt');
var UserSchema = new mongoose.Schema({
	name: {
		type:String,
		required:true,
		trim:true,
	},
	username: {
		type:String,
		required:true,
		trim: true
	},
	eid : {
		type:String,
		required:true,
		trim:true,
		unique:true
	},
	mob: {
		type:Number,
		trim:true,
		required:true
	},
	pwd: {
		type: String,
		required:true
	}
});

//The pre('save', callback) middleware executes the callback function every time before an entry is saved to the collection.
// only one parameter in function i.e. here we are using serial middleware function.
UserSchema.pre('save', function(next) {
	var user = this;
	bcrypt.hash(user.pwd, 10, function(err, hash) {
      // Store hash in database
      if(err){
      	return next(err);
      }
      user.pwd = hash;
      next();
    });
});

var User = mongoose.model('User',UserSchema);
module.exports = User;
