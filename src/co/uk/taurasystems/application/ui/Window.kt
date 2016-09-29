package co.uk.taurasystems.application.ui

import co.uk.taurasystems.application.ui.gen.RootPaneGen
import javafx.application.Application
import javafx.stage.Stage

/**
 * Created by tauraaamui on 15/08/2016.
 */

class Window : Application() {

    override fun start(primaryStage: Stage) {
        /*
        //val loader = FXMLLoader(LetterPaneController::class.java.getResource("LetterPane.fxml"))
        val loader = FXMLLoader(RootPaneController::class.java.getResource("/co/uk/taurasystems/application/ui/panes/views/RootPane.fxml"))
        primaryStage.scene = Scene(loader.load())
        primaryStage.show()
        */

        val rootPaneGen = RootPaneGen()
        primaryStage.scene = rootPaneGen.generateRootPane(primaryStage)
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Window::class.java)
        }
    }
}
