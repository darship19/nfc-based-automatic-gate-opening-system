const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

exports.sendNotification = functions.database.ref('/').onUpdate(async(change,context) => {
    const db = admin.database();
    const tokensRef = db.ref('device_tokens');
    const afterData = change.after.val();
    const beforeData = change.before.val();

    if (beforeData.Request !== "Yes" && afterData.Request == "Yes"){
        try {
            // Get all device tokens
            const snapshot = await tokensRef.once('value');
            const tokens = snapshot.val();
            
            if (!tokens) {
                console.log('No tokens found');
                return null;
            }

            const tokenList = Object.keys(tokens);
            const message = {
                notification: {
                    title: 'Request',
                    body: 'Check the request'
                },
                tokens: tokenList
            };

            // Send notification
            const response = await admin.messaging().sendMulticast(message);
            console.log('Successfully sent messages:', response);
            return null;
        } catch (error) {
            console.error('Error sending notification:', error);
            return null;
        }
    }
});
