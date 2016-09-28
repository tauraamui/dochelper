package co.uk.taurasystems.application.ui.gen

import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by alewis on 28/09/2016.
 */
class TabPaneGen {

    fun loadTabs() {
        val formXML = loadXMLDoc("form.xml")
    }

    fun loadXMLDoc(name: String): Document {
        val scanner = Scanner(File(name))
        val strBuilder = StringBuilder()

        while (scanner.hasNext()) { strBuilder.append(scanner.nextLine()) }
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val byteStream = ByteArrayInputStream(strBuilder.toString().toByteArray())
        val doc = builder.parse(byteStream)
        doc.documentElement.normalize()
        return doc
    }
}
