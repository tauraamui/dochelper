package co.uk.taurasystems.application.ui.gen

import co.uk.taurasystems.application.getRowCount
import co.uk.taurasystems.application.ui.openErrorDialog
import co.uk.taurasystems.application.utils.FileHelper
import co.uk.taurasystems.application.utils.WordDocHelper
import co.uk.taurasystems.application.utils.XMLParser
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import javafx.util.StringConverter
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.awt.GridLayout
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by alewis on 28/09/2016.
 */

interface GenElement {
    var id: Int
    var tag: String
}

interface GenElementLink {
    var sourceTabID: Int
    var sourceID: Int
    var destinationTabID: Int
    var destinationID: Int
}

//data class GenTab(var title: String, var genElements: ArrayList<GenElement>): GenElement
data class GenNamedList(override var id: Int, var name: String, var type: String, var source: String, override var tag: String) : GenElement
data class GenNamedField(override var id: Int, var name: String, var type: String, override var tag: String) : GenElement
data class GenNamedDatePicker(override var id: Int, var name: String, var format: String, override var tag: String) : GenElement

class GenLink : GenElementLink {
    override var sourceTabID: Int = -1
    override var sourceID: Int = -1
    override var destinationTabID: Int = -1
    override var destinationID: Int = -1
}

class GenTab : GenElement {

    override var tag = ""
    override var id: Int = -1
    var title: String = ""
    var genElements: ArrayList<GenElement> = ArrayList<GenElement>()
    var outputDirectory: File = File("")
    var outputFilePrefixes = ArrayList<String>()

    constructor(title: String, genElements: ArrayList<GenElement>) {
        this.title = title
        this.genElements = genElements
    }
}

class RootPaneGen {

    var sourceDir = File("")

    fun generateRootPane(primaryStage: Stage): Scene {
        val formXML = XMLParser().loadXMLDoc("form.xml")

        if (formXML.hasChildNodes()) {
            val rootElement = formXML.firstChild
            if (rootElement != null) {
                rootElement as Element

                if (rootElement.nodeName == "root_window") {
                    val tabPane = TabPane()
                    tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                    //setting scene to something arbitary because I don't want it to be null
                    var scene = Scene(GridPane())

                    val windowTitle = rootElement.getAttribute("title")
                    val width = rootElement.getAttribute("width")
                    val height = rootElement.getAttribute("height")

                    if (windowTitle != null) {
                        primaryStage.title = windowTitle
                    }

                    if (width != null && height != null) {
                        scene = Scene(tabPane, width.toDouble(), height.toDouble())
                    } else {
                        throw Exception("Width and height are not defined...")
                    }

                    val genTabModels = generateTabsModels(formXML)
                    val links = generateLinkModels(formXML)

                    generateTabsFromModels(genTabModels, links).forEach { tabPane.tabs.add(it) }

                    for (link in links) {
                        var sourceTab = Tab()
                        var destinationTab = Tab()

                        tabPane.tabs.forEach {
                            if (it.id.toInt() == link.sourceTabID) { sourceTab = it }
                            if (it.id.toInt() == link.destinationTabID) { destinationTab = it }
                        }

                        if (sourceTab.id != null && destinationTab.id != null) {
                            for (genTabModel in genTabModels) {
                                genTabModel.genElements.forEach {
                                    if (it.id == link.sourceID)
                                    println("Element ID: ${it.id}, Source ID: ${link.sourceID} ${(it.id == link.sourceID)}")
                                }
                            }
                            genTabModels.filter { it.id == link.sourceID }.forEach { println(it.genElements) }
                        }
                    }

                    for (tab in tabPane.tabs) {
                        if (tab.content is ScrollPane) {
                            val scrollPane = tab.content as ScrollPane
                            if (scrollPane.content is GridPane) {
                                val gridPane = scrollPane.content as GridPane
                                //gridPane.children.forEach { println(it.id) }
                            }
                        }
                    }

                    return scene
                } else {
                    throw Exception("Name of root node must be 'root_window'")
                }
            } else {
                throw Exception("Unable to find root node...")
            }
        } else {
            throw Exception("Layout document has no nodes...")
        }
    }

