import api.ServiceApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.google.gson.JsonObject
import fr.tsodev.DiscoveryImport.models.Import
import fr.tsodev.DiscoveryImport.models.ItemX
import fr.tsodev.DiscoveryImport.models.KindAttributeList
import fr.tsodev.DiscoveryImport.models.NodeKind
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import network.TokenHolder
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import retrofit2.HttpException
import utils.logging.Level
import utils.logging.TLogger
import java.io.File
import java.util.*
import kotlin.system.exitProcess


private val logger = TLogger


val partitionName = "DiscoveryImport"
val sourceName = "DiscoveryImport"
val typeName = "Test"

//val kindTabName = "NEWKINDS"
fun main(args: Array<String>):Unit =   DiscoveryImport().versionOption("1.0.0").main(args)

class DiscoveryImport: CliktCommand(help = "Ecrit des données dans BMC Discovery") {

    val validURL = "^(http(s):\\/\\/.)[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)(/)\$"

    val server: String by option(
        "-s", "--server",
        help = "URL API du serveur Discovery , (https et termine avec '/') \n " +
                "généralement https://server/api/v1.1/"
    )
        .required()
        .validate {
            if (!it.matches(Regex(validURL)))
                throw UsageError(
                    "URL du serveur invalide : $it \n" +
                            "lancer le programme avec l'option -h pour de l'aide"
                )
        }

    val username: String by option(
        "-u", "--username",
        help = "Nom de l'utilisateur"
    ).prompt()

    val password: String by option(
        "-p", "--password",
        help = "Mot de Passe de l'utilisateur"
    ).prompt(hideInput = true)

    val unsafe by option(
        "-x", "--unsecure",
        help = "pas de vérification du certificat SSL\n" +
                "(permet l'utilisation de certificat auto signé)"
    ).flag(default = true)

    val path by option(
        "-f", "--file",
        help = "path du fichier source à importer"
    ).file()

    val params by option(
        "-a", "--params",
        help = "Objet JSON à traiter"
    )

    val explain by option(
        "-e", "--explain",
        help = "explique la commande"
    ).flag(default = false)

    val generate by option(
        "-g", "--generate",
        help = ("Crée un fichier Excel de modele d'import")
    ).flag(default = false)

    val debugLevel by option(
        "-d", "--debug",
        help = "Niveau du debug"
    ).choice(
        "off" to Level.OFF,
        "trace" to Level.TRACE,
        "debug" to Level.DEBUG,
        "info" to Level.INFO,
        "error" to Level.ERROR,
        "fatal" to Level.FATAL,
        "all" to Level.ALL
    ).default(Level.OFF)

    override fun run() {


        echo("=========================================================================")
        echo(" Discovery Write Data - TSO pour Orange Business - 08/23 - version 1.0.0 ")
        echo("=========================================================================")

        logger.setRunLevel(debugLevel)
//        logger.addDateTimeToPrefix()
//        logger.addDurationToPrefix()

//        val appFolderPath = System.getProperty("user.dir")
//        logger.debug("Application Folder Path: $appFolderPath")
//        logger.debug("File Path: ${path?.absoluteFile}")

        if (password.isNullOrEmpty()) {
            logger.info("Erreur d'argument : PASSWORD ne peut pas être vide")
            exitProcess(-4)
        }

        if (username.isNullOrEmpty()) {
            logger.info("Erreur d'argument : USERNAME ne peut pas être vide")
            exitProcess(-4)
        }

        if (generate) {
            logger.info("Demande de génération du fichier modèle")

            path?.let { generateByCoroutines(username, password, server, it,  unsafe) }


        } else {

            val inputStream = path?.name?.let { File(it).inputStream() }
            val workbook = WorkbookFactory.create(inputStream)
            val sheetIterator = workbook.sheetIterator()
            val sheets: MutableList<IndexedValue<org.apache.poi.ss.usermodel.Sheet>> = mutableListOf()

            sheetIterator.withIndex().forEach { sheet ->
                sheets.add(sheet)
            }

            if (!sheets.isNotEmpty()) throw PrintMessage("Le fichier excel ne peut pas etre vide", -1, true)

    //        val newKinds = sheets.filter { (it.value.sheetName.uppercase(Locale.getDefault())) == kindTabName }
            val newKinds = sheets.filter { it.index == 0 }

            val newItems = mutableListOf<JsonObject>()
            val newItem = JsonObject()
            val newItemData = mutableListOf<ItemX>()
            if (newKinds.isEmpty())
                throw PrintMessage(
                    "Erreur : Le premier onglet du fichier excel doit contenir la liste des noeuds à importer",
                    -5,
                    true
                )
            else {
                logger.debug("Kinds : $newKinds,  ${sheets.get(newKinds.first().index)}")
                val kindSheet = sheets.get(newKinds.first().index).value
                logger.debug("Kind Sheet : $kindSheet , ${kindSheet.header}")
                val rowIterator = kindSheet.rowIterator()
                val attrs = mutableListOf<String>()
                rowIterator.forEach { row ->
                    var importedData = JsonObject()
                    val cellIterator = row.cellIterator().withIndex()
                    if (row.rowNum == 0) {                                  // First Row is Header Row with attribute names
//                    cellIterator.next()                                 // First Column is Kind name
                        cellIterator.forEach { cell ->
                            attrs.add(cell.value.stringCellValue)
                        }
                    } else {
                        when (row.rowNum) {
                            1 -> {}                                         // row is type
                            2 -> {}                                         // Description
                            3 -> {}                                         //
                            else -> {
                                cellIterator.forEach { cell ->
                                    val dataValue: Any? = cellValueByType(cell.value)
                                    importedData.addProperty(attrs.get(cell.index), dataValue)
                                }
                                val kind = newKinds.first().value.sheetName
                                if (kind != null) {
                                    logger.debug("ImportedData : row[${row.rowNum}] -> $importedData")
                                    newItemData.add(ItemX(importedData, kind.toString()))
                            }
                        }

                        }
                    } // if not null

                } // row iterator

                newItem.addProperty("items", newItemData)
                newItems.add(newItem)
                logger.debug("Import : $newItems")

            }


            val uuid = UUID.randomUUID()

            apiCallByCoroutines(
                username = username,
                password = password,
                server = server,
                Import(
                    complete = true,
                    items = newItemData,
                    source = sourceName,
                    type = typeName,
                    uuid = uuid.toString()
                ),
                unsafe = unsafe
            )
        }

    }

