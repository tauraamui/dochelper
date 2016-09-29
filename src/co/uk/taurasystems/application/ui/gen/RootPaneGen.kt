package co.uk.taurasystems.application.ui.gen

import co.uk.taurasystems.application.utils.XMLParser
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

interface GenElement
data class GenTab(var title: String, var genElements: ArrayList<GenElement>): GenElement
data class GenNamedList(var name: String, var type: String, var source: String): GenElement
data class GenNamedField(var name: String, var type: String, var tag: String): GenElement
data class GenNamedDatePicker(var name: String, var format: String, var tag: String): GenElement

class RootPaneGen {

    fun generateRootPane(primaryStage: Stage): Scene {
        val formXML = XMLParser().loadXMLDoc("form.xml")

        if (formXML.hasChildNodes()) {
            val rootElement = formXML.firstChild
            if (rootElement != null) {
                rootElement as Element

                if (rootElement.nodeName == "root_window") {
                    val tabPane = TabPane()
                    var scene = Scene(GridPane())

                    val windowTitle = rootElement.getAttribute("title")
                    val width = rootElement.getAttribute("width")
                    val height = rootElement.getAttribute("height")

                    if (windowTitle != null) { primaryStage.title = windowTitle}

                    if (width != null && height != null) {
                        scene = Scene(tabPane, width.toDouble(), height.toDouble())
                    } else {
                        throw Exception("Width and height are not defined...")
                    }

                    val genTabModels = generateTabsModels(formXML)

                    for (genTabModel in genTabModels) {
                        genTabModel.genElements.forEach { println(it) }
                    }

                    for (genTabModel in genTabModels) {
                        val tab = Tab()
                        tab.text = genTabModel.title
                        val layoutManager = GridPane()
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
                                //TODO: add generation of template list
                            } else if (genElement is GenNamedField) {
                                val nameLabel = Label(genElement.name)
                                nameLabel.id = genElement.name.replace(" ", "_").plus("_Label")
                                layoutManager.add(nameLabel, 0, i)
                                val field = TextField()
                                field.id = genElement.name.replace(" ", "_")
                                layoutManager.add(field, 1, i)
                            } else if (genElement is GenNamedDatePicker) {
                                val nameLabel = Label(genElement.name)
                                nameLabel.id = genElement.name.replace(" ", "_").plus("_Label")
                                layoutManager.add(nameLabel, 0, i)
                                val datePicker = DatePicker()
                                datePicker.id = genElement.name.replace(" ", "_")
                                layoutManager.add(datePicker, 1, i)
                            }
                        }
                        tab.content = layoutManager
                        tabPane.tabs.add(tab)
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

    private fun getTemplateList(sourcePath: String) {
        val sourceDirectory = File(sourcePath)
        if (sourceDirectory.exists() && sourceDirectory.isDirectory) {
            //TODO: Finish off this method
        }
    }

    private fun generateTabsModels(document: Document): ArrayList<GenTab> {

        val genTabs = ArrayList<GenTab>()

        val tabs = document.getElementsByTagName("tab")

        for (i in 0..tabs.length-1) {
            val tabData = tabs.item(i) as Element

            val genTab = GenTab("", ArrayList())
            genTab.title = tabData.getAttribute("title")

            val genElements = ArrayList<GenElement>()
            for (j in 0..tabData.childNodes.length-1) {
                val tabChildNodeData = tabData.childNodes.item(j)
                when (tabChildNodeData.nodeName) {
                    "named_field" -> genElements.add(createNamedFieldModel(tabChildNodeData as Element))
                    "named_date_picker" -> genElements.add(createNamedDatePickerModel(tabChildNodeData as Element))
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
}
