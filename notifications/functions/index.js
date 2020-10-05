const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite(event => {

	
  const user_id = event.params.user_id;
  const notification = event.params.notification;
  
  console.log('The user Id is :', user_id);
  
  if (!event.data.val()) {
  
  
  	return console.log('A notification has been deleted from the database :', notification_id);
  }   
  
  
  const fromuser = admin.database().ref(`/notification/${user_id}/${notification_id}`).once('value');
  
  return fromUser.then(fromUserResult =>  {
  
  
  	const from_user_id = fromUserResult.val().from;
  	
  	console.log("You have new notification");
  	
  	const userQuery = admin.database().ref(`users/${from_user_id}/name`).once('value');
  	
  	 const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');
  	 
  	 
  	 return Promise.all([userQuery, deviceToken]).then(result => {
  	 
  	  const userName = result[0].val();
  	  const token_id = result[1].val();
  	  
  	  
  	  	const payload = {
	
		notification: {
		
			title : "New Friend Request",
			body : `${userName} has sent you request`,
			icon : "default", 
			click_action: "com.example.singhkshitiz.letschat_TARGET_NOTIFICATION"
		
		},
		
		data : {
		
			from_user_id : from_user_id
		
		}
	
	};
	
	
	return admin.messaging().sendToDevice(token_id, payload).then(response => {
	
		console.log('This was the notification feature');
	
	
	});
  	 
  	 });
  
  });
  

});

