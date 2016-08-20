package co.uk.taurasystems.application.utils

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.poifs.filesystem.NotOLE2FileException
import org.apache.poi.poifs.filesystem.OfficeXmlFileException
import java.io.File
import java.io.FileInputStream
import java.util.*

/**
 * Created by tauraaamui on 15/08/2016.
 */
class Dochelper {

    companion object {
        var filePathAndDocType = HashMap<String, File>()

        fun mapFiles(docDir: File?) {
            try {
                for (file in docDir?.listFiles()!!) {
                    if (file.isDirectory) continue
                    filePathAndDocType.put(identifyFile(file), file)
                }
            } catch (e: KotlinNullPointerException) {
                println("oxh_docs folder doesn't exist")
            }
        }

        fun identifyFile(file: File?): String {
            if (file?.name!!.contains('.') && file?.name!!.split('.')[1].equals("doc")) {
                try {
                    val fileInputStream = FileInputStream(file)
                    val document = HWPFDocument(fileInputStream)
                    val range = document.range
                    for (i in 0..range.numParagraphs()-1) {
                        val paragraph = range.getParagraph(i)
                        if (paragraph.text().contains("I N V O I C E")) {
                            return "invoice"
                        } else if (paragraph.text().toLowerCase().contains("a private appointment has been arranged")) {
                            return "letter"
                        }
                    }
                } catch (e: OfficeXmlFileException) {
                    println("Document ${file?.name} is a newer .docx format...")
                } catch (e: NotOLE2FileException) {
                    println("Document ${file?.name} has an invalid header signature")
                }
            }
            return "unknown"
        }

        fun findAndReplaceKeysInFile(file: File?, keysAndValues: HashMap<String, String?>): HWPFDocument? {
            var documentToReplaceTextWithin: HWPFDocument? = null
            var fileInputStream: FileInputStream? = null
            if (file?.name!!.contains('.') && file?.name!!.split('.')[1].equals("doc")) {
                try {
                    fileInputStream = FileInputStream(file)
                    documentToReplaceTextWithin = HWPFDocument(fileInputStream)
                    val range = documentToReplaceTextWithin.range
                    for (i in 0..range.numParagraphs()-1) {
                        val paragraph = range.getParagraph(i)
                        for (j in 0..paragraph.numCharacterRuns()-1) {
                            val run = paragraph.getCharacterRun(j)
                            for ((key, value) in keysAndValues) {
                                run.replaceText(key, value)
                            }
                        }
                    }
                    fileInputStream?.close()
                } catch (e: OfficeXmlFileException) {
                    println("Document ${file?.name} is a newer .docx format...")
                } catch (e: NotOLE2FileException) {
                    println("Document ${file?.name} has an invalid header signature")
                }
            }
            return documentToReplaceTextWithin
        }
    }
}