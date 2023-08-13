package fr.tsodev.DiscoveryImport.models

data class KindAttributeList(
    val attrs: List<Attr>
)

data class Attr(
    val description: String,
    val display_name: String,
    val name: String,
    val type: String
)