package fr.tsodev.DiscoveryImport.models

data class Import(
    val complete: Boolean,
    val items: List<ItemX>,
    val source: String,
    val type: String,
    val uuid: String
)