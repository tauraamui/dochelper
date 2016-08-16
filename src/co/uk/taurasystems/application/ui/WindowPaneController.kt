package co.uk.taurasystems.application.ui

import co.uk.taurasystems.application.utils.Dochelper
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import org.apache.poi.hwpf.HWPFDocument
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.util.*

/**
 * Created by tauraaamui on 15/08/2016.
 */
class WindowPaneController {

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
    @FXML var okButton: Button? = null
    @FXML var cancelButton: Button? = null

    private val keysAndValues = HashMap<String, String?>()

    fun initialize() {
        okButton?.setOnAction {e -> setupFileMap()
            setupDataMap()
            createOutput()
        }
        cancelButton?.setOnAction { e -> System.exit(0) }
    }

    private fun setupFileMap() {
        val oxhDocsFolder = File("oxh_docs")
        if (oxhDocsFolder.exists() && oxhDocsFolder.isDirectory) {
            Dochelper.mapFiles(File("oxh_docs"))
        } else {
            oxhDocsFolder.mkdir()
        }
    }

    private fun setupDataMap() {
        keysAndValues.put("{ref}", refTextField?.text!!)
        keysAndValues.put("{date}", if (letterDatePicker?.value == null) "" else letterDatePicker?.value.toString())
        keysAndValues.put("{title}", titleTextField?.text!!)
        keysAndValues.put("{patient_full_name}", patientFullNameTextField?.text!!)
        keysAndValues.put("{address_line_1}", addressLine1TextField?.text!!)
        keysAndValues.put("{address_line_2}", addressLine2TextField?.text!!)
        keysAndValues.put("{address_line_3}", addressLine3TextField?.text!!)
        keysAndValues.put("{address_line_4}", addressLine4TextField?.text!!)
        keysAndValues.put("{post_code}", postCodeTextField?.text!!)
        keysAndValues.put("{patient_abbreviated_name}", patientAbbrNameTextField?.text!!)
        keysAndValues.put("{appointment_date_time}", (if (appointmentDatePicker?.value == null) "" else appointmentDatePicker?.value.toString()) + " " + (appointmentTimeTextField?.text!!))

        for ((key, value) in keysAndValues) {
            println(key + " " + value)
        }
    }

    private fun createOutput() {
        for ((fileTypeName, file) in Dochelper.filePathAndDocType) {
            val editedDoc = Dochelper.findAndReplaceKeysInFile(file, keysAndValues)
            if (fileTypeName.equals("unknown")) continue
            val outputStream = FileOutputStream("oxh_docs\\${patientFullNameTextField?.text} $fileTypeName.doc")
            editedDoc?.write(outputStream)
            outputStream.close()
        }
    }
}