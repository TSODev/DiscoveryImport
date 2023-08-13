package utils

import com.google.gson.JsonObject
import utils.logging.TLogger

private val logger = TLogger

class Transformation {

    companion object {
        fun getValue(obj: JsonObject, key: String): String? {
            logger.addToPrefix("[transform]")
            var result = ""
            val resultValue = obj.get(key)
            logger.debug("$obj , $key , $resultValue")
            if (resultValue != null) {
                result = resultValue.toString().replace("\"", "")
            }
            logger.removeToPrefix("[transform]")
            return result
        }

        fun getIntList(obj: JsonObject, key: String): List<Int> {

            val jsonElement = obj.get(key)
            if (jsonElement.isJsonArray) {
                val list = mutableListOf<Int>()
                for (element in jsonElement.asJsonArray) {
// Check if the element is a primitive and a number
                    if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                        list.add(element.asInt)
                    }
                }
                return list
            } else {
                throw IllegalArgumentException("The JsonElement is not an array")
            }
        }

        fun getStringList(obj: JsonObject, key: String): MutableList<String> {

            val jsonElement = obj.get(key)
            if (jsonElement.isJsonArray) {
                val list = mutableListOf<String>()
                for (element in jsonElement.asJsonArray) {
                    if (element.isJsonPrimitive && element.asJsonPrimitive.isString) {
                        list.add(element.asString)
                    }
                }
                return list
            } else {
                throw IllegalArgumentException("The JsonElement is not an array")
            }
        }

        fun getIntValue(obj: JsonObject, key: String): Int {
            val jsonElement = obj.get(key)
            if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isNumber) {
                return jsonElement.asInt
            } else {
                throw IllegalArgumentException("The JsonElement is not a number")
            }
        }

        fun getStringValue(obj: JsonObject, key: String): String {
            val jsonElement = obj.get(key)
            if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isString) {
                return jsonElement.asString.replace("\"", "")
            } else {
                throw IllegalArgumentException("The JsonElement is not a string")
            }
        }

        fun stringToArrayInt(string: String): MutableList<Int> {
            val parts = string.split(",").map { it.trim() }
            val array = mutableListOf<Int>()
            for (part in parts) {
                try {
                    val number = part.toInt()
                    array += number
                } catch (e: NumberFormatException) {
                }
            }
            return array
        }

        fun stringToListString(string: String): List<String> {
            val parts = string.split(",").map { it.trim() }
            val list = parts.toMutableList()
            return list
        }

        fun stringToInt(string: String): Int {
            try {
                val number = string.toInt()
                return number
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("The string is not an integer")
            }
        }

    }
}