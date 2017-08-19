// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const util = require('util')

let ITEMS_NODE = 'items'
let MEMBERS_NODE = 'members'
let USERS_NODE = 'users'
let CHANNELS_NODE = 'channels'

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

    var dbUser = {
        uid: uid,
        email: mail
    }
    if (firstName != null) {
        dbUser.firstName = firstName
    }
    if (lastName != null) {
        dbUser.lastName = lastName
    }
    let dbPromise = admin.database()
        .ref('/' + USERS_NODE)
        .once('value', snap => {
            if (snap.hasChild(uid)) {
                return setUserDeletedField(uid, false)
            } else {
                return admin.database()
                    .ref('/' + USERS_NODE + '/' + uid)
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
    if (user.uid) {
        setUserDeletedField(user.uid, true)
    }
})

//########################
//######## Items #########
//########################

exports.onNewPost = functions.database
    .ref('/' + ITEMS_NODE + '/{channelId}/{itemId}')
    .onWrite(event => {
        let channelId = event.params.channelId
        let itemId = event.params.itemId
        let item = event.data.val()

        console.log("Item '" + itemId + "' written to '" + channelId + "'")

        let newItem = !event.data.previous.exists()
        let itemChanged = !newItem && event.data.changed()
        let itemDeleted = !event.data.exists() && event.data.previous.exists()

        let itemInfo = {
            data: item,
            isNew: newItem,
            changed: itemChanged,
            deleted: itemDeleted,
            channel: channelId
        }

        if (!itemDeleted) {
            let pushPromise = sendPush(itemInfo)
        }
        let updateChannelPromise = updateChannelWithNewItem(itemInfo)

        return Promise.all([pushPromise, updateChannelPromise])
    })

function updateChannelWithNewItem(itemInfo) {

    console.log('updateChannelWithNewItem: ' + util.inspect(itemInfo, { showHidden: false, depth: null }))

    if (itemInfo.isNew === true || itemInfo.changed === true) {
        //Data
        let contributorName = itemInfo.data.creatorFirstName
        let contributor = itemInfo.data.creator
        let date = itemInfo.data.creationDate
        let lastEntry = itemInfo.data.name
        let channel = itemInfo.channel

        //Promises
        console.log('Setting /' + CHANNELS_NODE + '/' + channel + '/lastContributor = ' + contributor)
        let updateContributor = admin.database()
            .ref('/' + CHANNELS_NODE + '/' + channel + '/lastContributor')
            .set(contributor)

        console.log('Setting /' + CHANNELS_NODE + '/' + channel + '/lastContributorFirstName = ' + contributorName)
        let updateContributorName = admin.database()
            .ref('/' + CHANNELS_NODE + '/' + channel + '/lastContributorFirstName')
            .set(contributorName)

        console.log('Setting /' + CHANNELS_NODE + '/' + channel + '/lastEntry = ' + lastEntry)
        let updateEntry = admin.database()
            .ref('/' + CHANNELS_NODE + '/' + channel + '/lastEntry')
            .set(lastEntry)

        console.log('Setting /' + CHANNELS_NODE + '/' + channel + '/lastEntryDate = ' + date)
        let updateEntryDate = admin.database()
            .ref('/' + CHANNELS_NODE + '/' + channel + '/lastEntryDate')
            .set(date)

        return Promise
            .all([
                updateContributor,
                updateContributorName,
                updateEntry,
                updateEntryDate])
    } else if (itemInfo.deleted == true) {
        //TODO: search for last entry and update channel accordingly
    }
}

function sendPush(itemInfo) {
    var title
    if (itemInfo.isNew == true) {
        title = "New entry"
    } else {
        if (itemInfo.changed) {
            title = "ItemChanged"
        } else {
            title = itemInfo.channel
        }
    }

    var msg = itemInfo.data.name

    return loadUsersFromChannel(itemInfo.channel)
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
}

function loadUsersFromChannel(channelId) {
    let ref = admin.database()
        .ref('/' + MEMBERS_NODE + '/' + channelId)
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
    let ref = admin.database().ref('/' + USERS_NODE + '/' + id)
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
    .ref('/' + CHANNELS_NODE + '/{id}')
    .onWrite(event => {
        var value = event.data.val()
        if (value.key) {
            var channelId = value.key
            //Check if this is a new channel with poperly setup properties
            let channelCreated = !event.data.previous.exists()
            let channelDeleted = !channelCreated && !event.data.exists()
            let channelStateChanged = !channelCreated && event.data.changed()
            if (channelCreated && !value.members && !value.lastEntry) {
                console.log(
                    'Removing ' + channelId + ' due to no members and no items'
                )
                var parent = event.data.ref.parent
                return parent.child(channelId).remove()
            } else if (!channelStateChanged) {
                console.log('New channel ' + channelId + ' validated and successfully created')
                console.log('TODO: inform users')
                return
            } else if (channelDeleted) {
                //Channel is deleted, remove all references
                return admin.database()
                    .ref('/' + ITEMS_NODE)
                    .remove(channelId)
            }
        }
    })

exports.handleUserAdd = functions.database
    .ref('/channels/{channelId}/members/{userId}')
    .onWrite(event => {
        var channelId = event.params.channelId
        var userId = event.params.userId
        var isNew = !event.data.previous.exists()
        var wasDeleted = !event.data.exists()
        if (isNew) {
            console.log('Adding new member ' + userId + ' of channel ' + channelId + ' to member and user channel lists')
            var userUpdate = admin.database()
                .ref('/' + USERS_NODE + '/' + userId + '/' + CHANNELS_NODE + '/' + channelId)
                .set(true)
            var membersUpdate = admin.database()
                .ref('/' + MEMBERS_NODE + '/' + channelId + '/' + userId)
                .set(true)
            return Promise.all([userUpdate, membersUpdate])
        } else if (wasDeleted) {
            console.log('Removing member ' + userId + ' of channel ' + channelId + ' from member and user channel lists')
            var userUpdate = admin.database()
                .ref('/' + USERS_NODE + '/' + userId + '/' + CHANNELS_NODE + '/' + channelId)
                .remove()
            var membersUpdate = admin.database()
                .ref('/' + MEMBERS_NODE + '/' + channelId + '/' + userId)
                .remove()
            return Promise.all([userUpdate, membersUpdate])
        }
    })

//##########################
//######## Helpers #########
//##########################

function setUserDeletedField(key, deleted) {
    let ref = admin.database()
        .ref('/' + USERS_NODE)
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
    let firstName = getFirstNameFromMail(mail)
    let lastName = getLastNameFromMail(mail)
    var displayName = null
    if (firstName != null) {
        displayName = firstName
    }
    if (lastName != null) {
        if (firstName != null) {
            displayName = displayName + ' ' + lastName
        } else {
            displayName = lastName
        }
    }
    return displayName
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
            arr = prefix.split(".");
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
            arr = prefix.split(".");
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
