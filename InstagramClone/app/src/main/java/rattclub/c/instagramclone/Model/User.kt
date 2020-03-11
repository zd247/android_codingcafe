package rattclub.c.instagramclone.Model

class User {
    var username: String = ""
    var fullname: String = ""
    var bio: String = ""
    var image: String = ""
    var uid: String = ""

    constructor()

    constructor(username: String, fullname: String, bio: String, image: String, uid: String) {
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.image = image
        this.uid = uid
    }






}