// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);




//#########################
//######### Users #########
//#########################

exports.copyUserToDatabase = functions.auth.user().onCreate(event => {
    const user = event.data; // The Firebase user.
    const uid = user.uid;
    const mail = user.email.toString(); // The email of the user.
    var displayName = user.displayName; // The display name of the user.

    if (!displayName) {
        displayName = getDisplayNameFromMail(mail)
    }

    console.log("uid: " + uid + " - mail: " + mail + " - displayName: \"" + displayName + "\"")

    let authPromise = admin.auth().updateUser(uid, {
        displayName: displayName
    }).then(userRecord => {
        console.log("Successfully altered user: \"" + userRecord.displayName + "\"")
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
    let dbPromise = admin.database()
        .ref('/users')
        .once('value', snap => {
            if (snap.hasChild(key)) {
                return markUserAsDeleted(key, false)
            } else {
                return admin.database()
                    .ref('/users/' + key)
                    .set(dbUser)
                    .then(snapshot => {
                        console.log("Successfully created user \"" + displayName + "\"")
                    }).catch(error => {
                        console.log(error + " - Could not create user\"" + displayName + "\"")
                    })
            }
        })

    return Promise.all([authPromise, dbPromise]).then(snap => {
        console.log("everything done")
    }).catch(error => {
        console.log(error)
    })
})

exports.markUserAsDeleted = functions.auth.user().onDelete(event => {
    let user = event.data
    let key = getIdFromMail(user.email.toString())
    if (key) {
        markUserAsDeleted(key, true)
    }
})

//########################
//######## Items #########
//########################

exports.notifyMembersOfNewPosts = functions.database
    .ref('/items/{channelId}/{itemId}')
    .onWrite(event => {
        let channelId = event.params.channelId
        let itemId = event.params.itemId
        let item = event.data.val()

        let newItem = !event.data.previous.exists()
        let itemChanged = !newItem && event.data.changed()

        var title
        if (newItem == true) {
            title = "New entry"
        } else {
            if (itemChanged) {
                title = "ItemChanged"
            } else {
                title = channelId
            }
        }

        var msg = item.name
        if (item.description) {
            msg = (msg ? msg : item.key) + " - " + item.description
        }

        console.log("Item '" + itemId + "' written to '" + channelId + "'")
        return loadUsersFromChannel(channelId)
            .then(ids => {
                console.log("Received user IDs: " + ids)
                let promises = []
                for (var i = 0; i < ids.length; i++) {
                    promises.push(getUser(ids[i]))
                }
                return Promise.all(promises)
                    .then(results => {

                        let tokens = []
                        for (var i = 0; i < results.length; i++) {
                            const user = results[i]
                            const pushToken = user.currentPushToken;
                            if (pushToken) {
                                tokens.push(pushToken)
                            }
                        }
                        if (tokens.length > 0) {
                            console.log("Trying to send notification with title '" + title + "' and body '" + msg + "'")
                            let payload = {
                                notification: {
                                    title: title,
                                    body: msg ? msg : "some body",
                                    sound: 'default',
                                    badge: '1'
                                }
                            };
                            return admin.messaging().sendToDevice(tokens, payload);
                        }
                    }).catch(err => {
                        console.log(err)
                    })
            })
    })

function loadUsersFromChannel(channelId) {
    let ref = admin.database()
        .ref('/members/' + channelId)
    let promise = new Promise((resolve, reject) => {
        ref.once('value', snap => {
            let data = snap.val()
            let users = []
            for (var entry in data) {
                users.push(entry)
            }
            resolve(users)
        }, err => {
            reject(err)
        })
    })
    return promise
}

function getUser(id) {
    console.log("Fetching user " + id)
    let ref = admin.database().ref('/users/' + id)
    let promise = new Promise((resolve, reject) => {
        ref.once('value', snap => {
            let user = snap.val()
            console.log("Fetched user: " + user.firstName + " " + user.lastName)
            resolve(user)
        }, err => {
            reject(err)
        })
    })
    return promise
}

//###########################
//######## Channels #########
//###########################

exports.handleChannelsWrite = functions.database
    .ref('/channels/{id}')
    .onWrite(event => {
        var value = event.data.val()
        if (value.key) {
            var channelId = value.key
            var parent = event.data.ref.parent
            //Check if this is a new channel with poperly setup properties
            let channelCreated = !event.data.previous.exists()
            let channelStateChanged = !channelCreated && event.data.changed()
            if (channelCreated && !value.members && !value.lastEntry) {
                console.log(
                    'Removing ' + channelId + ' due to no members and no items'
                )
                return parent.child(channelId).remove()
            } else if (!channelStateChanged) {
                console.log('New channel ' + channelId + ' validated and successfully created')
                console.log('TODO: inform users')
            }
        }
    });

exports.handleUserAdd = functions.database
    .ref('/channels/{channelId}/members/{userId}')
    .onWrite(event => {
        var channelId = event.params.channelId
        var userId = event.params.userId
        var isNew = !event.data.previous.exists()
        var wasDeleted = !event.data.exists()
        if (isNew) {
            var userUpdate = admin.database()
                .ref('/users/' + userId + '/channels/' + channelId)
                .set(true)
            var membersUpdate = admin.database()
                .ref('/members/' + channelId + '/' + userId)
                .set(true)
            return Promise.all([userUpdate, membersUpdate])
        } else if (wasDeleted) {
            var userUpdate = admin.database()
                .ref('/users/' + userId + '/channels/' + channelId)
                .remove()
            var membersUpdate = admin.database()
                .ref('/members/' + channelId + '/' + userId)
                .remove()
            return Promise.all([userUpdate, membersUpdate])
        }
    })

//##########################
//######## Helpers #########
//##########################

function markUserAsDeleted(key, deleted) {
    let ref = admin.database()
        .ref('/users')
    return ref.once('value', snap => {
        if (snap.hasChild(key)) {
            return ref.child(key).update({
                deleted: deleted
            }).then(snap => {
                console.log("Successfully marked user '" + key + "' deleted:" + deleted)
            }).catch(error => {
                console.log(error + " - Could not mark user" + key + " deleted:" + deleted)
            })
        } else {
            console.log("Cannot delete user " + key + " - not existing")
        }
    })
}

function getDisplayNameFromMail(mail) {
    return getFirstNameFromMail(mail)
        + " "
        + getLastNameFromMail(mail)
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

String.prototype.replaceAll = function (search, replacement) {
    var target = this;
    return target.split(search).join(replacement);
}
