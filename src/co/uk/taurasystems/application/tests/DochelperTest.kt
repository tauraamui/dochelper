package co.uk.taurasystems.application.tests

import co.uk.taurasystems.application.utils.Dochelper
import java.io.File

/**
 * Created by tauraaamui on 29/08/2016.
 */
class DochelperTest {

    val oxhDocsFolder = File("oxh_docs")
    var mappedFiles = false

    fun testMapFiles() {
        Dochelper.mapFiles(oxhDocsFolder)
        mappedFiles = true
    }

    fun testDocumentContaines() {
        if (!mappedFiles) throw Exception("Need to run testMapFiles() first...")
        for (file in oxhDocsFolder.listFiles()) {
            println(Dochelper.documentContains(file, "appointment"))
        }
    }
}