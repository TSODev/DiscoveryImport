package models

import com.google.gson.annotations.SerializedName

data class AuthentificationResponse(

    @SerializedName("access_token")
    var token: String?,

    @SerializedName("expires_in")
    var expires_in: Long?,

    @SerializedName("token_type")
    var token_type: String?
) {
    constructor() : this(null,null,null)
}
