package co.uk.taurasystems.application.managers

import co.uk.taurasystems.application.utils.FileHelper
import java.io.File
import java.io.FileNotFoundException

/**
 * Created by alewis on 07/09/2016.
 */

class LetterManager {

    companion object {
        val oxhDocsFolder = File("oxh_docs")
        val oxhDocsOutputFolder = File("oxh_docs/output")

        val lettersInRootFolder = arrayListOf<File>()

        fun findLetters() {
            if (!oxhDocsFolder.exists() || !oxhDocsFolder.isDirectory) oxhDocsFolder.mkdir()
            if (!oxhDocsOutputFolder.exists() || !oxhDocsOutputFolder.isDirectory) oxhDocsOutputFolder.mkdir()
            if (oxhDocsFolder.exists() && oxhDocsFolder.isDirectory) {
                oxhDocsFolder.listFiles().forEach {
                    if (it.exists()) {
                        if (FileHelper.fileTitleContains(it, "emg", false) || FileHelper.fileTitleContains(it, "letter", false)) {
                            if (!lettersInRootFolder.contains(it)) lettersInRootFolder.add(it)

                        }
                    }
                }
            } else { throw FileNotFoundException("Folder doesn't exist or is not a folder: ${oxhDocsFolder.absolutePath}")}
        }

        fun outputLetter(letterNameToOutput: String) {

        }
    }
}