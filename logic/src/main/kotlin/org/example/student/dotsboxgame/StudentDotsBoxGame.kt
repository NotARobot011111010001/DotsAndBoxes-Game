package org.example.student.dotsboxgame

import uk.ac.bournemouth.ap.dotsandboxeslib.*
import uk.ac.bournemouth.ap.lib.matrix.*
import java.lang.Exception
import kotlin.random.Random


class StudentDotsBoxGame(val columns: Int, val rows: Int, players: List<Player>) : AbstractDotsAndBoxesGame() {
    override val players: List<Player> = players.toList()
    //TODO("You will need to get players from your constructor")

    override var currentPlayer: Player = players[0]
    //TODO("Determine the current player, like keeping" + "the index into the players list")

    // NOTE: you may want to me more specific in the box type if you use that type in your class
    override val boxes: Matrix<StudentBox> = MutableMatrix(columns, rows, ::StudentBox)
    //TODO("Create a matrix initialized with your own box type")

    override val lines: SparseMatrix<StudentLine> = MutableSparseMatrix(columns+1, rows*2+1,
        { x, y -> x < columns || y % 2 == 1 }, ::StudentLine)
    //TODO("Create a matrix initialized with your own line type")

    override var isFinished: Boolean = false
    //TODO("Provide this getter. Note you can make it a var to do so (with private set)")

    override fun playComputerTurns() {
        var current = currentPlayer
        while (current is ComputerPlayer && ! isFinished) {
            current.makeMove(this)
            current = currentPlayer
        }
    }

    /**
     * This is an inner class as it needs to refer to the game to be able to look up the correct
     * lines and boxes. Alternatively you can have a game property that does the same thing without
     * it being an inner class.
     */
    inner class StudentLine(lineX: Int, lineY: Int) : AbstractLine(lineX, lineY) {
        override var isDrawn: Boolean = false
        //TODO("Provide this getter. Note you can make it a var to do so")


        override val adjacentBoxes: Pair<StudentBox?, StudentBox?>
            get() {
                if (lineX == 0 && lineY % 2 ==1) {
                    val boxLeft = null
                    val boxRight = boxes[lineX, lineY / 2]
                    return Pair(boxLeft,boxRight)
                }
                else if (lineX == columns) {
                    val boxLeft = boxes[lineX - 1, lineY / 2]
                    val boxRight = null
                    return Pair(boxLeft, boxRight)
                }
                else if (lineY == 0) {
                    val boxLeft = null
                    val boxRight = boxes[lineX, lineY]
                    return Pair(boxLeft, boxRight)
                }
                else if (lineY == rows * 2) {
                    val boxLeft = boxes[lineX, lineY / 2 - 1]
                    val boxRight = null
                    return Pair(boxLeft, boxRight)
                }
                else if (lineY % 2 == 1) {
                    val boxLeft = boxes[lineX - 1, lineY / 2]
                    val boxRight = boxes[lineX, lineY / 2]
                    return Pair(boxLeft, boxRight)
                }
                else {
                    val boxLeft = boxes[lineX, lineY - (lineY / 2 + 1)]
                    val boxRight = boxes[lineX, lineY - (lineY / 2)]
                    return Pair(boxLeft, boxRight)
                }
                // TODO("You need to look up the correct boxes for this to work")
            } // end of adjacentBoxes

        override fun drawLine() {

            var twoTurns = false // checks for both turns to be executed/played

            if (!this.isDrawn) {
                this.isDrawn = true
                var finalLine = this
                for (boxes in adjacentBoxes.toList()) {
                    if (boxes != null) {
                        val playerBox = boxes.boundingLines.all { it.isDrawn }
                        if (playerBox) {
                            boxes.owningPlayer = currentPlayer
                            twoTurns = true
                        }
                    }
                }
            } else {
                throw Exception("Cannot draw line")
            }
            fireGameChange()

            if (!twoTurns) {
                currentPlayer = if (currentPlayer == players[0]) { // rotates the player
                    players[1]
                } else {
                    players[0]
                }

                if (currentPlayer is ComputerPlayer) { // checks if current player is computer
                    // currentPlayer = players[1]
                    playComputerTurns() // plays the turn
                    this.isDrawn = true
                    var finalLine = this
                    for (boxes in adjacentBoxes.toList()) {
                        if (boxes != null) {
                            val computerBox = boxes.boundingLines.all { it.isDrawn }
                            if (computerBox) {
                                boxes.owningPlayer = currentPlayer
                            }
                        }
                    }
                }
            } // end 0f (!twoTurns)

            var gameOverFlag = boxes.all { it.owningPlayer != null } // checks if all boxes owned

            if (gameOverFlag) {
                isFinished = true // sets the game to 'finished'

                var scores = getScores().mapIndexed { index, score -> players[index] to score}
                fireGameOver(scores)
            } // end of gameOverFlag

            //TODO("Implement the logic for a player drawing a line. Don't forget to inform the listeners (fireGameChange, fireGameOver)")
            // NOTE read the documentation in the interface, you must also update the current player.
        } // end of drawLine()

    } // end of StudentLine()

    inner class StudentBox(boxX: Int, boxY: Int) : AbstractBox(boxX, boxY) {
        override var owningPlayer: Player? = null // this will change as the game progresses
        //TODO("Provide this getter. Note you can make it a var to do so")

        /**
         * Gets the bordering lines of each box
         * This must be lazy or a getter, otherwise there is a chicken/egg problem with the boxes
         */
        override val boundingLines: Iterable<DotsAndBoxesGame.Line>
            get() =
                listOf(
                    lines[boxX, (boxY * 2)],
                    lines[boxX + 1, (boxY * 2) + 1],
                    lines[boxX, (boxY + 1) * 2],
                    lines[boxX, (boxY * 2) + 1]
                )
        //TODO("Look up the correct lines from the game outer class")
    }
}
