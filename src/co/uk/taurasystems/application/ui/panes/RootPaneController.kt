package co.uk.taurasystems.application.ui.panes

import co.uk.taurasystems.application.ui.Window
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.AnchorPane

/**
 * Created by tauraaamui on 29/08/2016.
 */
class RootPaneController {

    @FXML var tabPane: TabPane? = null
    @FXML var okButton: Button? = null
    @FXML var cancelButton: Button? = null

    fun initialize() {
        val letterFormTab = Tab("Letter")
        val invoiceFormTab = Tab("Invoice")
        val letterPaneLoader = FXMLLoader(RootPaneController::class.java.getResource("LetterPane.fxml"))
        val scrollPane = ScrollPane()
        val anchorPane = AnchorPane()
        scrollPane.content = letterPaneLoader.load()
        AnchorPane.setTopAnchor(scrollPane, 0.toDouble())
        AnchorPane.setBottomAnchor(scrollPane, 0.toDouble())
        AnchorPane.setRightAnchor(scrollPane, 0.toDouble())
        AnchorPane.setLeftAnchor(scrollPane, 0.toDouble())
        anchorPane.children.add(scrollPane)
        letterFormTab.content = anchorPane

        okButton?.setOnAction {e ->

            val selectedTab = tabPane?.tabs?.get(tabPane?.selectionModel?.selectedIndex!!)

            when (selectedTab) {
                letterFormTab -> {
                    val letterPaneController = letterPaneLoader.getController<LetterPaneController>()
                    letterPaneController.setupDataMap()
                    letterPaneController.createOutput()
                }
            }

            /*
            val letterPaneController = letterPaneLoader.getController<LetterPaneController>()
            letterPaneController.setupFileMap()
            letterPaneController.setupDataMap()
            letterPaneController.createOutput()
            */
        }
        cancelButton?.setOnAction { e -> System.exit(0) }

        tabPane?.tabs?.add(letterFormTab)
        tabPane?.tabs?.add(invoiceFormTab)
    }
}