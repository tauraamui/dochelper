package co.uk.taurasystems.application

import javafx.scene.layout.GridPane
import java.time.LocalDate

/**
 * Created by alewis on 30/09/2016.
 */

fun GridPane.getRowCount(): Int {
    var numRows = rowConstraints.size
    for (i in 0..children.size-1) {
        val child = children[i]
        if (child.isManaged) {
            val rowIndex = GridPane.getRowIndex(child)
            if (rowIndex != null) {
                numRows = Math.max(numRows, rowIndex+1)
            }
        }
    }
    return numRows
}