package fr.tsodev.discoverywrite.models

data class CommandResponse (
        val cmd: String,
        val code: Int,
        val id: String?,
        val partition: String?,
        val created: Boolean?,
        val node: Node?,
        val rel: Rel?
    )

data class Node(
    val id: String,
    val partition: String
)

data class Rel(
    val id: String,
    val partition: String
)