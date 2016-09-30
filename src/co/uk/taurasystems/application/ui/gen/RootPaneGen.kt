package co.uk.taurasystems.application.ui.gen

import co.uk.taurasystems.application.getRowCount
import co.uk.taurasystems.application.ui.openErrorDialog
import co.uk.taurasystems.application.utils.XMLParser
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.awt.GridLayout
import java.io.File
import java.util.*

/**
 * Created by alewis on 28/09/2016.
 */

interface GenElement {
    var tag: String
}

//data class GenTab(var title: String, var genElements: ArrayList<GenElement>): GenElement
data class GenNamedList(var name: String, var type: String, var source: String, override var tag: String) : GenElement

data class GenNamedField(var name: String, var type: String, override var tag: String) : GenElement
data class GenNamedDatePicker(var name: String, var format: String, override var tag: String) : GenElement

class GenTab : GenElement {

    override var tag = ""
    var title: String = ""
    var genElements: ArrayList<GenElement> = ArrayList<GenElement>()

    constructor(title: String, genElements: ArrayList<GenElement>) {
        this.title = title
        this.genElements = genElements
    }

    //TODO: Add additional methods, for stuff that I've forgotten... :P
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

                    for (genTabModel in genTabModels) {
                        println("Tab ${genTabModel.title}")
                        genTabModel.genElements.forEach { println(it) }
                    }

                    generateTabsFromModels(genTabModels).forEach { tabPane.tabs.add(it) }

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

    private fun generateTabsFromModels(genTabModels: ArrayList<GenTab>): ArrayList<Tab> {
        val tabList = ArrayList<Tab>()
        for (genTabModel in genTabModels) {
            val tab = Tab()
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
            //so meta, but getRowCount is an extension method, check the 'ExtensionMethods.kt' file
            println(layoutManager.getRowCount())
            appendButtonBarToGridPane(layoutManager, genTabModel.genElements)
            tab.content = scrollPane
            tabList.add(tab)
        }
        return tabList
    }

    private fun appendButtonBarToGridPane(layoutManager: GridPane, genElements: ArrayList<GenElement>) {
        val buttonBar = ButtonBar()
        buttonBar.prefHeight = 15.0

        buttonBar.buttons.add(createOKButton(layoutManager, genElements))
        layoutManager.addRow(layoutManager.getRowCount() + 1, buttonBar)
    }

    private fun createOKButton(layoutManager: GridPane, genElements: ArrayList<GenElement>): Button {
        val okButton = Button("OK")
        okButton.onAction = EventHandler {
            val tagsAndValues = getTagsAndValues(layoutManager, genElements)
            tagsAndValues.forEach { key, value ->  println("$key $value")}
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
                        } else {
                            openErrorDialog("Error", "No template selected", "Please select a template from the drop down..."); return@forEach
                        }
                    } else if (it is TextField && genElement is GenNamedField) {
                        tagsAndValues.put(it.id, it.text)
                    } else if (it is DatePicker && genElement is GenNamedDatePicker) {
                        //TODO: find out the correct way to get the date with a certain format...
                        //val dateValue = it.value.toString()
                        //tagsAndValues.put(it.id, it.value.toString())
                    }
                }
            }
        }
        return tagsAndValues
    }

    private fun getTemplateList(sourceDir: File): Array<File> {
        if (sourceDir.exists()) {
            if (sourceDir.isDirectory) {
                return sourceDir.listFiles()
            } else {
                throw Exception("Source must be a directory")
            }
        } else {
            throw Exception("Source directory ${sourceDir.name} does not exist...")
        }
    }

    private fun generateTabsModels(document: Document): ArrayList<GenTab> {

        val genTabs = ArrayList<GenTab>()

        val tabs = document.getElementsByTagName("tab")

        for (i in 0..tabs.length - 1) {
            val tabData = tabs.item(i) as Element

            val genTab = GenTab("", ArrayList())
            genTab.title = tabData.getAttribute("title")

            val genElements = ArrayList<GenElement>()
            for (j in 0..tabData.childNodes.length - 1) {
                val tabChildNodeData = tabData.childNodes.item(j)
                when (tabChildNodeData.nodeName) {
                    "named_field" -> genElements.add(createNamedFieldModel(tabChildNodeData as Element))
                    "named_date_picker" -> genElements.add(createNamedDatePickerModel(tabChildNodeData as Element))
                    "named_list" -> genElements.add(createNamedListModel(tabChildNodeData as Element))
                }
            }
            genTab.genElements = genElements
            genTabs.add(genTab)
        }
        return genTabs
    }

    private fun createNamedFieldModel(element: Element): GenNamedField {
        val genNamedField = GenNamedField("", "", "")
        genNamedField.name = element.getAttribute("name")
        genNamedField.type = element.getAttribute("type")
        genNamedField.tag = element.getAttribute("tag")
        return genNamedField
    }

    private fun createNamedDatePickerModel(element: Element): GenNamedDatePicker {
        val genNamedDatePicker = GenNamedDatePicker("", "", "")
        genNamedDatePicker.name = element.getAttribute("name")
        genNamedDatePicker.format = element.getAttribute("format")
        genNamedDatePicker.tag = element.getAttribute("tag")
        return genNamedDatePicker
    }

    private fun createNamedListModel(element: Element): GenNamedList {
        val genNamedList = GenNamedList("", "", "", "")
        genNamedList.name = element.getAttribute("name")
        genNamedList.type = element.getAttribute("type")
        genNamedList.source = element.getAttribute("source")
        return genNamedList
    }
}
