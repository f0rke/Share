// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.copyUserToDatabase = functions.auth.user().onCreate(event => {
    const user = event.data; // The Firebase user.
    const uid = user.uid;
    const mail = user.email.toString(); // The email of the user.
    var displayName = user.displayName; // The display name of the user.

    if (!displayName) {
        displayName = getDisplayNameFromMail(mail)
    }

    console.log("uid: " + uid + " - mail: " + mail + " - displayName:" + displayName)

    let authPromise = admin.auth().updateUser(uid, {
        displayName: displayName
    }).then(userRecord => {
        console.log("Successfully altered user: " + userRecord.toJSON())
    }).catch(error => {
        console.log("Error " + error + " altering user " + user.toJSON())
    })

    const firstName = getFirstNameFromMail(mail)
    const lastName = getLastNameFromMail(mail)
    const key = getIdFromMail(mail)

    var dbUser = {
        firstName: firstName,
        lastName: lastName,
        key: key,
        email: mail
    }
    let ref = admin.database()
        .ref('/users')
    ref.once('value', snap => {
        var dbPromise
        if (snap.hasChild(key)) {
            dbPromise = markUserAsDeleted(key, false)
        } else {
            dbPromise = admin.database()
                .ref('/users/' + key)
                .set(dbUser).then(snapshot => {
                    console.log("Successfully created user " + displayName)
                }).catch(error => {
                    console.log(error + " - Could not create user" + displayName)
                })
        }

        Promise.all([authPromise, dbPromise]).then(snap => {
            console.log("everything done")
            return
        }).catch(error => {
            console.log(error)
        })
    })
})

exports.someHTTTPSFunction = functions.https.onRequest( (req, resp) =>{
    return resp.send("Hello Max!")
})

exports.markUserAsDeleted = functions.auth.user().onDelete(event => {
    let user = event.data
    let key = getIdFromMail(user.email.toString())
    if (key) {
        markUserAsDeleted(key, true)
    }
})

//Helpers
function markUserAsDeleted(key, deleted) {
    let ref = admin.database()
        .ref('/users')
    ref.once('value', snap => {
        if (snap.hasChild(key)) {
            ref.child(key).update({
                deleted: deleted
            }).then(snap => {
                console.log("Successfully marked user " + key + " deleted:" + deleted)
            }).catch(error => {
                console.log(error + " - Could not mark user" + key + " deleted:" + deleted)
            })
        } else {
            console.log("Cannot delete user " + key + " - not existing")
        }
    })
}

function getDisplayNameFromMail(mail) {
    return getFirstNameFromMail(mail) + " " + getLastNameFromMail(mail)
}

function getIdFromMail(mail) {
    prefix = getMailPrefix(mail);
    prefix = removeUnallowedFirebaseKeyChars(prefix);
    return prefix;
}

function convertToId(candidate) {
    id = null;
    if (candidate != null && !candidate.isEmpty()) {
        id = removeUnallowedFirebaseKeyChars(candidate);
        if (!id.isEmpty()) {
            id = id.toLowerCase();
        } else {
            id = null;
        }
    }
    return id;
}

function getMailPrefix(mail) {
    return mail.split("@")[0];
}

String.prototype.replaceAll = function (search, replacement) {
    var target = this;
    return target.split(search).join(replacement);
}

function removeUnallowedFirebaseKeyChars(prefix) {
    value = prefix.replaceAll("\\.", "");
    value = value.replaceAll("#", "");
    value = value.replaceAll("$", "");
    value = value.replaceAll("\\[", "");
    value = value.replaceAll("]", "");
    return value;
}

function getFirstNameFromMail(mail) {
    prefix = getMailPrefix(mail);
    if (prefix) {
        var arr;
        if (prefix.includes(".")) {
            arr = prefix.split("\\.");
        } else if (prefix.includes("-")) {
            arr = prefix.split("-");
        } else if (prefix.includes("_")) {
            arr = prefix.split("_");
        } else {
            arr = null;
        }
        if (arr && arr.length >= 1) {
            const name = arr[0];
            return capitalizeFirstLetter(name);
        } else {
            return capitalizeFirstLetter(prefix);
        }
    }
    return null;
}

function getLastNameFromMail(mail) {
    const prefix = getMailPrefix(mail);
    if (prefix) {
        var arr;
        if (prefix.includes(".")) {
            arr = prefix.split("\\.");
        } else if (prefix.includes("-")) {
            arr = prefix.split("-");
        } else if (prefix.includes("_")) {
            arr = prefix.split("_");
        } else {
            arr = null;
        }
        if (arr && arr.length >= 2) {
            let name = arr[arr.length - 1];
            return capitalizeFirstLetter(name);
        } else {
            return capitalizeFirstLetter(prefix);
        }
    }
    return null;
}

function capitalizeFirstLetter(name) {
    if (name != null) {
        var arr = name.split('');
        if (arr.length > 0) {
            var firstChar = arr[0].toString();
            firstChar = firstChar.toUpperCase()
            arr[0] = firstChar[0]
            name = arr.join('');
        }
    }
    return name;
}

exports.deleteEmptyChannel = functions.database
    .ref('/channels/{id}')
    .onWrite(event => {
        var value = event.data.val()
        if (value.key) {
            var channelId = value.key
            var parent = event.data.ref.parent
            if (!value.members && !value.lastEntry) {
                console.log(
                    'Removing ' + channelId + ' due to no members and no items'
                )
                return parent.child(channelId).remove()
            } else {
                console.log('New channel ' + channelId + ' validated and successfully created')
            }
        }
        return
    });