    private fun generateTabsFromModels(genTabModels: ArrayList<GenTab>, links: ArrayList<GenLink>): ArrayList<Tab> {
        val tabList = ArrayList<Tab>()
        for (genTabModel in genTabModels) {
            val tab = Tab()
            tab.id = genTabModel.id.toString()
            tab.text = genTabModel.title
            val scrollPane = ScrollPane()
            val layoutManager = GridPane()
            scrollPane.content = layoutManager
            layoutManager.vgap = 5.0
            layoutManager.hgap = 5.0
            layoutManager.padding = Insets(10.0, 10.0, 10.0, 10.0)
            layoutManager.addColumn(0)
            layoutManager.addColumn(1)

            genTabModel.genElements.forEachIndexed { i, genElement ->
                layoutManager.addRow(i)

                if (genElement is GenNamedList) {
                    val nameLabel = Label(genElement.name)
                    nameLabel.id = genElement.name.replace(" ", "_").plus("_Label")
                    layoutManager.add(nameLabel, 0, i)

                    val comboBox = ComboBox<String>()
                    val source = File(genElement.source)
                    val templateList = getTemplateList(source)

                    templateList.forEach { comboBox.items.add(it.name) }
                    comboBox.id = genElement.tag
                    layoutManager.add(comboBox, 1, i)

                } else if (genElement is GenNamedField) {
                    val nameLabel = Label(genElement.name)
                    nameLabel.id = genElement.name.replace(" ", "_").plus("_Label")
                    layoutManager.add(nameLabel, 0, i)

                    val field = TextField()
                    field.id = genElement.tag
                    layoutManager.add(field, 1, i)

                } else if (genElement is GenNamedDatePicker) {
                    val nameLabel = Label(genElement.name)
                    nameLabel.id = genElement.name.replace(" ", "_").plus("_Label")
                    layoutManager.add(nameLabel, 0, i)

                    val datePicker = DatePicker()
                    datePicker.id = genElement.tag
                    layoutManager.add(datePicker, 1, i)
                }
            }

            appendButtonBarToGridPane(layoutManager, genTabModel.genElements, genTabModel.outputFilePrefixes, genTabModel.outputDirectory)
            tab.content = scrollPane
            tabList.add(tab)
        }
        return tabList
    }

    private fun appendButtonBarToGridPane(layoutManager: GridPane, genElements: ArrayList<GenElement>, outputFileNamePrefixTags: ArrayList<String>, outputDirectory: File) {
        val buttonBar = ButtonBar()
        buttonBar.prefHeight = 15.0

        buttonBar.buttons.add(createOKButton(layoutManager, genElements, outputFileNamePrefixTags, outputDirectory))
        layoutManager.addRow(layoutManager.getRowCount() + 1, buttonBar)
    }

    private fun createOKButton(layoutManager: GridPane, genElements: ArrayList<GenElement>, outputFileNamePrefixTags: ArrayList<String>, outputDirectory: File): Button {
        val okButton = Button("OK")
        okButton.onAction = EventHandler {
            val tagsAndValues = getTagsAndValues(layoutManager, genElements)
            val outputFileNamePrefixTagValues = ArrayList<String>()
            //tagsAndValues.forEach { key, value -> println("$key $value") }
            for (prefixTag in outputFileNamePrefixTags) {
                tagsAndValues.forEach { key, value ->
                    if (key == prefixTag) { outputFileNamePrefixTagValues.add(value) }
                }
            }
            val templateFile = File(tagsAndValues["{template_file}"])
            if (templateFile.exists()) {
                replaceTagsInTemplate(templateFile, tagsAndValues, outputFileNamePrefixTagValues, outputDirectory)
            }
        }
        return okButton
    }

    private fun getTagsAndValues(layoutManager: GridPane, genElements: ArrayList<GenElement>): HashMap<String, String> {
        val tagsAndValues = HashMap<String, String>()
        var templateFile = File("")
        for (genElement in genElements) {
            layoutManager.children.forEach {
                if (it.id == genElement.tag) {
                    if (it is ComboBox<*> && genElement is GenNamedList) {
                        val selectedIndex = it.selectionModel.selectedIndex
                        if (selectedIndex >= 0) {
                            templateFile = File(genElement.source.plus("/${it.items[selectedIndex]}"))
                            tagsAndValues.putIfAbsent("{template_file}", templateFile.absolutePath)
                        } else {
                            tagsAndValues.putIfAbsent("{template_file}", "no file selected")
                        }
                    } else if (it is TextField && genElement is GenNamedField) {
                        tagsAndValues.put(it.id, it.text)
                    } else if (it is DatePicker && genElement is GenNamedDatePicker) {
                        if (it.value != null) {
                            val chronoDate = it.chronology.date(it.value)
                            tagsAndValues.putIfAbsent(it.id, chronoDate.format(DateTimeFormatter.ofPattern(genElement.format)))
                        } else {
                            tagsAndValues.putIfAbsent(it.id, "")
                        }
                    }
                }
            }
        }
        return tagsAndValues
    }