    private fun cellValueByType(cell: Cell) : Any? {
        var dataValue: Any? = null
        val cType = cell.cellType
        if (cType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            dataValue = cell.dateCellValue
        } else
            when (cType) {
                CellType.STRING -> dataValue = cell.stringCellValue
                CellType.NUMERIC -> dataValue = cell.numericCellValue
                CellType.BOOLEAN -> dataValue = cell.booleanCellValue
                CellType.ERROR -> dataValue = null
                CellType.BLANK -> dataValue = null
                CellType._NONE -> dataValue = null
                else -> dataValue = cell.stringCellValue
            }
        return dataValue

}

private fun JsonObject.addProperty(attrValue: String?, dataValue: Any?) {
    when (dataValue) {
        is String -> addProperty(attrValue, dataValue)
        is Number -> addProperty(attrValue, dataValue)
        is Boolean -> addProperty(attrValue, dataValue)
        else -> addProperty(attrValue, dataValue.toString()) // Convert non-primitive
    }
}


private fun apiCallByCoroutines(
    username: String,
    password: String,
    server: String,
    items: Import,
    unsafe: Boolean
) = runBlocking {
    launch { // launch new coroutine in the scope of runBlocking

        try {
            ServiceApi.apiGetToken(server, username, password, unsafe).let { token: String? ->
                if (token != null) {
                    TokenHolder.saveToken(token)
//                    ServiceApi.apiImport(server,"IMPORT", import = items, unsafe).let { result ->
//                        logger.debug("Import Result : $result")
//                    }
                }
            }
        } catch (exception: HttpException) {
            logger.error("Erreur : ${exception.message}")
            exitProcess(-1)
        }
    }
}

