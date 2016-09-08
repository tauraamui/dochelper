package co.uk.taurasystems.application.utils

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by tauraaamui on 15/08/2016.
 */
class WordDocHelper {

    companion object {

        private var legacyWordDocument: HWPFDocument? = null
        private var legacyWordDocumentOpen = false

        private var modernWordDocument = XWPFDocument()
        private var modernWordDocumentOpen = false

        fun openDocument(file: File?) {
            if (file == null) return
            closeDocument()
            if (file.exists()) {
                if (FileHelper.getFileExt(file) == "docx") {
                    openModernWordDocument(file)
                } else if (FileHelper.getFileExt(file) == "doc") {
                    openLegacyWordDocument(file)
                } else {
                    //TODO: Need to change exception type to something more relevant
                    throw Exception("Incorrect file extension for Word documents")
                }
            }
        }

        private fun openModernWordDocument(file: File?) {
            modernWordDocument = XWPFDocument(FileInputStream(file))
            modernWordDocumentOpen = true
        }

        private fun openLegacyWordDocument(file: File?) {
            if (file == null) return
            legacyWordDocument = HWPFDocument(FileInputStream(file))
            legacyWordDocumentOpen = true
        }

        fun closeDocument() {
            closeLegacyDocument()
            closeModernDocument()
        }

        private fun closeLegacyDocument() {
            if (legacyWordDocumentOpen) {
                legacyWordDocument?.dataStream?.inputStream()?.close()
                legacyWordDocumentOpen = false
            }
        }

        private fun closeModernDocument() {
            if (modernWordDocumentOpen) {
                modernWordDocument.close()
                modernWordDocumentOpen = false
            }
        }

        fun documentContains(textToFind: String): Boolean {
            var textFound = false
            if (legacyWordDocumentOpen) {
                for (i in 0..legacyWordDocument?.range?.numParagraphs()!! - 1) {
                    textFound = (legacyWordDocument?.range?.getParagraph(i)?.text()?.contains(textToFind)!!)
                    break
                }
            } else if (modernWordDocumentOpen) {
                for (paragraph in modernWordDocument.paragraphsIterator) {
                    textFound = (paragraph.text.contains(textToFind))
                    break
                }
            }
            return textFound
        }

        fun documentContains(textToFind: String, caseSensitive: Boolean): Boolean {
            var textFound = false
            if (!caseSensitive) documentContains(textToFind)
            if (legacyWordDocumentOpen) {
                for (i in 0..legacyWordDocument?.range?.numParagraphs()!! - 1) {
                    textFound = (legacyWordDocument?.range?.getParagraph(i)?.text()?.toLowerCase()?.contains(textToFind.toLowerCase())!!)
                    break
                }
            } else if (modernWordDocumentOpen) {
                for (paragraph in modernWordDocument.paragraphsIterator) {
                    textFound = (paragraph.text.toLowerCase().contains(textToFind.toLowerCase()))
                }
            }
            return textFound
        }

        fun isDoc(file: File?): Boolean = if (FileHelper.getFileExt(file) == "doc") true else false

        fun isDocx(file: File?): Boolean = if (FileHelper.getFileExt(file) == "docx") true else false

        fun isModernExcelDoc(file: File?): Boolean = if (FileHelper.getFileExt(file) == "xlsx") true else false

        fun isLegacyExcelDoc(file: File?): Boolean = if (FileHelper.getFileExt(file) == "xls") true else false

        fun replaceTextInDocument(stringTextToReplace: String, stringToReplaceWith: String?) {
            if (legacyWordDocumentOpen) {
                for (i in 0..legacyWordDocument?.range?.numParagraphs()!!-1) {
                    val paragraph = legacyWordDocument?.range?.getParagraph(i)
                    for (j in 0..paragraph?.numCharacterRuns()!!-1) {
                        val run = paragraph?.getCharacterRun(j)
                        run?.replaceText(stringTextToReplace, stringToReplaceWith)
                    }
                }
            } else if (modernWordDocumentOpen) {
                for (paragraph in modernWordDocument.paragraphsIterator) {
                    paragraph.text.replace(stringTextToReplace, stringToReplaceWith!!)
                    paragraph.text
                }
            }
        }

        fun getDocumentContent(): String {
            println("Legacy document open: $legacyWordDocumentOpen\nModern document open $modernWordDocumentOpen")
            if (legacyWordDocumentOpen) {
                for (i in 0..legacyWordDocument?.range?.numParagraphs()!!-1) {
                    val paragraph = legacyWordDocument?.range?.getParagraph(i)
                    for (j in 0..paragraph?.numCharacterRuns()!!-1) {
                        val run = paragraph?.getCharacterRun(j)
                        println(run?.text())
                    }
                }
                return ""
            } else if (modernWordDocumentOpen) {
                for (paragraph in modernWordDocument.paragraphsIterator) {
                    println(paragraph.text)
                }
                return ""
            }
            return "Document console out failed..."
        }

        fun output(outputStream: FileOutputStream) {
            if (legacyWordDocumentOpen) {
                legacyWordDocument?.write(outputStream)
            } else if (modernWordDocumentOpen) {
                modernWordDocument.write(outputStream)
            }
        }
    }
}