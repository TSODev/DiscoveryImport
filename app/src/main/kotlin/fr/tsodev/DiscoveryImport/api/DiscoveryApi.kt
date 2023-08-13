package api

import com.google.gson.JsonObject
import fr.tsodev.DiscoveryImport.models.Import
import fr.tsodev.DiscoveryImport.models.KindAttributeList
import fr.tsodev.DiscoveryImport.models.NodeKind
import fr.tsodev.DiscoveryImport.models.TopologyImport
import fr.tsodev.discoverywrite.models.Command
import fr.tsodev.discoverywrite.models.CommandResponse
import fr.tsodev.discoverywrite.models.Partition
import models.*
import retrofit2.Call
import retrofit2.http.*

interface DiscoveryApi {

    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    @POST("/api/token")
    fun authenticateUser(
        @Field("grant_type") grant_type: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<AuthentificationResponse>

    @Headers(
        "Content-Type: application/json"
    )
    @POST("data/write")
    fun apiWriteCommand(
        @Body cmd: List<Command>,

        ): Call<CommandResponse>


    @Headers(
        "Content-Type: application/json"
    )
    @POST("data/import")
    fun apiImport(
        @Body import: Import,

        ): Call<String>

    @Headers(
        "Content-Type: application/json"
    )
    @POST("data/import/graph")
    fun apiImportGraph(
        @Body topo: TopologyImport,

        ): Call<String>


    @Headers(
        "Content-Type: application/json"
    )
    @POST("data/partitions")
    fun apiCreatePartition(
        @Body partition: Partition,

        ): Call<String>

    @Headers(
        "Content-Type: application/json"
    )
    @GET("data/partitions")
    fun apiGetPartitions(
        ): Call<JsonObject>

    @Headers(
        "Content-Type: application/json"
    )
    @GET("taxonomy/nodekinds")
    fun apiGetNodeKinds(
        @Query("format") format: String = "info",
        @Query("section") section: String? = null,
        @Query("locale") locale: String? = null
    ): Call<List<NodeKind>>

    @Headers(
        "Content-Type: application/json"
    )
    @GET("taxonomy/nodekinds/{kind}")
    fun apiGetKindAttributeList(
        @Path("kind") kind: String,
        @Query("format") format: String = "info",
        @Query("section") section: String? = null,
        @Query("locale") locale: String? = null
    ): Call<KindAttributeList>

    @Headers(
        "Content-Type: application/json"
    )
    @POST("data/candidate")
    fun apiGetIdByName(
        @Body query: JsonObject,

        ): Call<JsonObject>
}