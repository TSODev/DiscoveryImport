package fr.tsodev.discoverywrite.models


data class Command(
    var cmd: String?,
    var id: String?,
    var kind: String?,
    var partition: String?,
    var role1: Role?,
    var role2: Role?,
    var state: Map<String, String>?,
    var types: Map<String, String>?,
    val from: Role?,
    val rel: Relation?,
    val to: Destination?,
    val void: List<String>?,
    var match_keys: Array<String>?,
    var update: Boolean?,
    var cascade: Boolean?,
    var async: Boolean?
)

data class Relation(
    val kind: String,
    val state: Map<String, String>?,
)

data class Destination(
    val role: Role,
    val kind: String,
    val state: Map<String, String>?,
    val match_keys: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Destination

        if (role != other.role) return false
        if (kind != other.kind) return false
        if (state != other.state) return false
        return match_keys.contentEquals(other.match_keys)
    }

    override fun hashCode(): Int {
        var result = role.hashCode()
        result = 31 * result + kind.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + match_keys.contentHashCode()
        return result
    }
}

data class Role(
    val id: String,
    val partition: String,
    val role: String
)

class Type(
    val key: String,
    val value: String,
)

