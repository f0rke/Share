const functions = require('firebase-functions');

// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

exports.channelChange = functions.database
    .ref('/items')
    .onWrite(event => {
        var value = event.data.val()
        console.log(value)
        return
    });

exports.helloLisa = functions.https.onRequest((request, response) => {
    var todayte = new Date();
    var minutes = todayte.getMinutes();
    var hours = todayte.getHours();
    if (minutes < 10) {
        minutes = '0' + minutes;
    }
    hours = (hours + 2) % 24
    if (hours < 10) {
        hours = '0' + hours;
    }
    todayte = hours + ":" + minutes;
    response.send("Hallo Lisa. Es ist " + todayte + " Uhr");
});
