package co.uk.taurasystems.application.ui.panes.controllers

import co.uk.taurasystems.application.managers.LetterManager
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream
import java.util.*
import co.uk.taurasystems.application.ui.openErrorDialog
import co.uk.taurasystems.application.utils.FileHelper
import co.uk.taurasystems.application.utils.WordDocHelper

/**
 * Created by tauraaamui on 15/08/2016.
 */
class LetterPaneController {

    private var chosenTemplateFileName: String? = null
    @FXML var refTextField: TextField? = null
    @FXML var titleTextField: TextField? = null
    @FXML var patientFullNameTextField: TextField? = null
    @FXML var addressLine1TextField: TextField? = null
    @FXML var addressLine2TextField: TextField? = null
    @FXML var addressLine3TextField: TextField? = null
    @FXML var addressLine4TextField: TextField? = null
    @FXML var postCodeTextField: TextField? = null
    @FXML var patientAbbrNameTextField: TextField? = null
    @FXML var letterDatePicker: DatePicker? = null
    @FXML var appointmentDatePicker: DatePicker? = null
    @FXML var appointmentTimeTextField: TextField? = null
    @FXML var letterTypeComboBox: ComboBox<String>? = null

    private val keysAndValues = HashMap<String, String?>()

    private var daysAndSuffixes = listOf<String>("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
            //10    11    12    13    14    15    16    17    18    19
            "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
            //20    21    22    23    24    25    26    27    28    29
            "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
            //30    31
            "th", "st")


    //private val filePathAndDocType = HashMap<String, File>()


    fun initialize() {
        setupFileList()
    }

    fun setupFileList() {
        LetterManager.findLetters()
        letterTypeComboBox?.items?.clear()
        LetterManager.lettersInRootFolder.forEach {
            letterTypeComboBox?.items?.add("${it.name}")
        }
    }

    fun setupDataMap() {
        chosenTemplateFileName = letterTypeComboBox?.value
        keysAndValues.put("{ref}", refTextField?.text!!)
        keysAndValues.put("{date}", getDatePickerValue(letterDatePicker))
        keysAndValues.put("{title}", titleTextField?.text!!)
        keysAndValues.put("{patient_full_name}", patientFullNameTextField?.text)
        keysAndValues.put("{patient_abbreviated_name}", patientAbbrNameTextField?.text)
        keysAndValues.put("{address_line_1}", addressLine1TextField?.text)
        keysAndValues.put("{address_line_2}", addressLine2TextField?.text)
        keysAndValues.put("{address_line_3}", addressLine3TextField?.text)
        keysAndValues.put("{address_line_4}", addressLine4TextField?.text)
        keysAndValues.put("{post_code}", postCodeTextField?.text!!)
        //keysAndValues.put("{appointment_date_time}", getDatePickerValue(appointmentDatePicker) + " " + appointmentTimeTextField?.text!!)
        getDatePickerValue(datePicker = letterDatePicker)
        getDatePickerValue(datePicker = appointmentDatePicker)
        keysAndValues.put("{appointment_date_time}", "${getDatePickerValue(appointmentDatePicker)} ${appointmentTimeTextField?.text}")
    }

    private fun getDatePickerValue(datePicker: DatePicker?): String {
        //var formattedDate: StringBuilder = StringBuilder()
        val calendar = Calendar.getInstance()
        if (datePicker == null || datePicker.value == null) return ""

        val day = datePicker.value.dayOfMonth
        val month = datePicker.value.month.toString()
        val year = datePicker.value.year

        val formattedDate = "$day${daysAndSuffixes[day]} ${camelCase(month)} $year"

        return formattedDate.toString()
    }

    fun createOutput() {
        if (chosenTemplateFileName.isNullOrEmpty()) {
            openErrorDialog("Error Dialog", "Missing template", "Must select a template from the drop down")
            return
        } else {
            val chosenTemplate = File("oxh_docs/$chosenTemplateFileName")
            WordDocHelper.openDocument(chosenTemplate)
            println(WordDocHelper.getDocumentContent())
            for ((key, value) in keysAndValues) { WordDocHelper.replaceTextInDocument(key, value) }
            val outputFilePath = FileHelper.getUniqueFileName(File("${LetterManager.Companion.oxhDocsOutputFolder}/${patientFullNameTextField?.text?.trimEnd()} EMG Letter.${FileHelper.Companion.getFileExt(chosenTemplate)}"))
            WordDocHelper.output(FileOutputStream(File(outputFilePath)))
            WordDocHelper.closeDocument()
        }

        /*
        for ((fileTypeName, file) in Dochelper.filePathAndDocType) {
            val editedDoc = Dochelper.findAndReplaceTagsInDoc(file, keysAndValues)
            if (fileTypeName == "unknown") continue
            if (fileTypeName.contains("letter")) {
                val outputFilePath = Dochelper.getUniqueFileName(File("$oxhDocsOutputFolder/${patientFullNameTextField?.text?.trimEnd()} $fileTypeName"), Dochelper.getFileExt(file))
                val outputStream = FileOutputStream(File(outputFilePath))
                if (editedDoc is HWPFDocument) editedDoc.write(outputStream)
                if (editedDoc is XWPFDocument) continue
                outputStream.close()
            }
        }
        */
    }

    private fun camelCase(stringToReformat: String): String {
        val stringLowerCase = stringToReformat.toLowerCase()
        val stringBuilder = StringBuilder()
        stringBuilder.setLength(stringLowerCase.length)
        stringLowerCase.forEachIndexed { i, c ->
            if (i == 0) {
                stringBuilder.set(i, stringLowerCase[i].toUpperCase())
            } else {
                stringBuilder.set(i, stringLowerCase[i])
            }
        }
        return stringBuilder.toString()
    }
}