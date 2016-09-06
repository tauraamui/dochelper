package co.uk.taurasystems.application.tests

import co.uk.taurasystems.application.utils.WordDocHelper
import java.io.File

/**
 * Created by tauraaamui on 29/08/2016.
 */
class DochelperTest {

    val oxhDocsFolder = File("oxh_docs")
    var mappedFiles = false

    fun testDocumentContaines() {
        if (!mappedFiles) throw Exception("Need to run testMapFiles() first...")
        oxhDocsFolder.listFiles().forEach { println(WordDocHelper.documentContains("appointment")) }
    }
}