package co.uk.taurasystems.application.ui

import co.uk.taurasystems.application.tests.DochelperTest
import co.uk.taurasystems.application.ui.panes.LetterPaneController
import co.uk.taurasystems.application.ui.panes.RootPaneController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Created by tauraaamui on 15/08/2016.
 */

class Window : Application() {

    override fun start(primaryStage: Stage) {
        //val loader = FXMLLoader(LetterPaneController::class.java.getResource("LetterPane.fxml"))
        val loader = FXMLLoader(RootPaneController::class.java.getResource("RootPane.fxml"))
        primaryStage.scene = Scene(loader.load())
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Window::class.java)
        }
    }
}
