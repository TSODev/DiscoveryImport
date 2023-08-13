package fr.tsodev.DiscoveryImport.models

import com.google.gson.JsonObject

data class TopologyImport(
    val items: List<Item>,
    val source: String,
    val source_prefix: String
)

data class Item(
    val `data`: JsonObject,
    val key: String,
    val kind: String,
    val role1: Role1,
    val role2: Role1
)

data class Role1(
    val key: String,
    val kind: String,
    val role: String
)