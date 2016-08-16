package co.uk.taurasystems.application.ui

import co.uk.taurasystems.application.utils.Dochelper
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File
import java.net.URL

/**
 * Created by tauraaamui on 15/08/2016.
 */

class Window : Application() {

    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader(URL("\\src\\co\\uk\\taurasystems\\WindowPane.fxml"))
        primaryStage.scene = loader.load()
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val docDir = File("oxh_docs")
            if (docDir.exists()) {
                for (file in docDir.listFiles()) {
                    Dochelper.filePathAndDocType.put(Dochelper.identifyFile(file), file)
                }

                for ((key, value) in Dochelper.filePathAndDocType) {
                    println(key + " " + value)
                }
            }
            launch(Window::class.java)
        }
    }
}
