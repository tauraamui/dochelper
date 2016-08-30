package co.uk.taurasystems.application.utils

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.poifs.filesystem.NotOLE2FileException
import org.apache.poi.poifs.filesystem.OfficeXmlFileException
import org.apache.poi.xwpf.usermodel.TextSegement
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
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

        fun identifyFile(file: File): String {
            if (documentTitleContains(file, "EMG", true)) return "EMG letter"
            if (documentTitleContains(file, "invoice", true)) return "invoice"

            //if (documentContains(file, "i n v o i c e")) return "invoice"
            //if (documentContains(file, "a private appointment has been arranged")) return "letter"
            /*
            val fileExt = getFileExt(file)
            if (fileExt.equals("doc")) {
                try {
                    val hwpfDocument = HWPFDocument(FileInputStream(file))
                    for (i in 0..hwpfDocument.range.numParagraphs()-1) {
                        if (hwpfDocument.range.getParagraph(i).text().toLowerCase().contains("i n v o i c e")) {
                            return "invoice"
                        } else if (hwpfDocument.range.getParagraph(i).text().toLowerCase().contains("a private appointment has been arranged")) {
                            return "letter"
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (fileExt.equals("docx")) {
                throw NotImplementedError("Cannot handle .docx files atm")
            }
            */
            return "unknown"
        }

        fun documentTitleContains(file: File, textToFind: String): Boolean {
            if (isDoc(file) && file.name.contains(textToFind)) return true
            else if (isDocx(file) && file.name.contains(textToFind)) return true
            return false
        }

        fun documentTitleContains(file: File, textToFind: String, toLower: Boolean): Boolean {
            if (!toLower) return documentTitleContains(file, textToFind)
            if (isDoc(file) && file.name.toLowerCase().contains(textToFind.toLowerCase())) return true
            else if (isDocx(file) && file.name.toLowerCase().contains(textToFind.toLowerCase())) return true
            return false
        }

        fun documentContains(file: File, textToFind: String): Boolean {
            if (isDoc(file)) {
                try {
                    val hwpfDocument = HWPFDocument(FileInputStream(file))
                    for (i in 0..hwpfDocument.range.numParagraphs()-1) {
                        if (hwpfDocument.range.getParagraph(i).text().toLowerCase().contains(textToFind.toLowerCase())) return true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (isDocx(file)) {
                try {
                    //TODO: Check to make sure this is actually they way to do string searching within a docx file...
                    val xwpfDocument = XWPFDocument(FileInputStream(file))
                    for (paragraph in xwpfDocument.paragraphsIterator) {
                        if (paragraph.text.contains(textToFind)) return true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return false
        }

        private fun isDoc(file: File?): Boolean {
            if (getFileExt(file) == "doc") return true
            return false
        }

        private fun isDocx(file: File?): Boolean {
            if (getFileExt(file) == "docx") return true
            return false
        }

        fun findAndReplaceTagsInDoc(file: File, keysAndValues: HashMap<String, String?>): Any? {
            if (isDoc(file)) {
                return replaceTagsInLegacyDoc(file, keysAndValues)
            } else if (isDocx(file)) {
                return replaceTagsInModernDoc(file, keysAndValues)
            } else {
                throw Exception("extension must match either '.doc' or '.docx'")
            }
        }

        private fun replaceTagsInLegacyDoc(file: File?, keysAndValues: HashMap<String, String?>): HWPFDocument? {
            var hwpfDocument: HWPFDocument? = null
            try {
                hwpfDocument = HWPFDocument(FileInputStream(file))
                val range = hwpfDocument.range
                for (i in 0.. range.numParagraphs()-1) {
                    val paragraph = range.getParagraph(i)
                    for (j in 0..paragraph.numCharacterRuns()-1) {
                        val run = paragraph.getCharacterRun(j)
                        for ((key, value) in keysAndValues) {
                            run.replaceText(key, value)
                        }
                    }
                }
                hwpfDocument.dataStream.inputStream().close()
            } catch (e: OfficeXmlFileException) {
                println("Document ${file?.name} is a newer .docx format...")
            } catch (e: NotOLE2FileException) {
                println("Document ${file?.name} has an invalid header signature")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return hwpfDocument
        }

        private fun replaceTagsInModernDoc(file: File?, keysAndValues: HashMap<String, String?>): XWPFDocument? {
            var xwpfDocument: XWPFDocument? = null
            try {
                xwpfDocument = XWPFDocument(FileInputStream(file))
                xwpfDocument.paragraphs.forEach {
                    println(it.text)
                }
                xwpfDocument.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return xwpfDocument
        }

        fun getFileExt(file: File?): String {
            if (file == null) return ""
            if (file.name.contains(".")) {
                return file.name.split(".")[1]
            } else {
                return ""
            }
        }

        fun getUniqueFileName(file: File, extension: String): String {

            var fileToSave = file
            var versionSuffix = 1

            val firstFile = File(file.absolutePath + "." + extension)
            if (!firstFile.exists()) return firstFile.absolutePath

            fileToSave = File(file.absolutePath + " $versionSuffix." + extension)

            while (fileToSave.exists()) {
                fileToSave = File(file.absolutePath + " $versionSuffix." + extension)
                versionSuffix++
            }
            return fileToSave.absolutePath
        }
    }
}