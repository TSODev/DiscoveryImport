package api


import com.google.gson.JsonObject
import fr.tsodev.DiscoveryImport.models.Import
import fr.tsodev.DiscoveryImport.models.KindAttributeList
import fr.tsodev.DiscoveryImport.models.NodeKind
import fr.tsodev.DiscoveryImport.models.TopologyImport
import fr.tsodev.discoverywrite.models.Command
import fr.tsodev.discoverywrite.models.Partition

import network.RetrofitClient
import retrofit2.HttpException
import utils.logging.TLogger

private val logger = TLogger

interface ServiceApi {

    companion object {

        fun apiGetToken(serverUrl: String, username: String, password: String, unsafe: Boolean): String? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)

            var token: String? = null
            val response = apiDiscovery.authenticateUser("password", username, password).execute()
            if (response.isSuccessful) {
                token = response.body()?.token!!
            } else {
                throw HttpException(response)
            }
            return token

        }

        fun apiGetNodeKinds(serverUrl: String, section: String, unsafe: Boolean): List<NodeKind>? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)

            var result: List<NodeKind>? = null
            val response = apiDiscovery.apiGetNodeKinds(section = section).execute()
            if (response.isSuccessful) {
                result = response.body()
            } else {
                throw HttpException(response)
            }
            return result

        }

        fun apiGetKindAttributeList(serverUrl: String, kind: String, unsafe: Boolean): KindAttributeList? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)

            var result: KindAttributeList? = null
            val response = apiDiscovery.apiGetKindAttributeList(kind = kind).execute()
            if (response.isSuccessful) {
                result = response.body()
            } else {
                throw HttpException(response)
            }
            return result

        }

        fun apiGetIdByName(serverUrl: String, name: String, kind: String, unsafe: Boolean): JsonObject? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)

            val body = JsonObject()
            body.addProperty("kind",kind.replace("\"",""))
            body.addProperty("name", name.replace("\"",""))
            logger.debug("GETID: $kind, $name -> $body")
            var id = JsonObject()
            val response = apiDiscovery.apiGetIdByName(body).execute()
            if (response.isSuccessful) {
                id = response.body()!!

            } else {
                throw HttpException(response)
            }
            return id
        }

        fun apiImport(serverUrl: String, name: String, import: Import, unsafe: Boolean): String? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)

            var result = ""
            val response = apiDiscovery.apiImport(import).execute()
            if (response.isSuccessful) {
                result = response.body().toString()
            } else {
                throw HttpException(response)
            }
            return result
        }

        fun apiTopologyImport(serverUrl: String, name: String, topology: TopologyImport, unsafe: Boolean): String? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)

            var result = ""
            val response = apiDiscovery.apiImportGraph(topology).execute()
            if (response.isSuccessful) {
                result = response.body().toString()
            } else {
                throw HttpException(response)
            }
            return result
        }


        fun apiGetPartition(serverUrl: String,unsafe: Boolean): JsonObject? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)
            var id = JsonObject()
            val response = apiDiscovery.apiGetPartitions().execute()
            if (response.isSuccessful) {
                id = response.body()!!
            } else {
                throw HttpException(response)
            }
            return id
        }
        fun apiCreatePartition(serverUrl: String, partition: Partition, unsafe: Boolean): String? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)
            var id = ""
            val response = apiDiscovery.apiCreatePartition(partition).execute()
            if (response.isSuccessful) {
                id = "OK"
            } else {
                throw HttpException(response)
            }
            return id
        }
        fun apiSendCommand(serverUrl: String, commands: List<Command>, unsafe: Boolean): String? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)
            var id = ""
            logger.debug("Commandes :$commands")
            val response = apiDiscovery.apiWriteCommand(commands).execute()
            if (response.isSuccessful) {
                id = response.body().toString()
            } else {
                throw HttpException(response)
            }
            return id
        }
    }
}




