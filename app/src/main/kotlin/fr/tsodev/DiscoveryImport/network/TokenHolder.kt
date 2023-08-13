package network

// Declare an object to store the token
object TokenHolder {
    // Declare a property to store the token
    var token: String = ""

    // Declare a function to save the token
    fun saveToken(token: String) {
// Assign the token to the property
        this.token = token
    }

//    fun getToken(): String {
//        return this.token
//    }
}
