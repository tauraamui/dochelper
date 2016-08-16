package co.uk.taurasystems.application.ui

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Created by tauraaamui on 15/08/2016.
 */

class Window : Application() {

    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader(WindowPaneController::class.java.getResource("WindowPane.fxml"))
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
