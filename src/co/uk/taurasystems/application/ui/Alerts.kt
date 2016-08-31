package co.uk.taurasystems.application.ui

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType

/**
 * Created by tauraamui on 31/08/2016.
 */

fun openAlertDialog(type: AlertType, title: String, headerText: String, contentText: String) {
    val dialog = Alert(type)
    dialog.title = title
    dialog.headerText = headerText
    dialog.contentText = contentText
    dialog.showAndWait()
}

fun openErrorDialog(title: String, headerText: String, contentText: String) {
    val dialog = Alert(AlertType.ERROR)
    dialog.title = title
    dialog.headerText = headerText
    dialog.contentText = contentText
    dialog.showAndWait()
}