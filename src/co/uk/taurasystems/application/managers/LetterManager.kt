package co.uk.taurasystems.application.managers

import co.uk.taurasystems.application.utils.FileHelper
import java.io.File
import java.io.FileNotFoundException

/**
 * Created by alewis on 07/09/2016.
 */
class LetterManager {

    companion object {
        private val oxhDocsFolder = File("oxh_docs")
        private val oxhDocsOutputFolder = File("oxh_docs/output")

        val lettersInRootFolder = arrayListOf<File>()
        private var daysAndSuffixes = listOf<String>("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
                //10    11    12    13    14    15    16    17    18    19
                "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
                //20    21    22    23    24    25    26    27    28    29
                "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
                //30    31
                "th", "st")

        fun findLetters() {
            if (!oxhDocsFolder.exists() || !oxhDocsFolder.isDirectory) oxhDocsFolder.mkdir()
            if (!oxhDocsOutputFolder.exists() || !oxhDocsOutputFolder.isDirectory) oxhDocsOutputFolder.mkdir()
            if (oxhDocsFolder.exists() && oxhDocsFolder.isDirectory) {
                lettersInRootFolder.clear()
                oxhDocsFolder.listFiles().forEach {
                    if (it.exists()) {
                        if (FileHelper.fileTitleContains(it, "emg", false) || FileHelper.fileTitleContains(it, "letter", false)) {
                            lettersInRootFolder.add(it)
                        }
                    }
                }
            } else { throw FileNotFoundException("Folder doesn't exist or is not a folder: ${oxhDocsFolder.absolutePath}")}
        }

    }
}