    //TODO - Insert Orange Business Logo @ row 1
    private fun generateByCoroutines(
        username: String,
        password: String,
        server: String,
        path: File,
        unsafe: Boolean
    ) = runBlocking {
        launch { // launch new coroutine in the scope of runBlocking

            val excelFile = File(path.name)
            if (excelFile.exists()) excelFile.delete()
            val outputStream = path.name.let { File(it).outputStream() }
            val workbook = XSSFWorkbook()
            workbook.createSheet("Instructions")

            val cs0: CellStyle = workbook.createCellStyle()
            val cs1: CellStyle = workbook.createCellStyle()
            val cs2: CellStyle = workbook.createCellStyle()
            val cs3: CellStyle = workbook.createCellStyle()

            val f0: Font = workbook.createFont()
            val f1: Font = workbook.createFont()
            val f2: Font = workbook.createFont()
            val f3: Font = workbook.createFont()
            f0.fontName = "Helvetica"
            f0.bold = true
            f0.fontHeightInPoints = 12.toShort()
            f0.color = IndexedColors.DARK_BLUE.index
            f1.fontName = "Helvetica"
            f1.bold = false
            f1.fontHeightInPoints = 10.toShort()
            f1.color = IndexedColors.WHITE.index
            f2.fontName = "Helvetica"
            f2.bold = false
            f2.italic = true
            f2.fontHeightInPoints = 10.toShort()
            f3.fontName = "Helvetica"
            f3.fontHeightInPoints = 10.toShort()
            f3.color = IndexedColors.RED.index

            cs0.setFont(f0);
            cs1.setFont(f1);
            cs2.setFont(f2);
            cs3.setFont(f3);
            cs0.alignment = HorizontalAlignment.LEFT
            cs1.alignment = HorizontalAlignment.RIGHT
            cs3.borderBottom = BorderStyle.MEDIUM

            cs0.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            cs1.fillForegroundColor = IndexedColors.DARK_BLUE.index
            cs2.fillForegroundColor = IndexedColors.LIGHT_ORANGE.index
            cs3.fillForegroundColor = IndexedColors.LIGHT_ORANGE.index
            cs0.fillPattern = FillPatternType.SOLID_FOREGROUND
            cs1.fillPattern = FillPatternType.SOLID_FOREGROUND
            cs2.fillPattern = FillPatternType.SOLID_FOREGROUND
            cs3.fillPattern = FillPatternType.SOLID_FOREGROUND

            cs0.borderLeft = BorderStyle.DOTTED
            cs1.borderLeft = BorderStyle.DOTTED
            cs2.borderLeft = BorderStyle.DOTTED
            cs3.borderLeft = BorderStyle.DOTTED
            cs0.borderRight = BorderStyle.DOTTED
            cs1.borderRight = BorderStyle.DOTTED
            cs2.borderRight = BorderStyle.DOTTED
            cs3.borderRight = BorderStyle.DOTTED

            cs0.rotation = 45.toShort()
            cs2.wrapText = true

            var nodeKinds : List<NodeKind> = listOf()
            val attributes: MutableList<Pair<String, KindAttributeList>> = mutableListOf()

            try {
                ServiceApi.apiGetToken(server, username, password, unsafe).let { token: String? ->
                    if (token != null) {
                        TokenHolder.saveToken(token)
                    ServiceApi.apiGetNodeKinds(server,section = "inferred", unsafe).let { result ->
                        if (result != null) {
                            nodeKinds = result.sortedBy { it.name }
                        }
                        logger.info("Récupération taxonomy des noeuds et création des onglets excel")
                        nodeKinds.forEach { nodeKind ->

                            ServiceApi.apiGetKindAttributeList(server, nodeKind.name, unsafe).let { attrList ->
                                if (attrList != null) {
                                    if (attrList.attrs.isNotEmpty()) {
                                        val worksheet = workbook.createSheet(nodeKind.name)
                                        val rowZero = worksheet.createRow(0)
                                        val rowOne = worksheet.createRow(1)
                                        val rowTwo = worksheet.createRow(2)
                                        val rowThree = worksheet.createRow(3)
                                        val rowFour = worksheet.createRow(4)
                                        attributes.add(Pair(nodeKind.name.uppercase(), attrList!!))
                                        var i = 0

                                        attrList.attrs.forEachIndexed { index, attr ->
                                            val cZero = rowZero.createCell(i)
                                            val cOne = rowOne.createCell(i)
                                            val cTwo = rowTwo.createCell(i)
                                            val cThree = rowThree.createCell(i)
                                            val cFour = rowFour.createCell(i)

                                            cZero.setCellStyle(cs0)
                                            cOne.setCellStyle(cs1)
                                            cTwo.setCellStyle(cs2)
                                            cThree.setCellStyle(cs3)
                                            if (i == 0) cThree.setCellValue("vvvvvv - Commencer la liste des données à importer à la rangée 5 - vvvvv")

                                            cZero.setCellValue(attr.name)
                                            cOne.setCellValue("(${attr.type})")
                                            cTwo.setCellValue(attr.description)
                                            when (attr.type.uppercase()) {
                                                "STRING" ->  cFour.cellType = CellType.STRING
                                                "INT" -> cFour.cellType = CellType.NUMERIC
                                                "BOOLEAN" -> cFour.cellType = CellType.BOOLEAN
                                                "DATE" -> {
                                                    cFour.cellType = CellType.STRING
                                                }
                                                "DICTIONARY"-> cFour.cellType = CellType.STRING
                                                "LIST:STRING" -> cFour.cellType = CellType.STRING
                                                "LIST:INT" -> cFour.cellType = CellType.STRING
                                                "FLOAT" -> cFour.cellType = CellType.NUMERIC
                                                else -> logger.debug ("Type ${attr.type} n'est pas pris en compte")
                                            }

                                            worksheet.setColumnWidth(i, 20 * 256)
                                            i += 1
                                        }
                                        logger.info(".", false)
                                    }
                                }
                            }

                        }
                        logger.info("")
                        workbook.write(outputStream)
                        workbook.close()
                        logger.info("Le fichier modèle est crée $path")
                        logger.info("vous pouver en faire une copie, le completer avec les noeuds à importer et relancer le programme")
                    }
                    }
                }
            } catch (exception: HttpException) {
                logger.error("Erreur : ${exception.message}")
                exitProcess(-1)
            }
        }
    }

}
