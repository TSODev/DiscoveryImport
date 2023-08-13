package fr.tsodev.discoverywrite.models

import java.lang.reflect.Type


data class Item(
    val kind: String,
    val attributes: List<Attribute>         // key , type , value
) {

}

data class Attribute(
    val key: String,
    var type: Type,
    var value: Any
) {

}