    private fun replaceTagsInTemplate(document: File, tagsAndValues: HashMap<String, String>, fileNamePrefixes: ArrayList<String>, outputDirectory: File) {
        if (FileHelper.getFileExt(document) == "docx" || FileHelper.getFileExt(document) == "doc") {
            val wordDocHelper = WordDocHelper()
            wordDocHelper.openDocument(document)
            tagsAndValues.forEach { tag, value -> wordDocHelper.replaceTextInDocument(tag, value) }
            if (!outputDirectory.exists()) { outputDirectory.mkdir() }
            val documentName = "${document.name}"
            var documentNameWithPrefixes = ""
            for (prefix in fileNamePrefixes) { documentNameWithPrefixes += "$prefix "}
            documentNameWithPrefixes += documentName
            val outputFilePath = "${outputDirectory.absolutePath}/${documentNameWithPrefixes}"
            wordDocHelper.output(FileOutputStream(FileHelper.getUniqueFileName(File(outputFilePath))))
            wordDocHelper.closeDocument()
        }
    }

    private fun getTemplateList(sourceDir: File): Array<File> {
        if (sourceDir.exists()) {
            if (sourceDir.isDirectory) {
                return sourceDir.listFiles()
            } else {
                throw Exception("Source must be a directory")
            }
        } else {
            sourceDir.mkdir()
            return getTemplateList(sourceDir)
        }
    }

    private fun generateTabsModels(document: Document): ArrayList<GenTab> {

        val genTabs = ArrayList<GenTab>()

        val tabs = document.getElementsByTagName("tab")

        for (i in 0..tabs.length - 1) {
            val tabData = tabs.item(i) as Element

            if (tabData.nodeName == "tab") {
                val genTab = GenTab("", ArrayList())
                try {
                    genTab.id = tabData.getAttribute("id").toInt()
                } catch (e: NumberFormatException) {}
                genTab.title = tabData.getAttribute("title")

                val genElements = ArrayList<GenElement>()
                for (j in 0..tabData.childNodes.length - 1) {
                    val tabChildNodeData = tabData.childNodes.item(j)
                    when (tabChildNodeData.nodeName) {
                        "named_field" -> genElements.add(createNamedFieldModel(tabChildNodeData as Element))
                        "named_date_picker" -> genElements.add(createNamedDatePickerModel(tabChildNodeData as Element))
                        "named_list" -> genElements.add(createNamedListModel(tabChildNodeData as Element))
                        "output_directory" -> {
                            val outputDirectoryElement = (tabChildNodeData as Element)
                            genTab.outputDirectory = File(outputDirectoryElement.getAttribute("path"))
                            val prefixes = ArrayList<String>()
                            for (i in 0..outputDirectoryElement.attributes.length) {
                                prefixes.add(outputDirectoryElement.getAttribute("suffix$i"))
                            }
                            genTab.outputFilePrefixes = prefixes
                        }
                    }
                }
                genTab.genElements = genElements
                genTabs.add(genTab)
            }

        }
        return genTabs
    }

    private fun generateLinkModels(document: Document): ArrayList<GenLink> {
        val genLinks = ArrayList<GenLink>()
        val links = document.getElementsByTagName("link")

        for (i in 0..links.length-1) {
            val linkData = links.item(i) as Element

            val genLink = GenLink()
            if (linkData.nodeName == "link") {
                genLink.sourceTabID = linkData.getAttribute("source_tab_id").toInt()
                genLink.sourceID = linkData.getAttribute("source_id").toInt()
                genLink.destinationTabID = linkData.getAttribute("destination_tab_id").toInt()
                genLink.destinationID = linkData.getAttribute("destination_id").toInt()
            }
            genLinks.add(genLink)
        }
        return genLinks
    }

    private fun createNamedFieldModel(element: Element): GenNamedField {
        val genNamedField = GenNamedField(-1, "", "", "")
        try { genNamedField.id = element.getAttribute("id").toInt() } catch (e: NumberFormatException) {}
        genNamedField.name = element.getAttribute("name")
        genNamedField.type = element.getAttribute("type")
        genNamedField.tag = element.getAttribute("tag")
        return genNamedField
    }

    private fun createNamedDatePickerModel(element: Element): GenNamedDatePicker {
        val genNamedDatePicker = GenNamedDatePicker(-1, "", "", "")
        try { genNamedDatePicker.id = element.getAttribute("id").toInt() } catch (e: NumberFormatException) {}
        genNamedDatePicker.name = element.getAttribute("name")
        genNamedDatePicker.format = element.getAttribute("format")
        genNamedDatePicker.tag = element.getAttribute("tag")
        return genNamedDatePicker
    }

    private fun createNamedListModel(element: Element): GenNamedList {
        val genNamedList = GenNamedList(-1, "", "", "", "")
        try { genNamedList.id = element.getAttribute("id").toInt() } catch (e: NumberFormatException) {}
        genNamedList.name = element.getAttribute("name")
        genNamedList.type = element.getAttribute("type")
        genNamedList.source = element.getAttribute("source")
        genNamedList.tag = element.getAttribute("tag")
        return genNamedList
    }
}
