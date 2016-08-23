package co.uk.taurasystems.application.utils

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.poifs.filesystem.NotOLE2FileException
import org.apache.poi.poifs.filesystem.OfficeXmlFileException
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
                for ((key, value) in filePathAndDocType) {
                    println("$key $value")
                }
            } catch (e: KotlinNullPointerException) {
                println("oxh_docs folder doesn't exist")
            }
        }

        fun identifyFile(file: File): String {
            val fileExt = getFileExt(file)
            println(fileExt)
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
                return "not implemented docx"
            }
            return "unknown"
        }

        fun findAndReplaceKeysInDoc(file: File?, keysAndValues: HashMap<String, String?>): Any? {

            if (getFileExt(file).equals("doc")) {
                return replaceTagsInLegacyDoc(file, keysAndValues)
            } else if (getFileExt(file).equals("docx")) {
                return replaceTagsInModernDoc(file, keysAndValues)
            } else {
                throw Exception("extension must match either '.doc' or '.docx'")
            }

            /*
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
                    fileInputStream.close()
                } catch (e: OfficeXmlFileException) {
                    println("Document ${file?.name} is a newer .docx format...")
                } catch (e: NotOLE2FileException) {
                    println("Document ${file?.name} has an invalid header signature")
                }
            }
            return documentToReplaceTextWithin
            */
        }

        private fun replaceTagsInLegacyDoc(file: File?, keysAndValues: HashMap<String, String?>): HWPFDocument? {
            var hwpfDocument: HWPFDocument? = null
            try {
                hwpfDocument = HWPFDocument(FileInputStream(file))
                val range = hwpfDocument.range
                for (i in 0.. range.numParagraphs()-1) {
                    val paragraph = range.getParagraph(i)
                    for (j in 0..paragraph.numCharacterRuns()-1) {
                        val run = paragraph.getCharacterRun(i)
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

        private fun getFileExt(file: File?): String {
            if (file == null || !file.exists()) return ""
            if (file.name.contains(".")) {
                return file.name.split(".")[1]
            } else {
                return ""
            }
        }
    }
}