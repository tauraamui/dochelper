package co.uk.taurasystems.application.ui.panes

import co.uk.taurasystems.application.ui.Window
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

/**
 * Created by tauraaamui on 29/08/2016.
 */
class RootPaneController {

    @FXML var tabPane: TabPane? = null

    fun initialize() {
        val letterFormTab = Tab("Letter")
        val invoiceFormTab = Tab("Invoice")
        letterFormTab.content = (FXMLLoader(RootPaneController::class.java.getResource("LetterPane.fxml")).load())
        tabPane?.tabs?.add(letterFormTab)
    }
}