package co.uk.taurasystems.application.ui

import co.uk.taurasystems.application.ui.gen.RootPaneGen
import javafx.application.Application
import javafx.stage.Stage

/**
 * Created by tauraaamui on 15/08/2016.
 */

class Window : Application() {

    override fun start(primaryStage: Stage) {
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
