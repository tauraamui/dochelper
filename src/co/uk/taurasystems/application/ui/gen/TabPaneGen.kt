package co.uk.taurasystems.application.ui.gen

import co.uk.taurasystems.application.utils.XMLParser
import javafx.scene.Scene
import javafx.scene.control.Tab
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.util.*

/**
 * Created by alewis on 28/09/2016.
 */

interface GenElement
data class GenTab(var title: String, var genElements: ArrayList<GenElement>): GenElement
data class GenNamedField(var name: String, var type: String, var tag: String): GenElement
data class GenNamedDatePicker(var name: String, var format: String, var tag: String): GenElement

class TabPaneGen {

    fun generateRootPane() {
        val formXML = XMLParser().loadXMLDoc("form.xml")
        val genTabModels = generateTabsModel(formXML)
    }

    private fun generateTabsModel(document: Document): ArrayList<GenTab> {

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
