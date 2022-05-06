package uk.ac.bournemouth.ap.dotsandboxes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.GestureDetectorCompat
import com.google.android.material.snackbar.Snackbar
import org.example.student.dotsboxgame.StudentDotsBoxGame
import uk.ac.bournemouth.ap.dotsandboxeslib.HumanPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.Player


open class DotsAndLinesView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    var game: StudentDotsBoxGame = StudentDotsBoxGame(6,6, players = listOf(HumanPlayer(), HumanPlayer()))

    // gets the rows and columns from the logic
    val rows = game.rows
    val columns = game.columns

    private val human = game.players[0]
    private val computer = game.players[1]

    // text for players
    private val humanText: String = "Human: "
    private val computerText: String = "Computer: "

    // Dots size and spacing
    private var dotsDiameter: Float = 0f
    private var dotsSpacing: Float = 0f
    private var dotsSpacingRatio: Float = 0.1f
    
    /** sets the paint for the background */
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(255,255,255)
    }
    /** sets the paint for dots */
    private val dotsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(153,153,153)

        // Controls the size of the dot
        strokeWidth = 15f
        strokeCap = Paint.Cap.ROUND
    }
    /** painting the Human player's font/text/line color and setting text properties */
    private val paintHuman = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLUE

        //set text properties
        textAlign = Paint.Align.RIGHT
        textSize = 30f * resources.displayMetrics.density
        typeface = Typeface.DEFAULT_BOLD
    }
    /** painting the Computer Player's font/text/line color and setting text properties */
    private val paintComputer = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED

        //set text properties
        textAlign = Paint.Align.RIGHT
        textSize = 30f * resources.displayMetrics.density
        typeface = Typeface.DEFAULT_BOLD
    }

    // Player 1 line color
    private val player1Line: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLUE
    }
    // ComputerPlayer line color
    private val computerLine: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
    }

    // unknown line color
    private val unknownLine: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(200,200,200)
    }

    private var xDrawRange = 1..columns
    private var yDrawRange = 1..rows



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //When the amount of the dots changes, this function changes the view

        val diameterX = width / (columns + (columns + 1) * dotsSpacingRatio)
        val diameterY = height / (rows + (rows + 1) * dotsSpacingRatio)

        dotsDiameter = minOf(diameterX, diameterY)
        dotsSpacing = dotsDiameter * dotsSpacingRatio
    }


    @SuppressLint("WrongCall")
    override fun onDraw(canvas: Canvas) {

        // Background
        // Measure the size of the canvas, we could take into account padding here
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        val xSep: Float = (canvasWidth / (columns + 1))
        val ySep: Float = (canvasWidth / (rows + 1))

        // measures the size of the playable grid
        val boxWidth = dotsSpacing + ((dotsDiameter + dotsSpacing) * rows)
        val boxHeight = dotsSpacing + ((dotsDiameter + dotsSpacing) * columns)

        // player score
        var scoreHuman = game.getScores()[0]
        var scoreComputer = game.getScores()[1]

        val colX = dotsSpacing / 2 + ((dotsDiameter + dotsSpacing) * columns)
        val rowY = dotsSpacing / 2 + ((dotsDiameter + dotsSpacing) * rows)

        val nextColX = dotsSpacing / 2 + ((dotsDiameter + dotsSpacing) * (columns + 1))
        val nextRowY = dotsSpacing / 2 + ((dotsDiameter + dotsSpacing) * (rows + 1))

        /** sets the color of line according to the current player */
        var paint: Paint = when (game.currentPlayer) {
            human -> paintHuman
            computer -> paintComputer
            else -> unknownLine
        }

        //get half of the width and height to locate the centre of the screen
        val viewWidthHalf = canvasWidth / 2f
        val viewHeightHalf = canvasHeight / 2f

        // HUMAN text view height and width
        val humanTextViewHeight = viewHeightHalf / 0.7f
        val humanTextViewWidth = viewWidthHalf / 0.9f

        // COMPUTER text view height and width
        val computerTextViewHeight = viewHeightHalf / 0.75f
        val computerTextViewWidth = viewWidthHalf / 0.9f

        // draws the little boxes beside the scores
        canvas.drawRect(60f, computerTextViewHeight, 120f, 1245f, paintComputer)
        canvas.drawRect(60f, humanTextViewHeight, 120f, 1340f, paintHuman)

        // draws the text and the score
        canvas.drawText(humanText + "$scoreHuman", humanTextViewWidth, humanTextViewHeight, paintHuman)
        canvas.drawText(computerText + "$scoreComputer", computerTextViewWidth, computerTextViewHeight, paintComputer)

        for (x in 1 until columns) { // x axis
            for (y in 1 until rows + 1 ) {
                paint = unknownLine

                if (game.lines[x, (y * 2)].isDrawn) {
                    if (game.currentPlayer == human) {
                        paint = player1Line
                    }
                    else if (game.currentPlayer == computer) {
                        paint = computerLine
                    }
                }
                if (y != rows + 1) {
                    canvas.drawLine(x * xSep + 15, y * ySep, (x + 1) * xSep - 15, y * ySep, paint)
                }
            }
        }
        for (x in 1 .. columns) { // y axis lines
            for (y in 1 until rows) {
                paint = unknownLine

                if (game.lines[(x), (y * 2) + 1].isDrawn) { // checking if the line is drawn
                    if (game.currentPlayer == game.players[0]) {
                        paint = player1Line
                    } else if (game.currentPlayer == game.players[1]) {
                        paint = computerLine
                    }
                }
                if (y != rows + 1) {
                    canvas.drawLine(x * xSep, y * ySep + 15, x * xSep, (y + 1) * ySep - 15, paint)
                }
            }
        }

        // drawing dots
        for (col in xDrawRange) { // for loop is separated from above because dots are drawn above the lines
            for (row in yDrawRange){
                canvas.drawPoint(col*xSep, row*ySep, dotsPaint) // dots
            }
        }

        // checks the boxes for the owning player and sets the paint of box to color of corresponding player
        for (row in 0 until rows) {
            for (col in 0 until columns) {
                if (game.boxes[col, row].owningPlayer == human) {
                    canvas.drawRect(colX, rowY, nextColX, nextRowY, paintHuman)
                    scoreHuman += 1
                }
                else if (game.boxes[col, row].owningPlayer == computer) {
                    canvas.drawRect(colX, rowY, nextColX, nextRowY, paintComputer)
                    scoreComputer += 1
                }
            }
        }

        if (scoreHuman + scoreComputer == columns * rows && game.isFinished) { // adds the scores of each player and checks if equal to the amount of boxes
            if (scoreHuman > scoreComputer) {
                canvas.drawText("Player One Wins!", boxWidth / 2.5f, boxHeight * 1.35f, paintHuman)
            }
            else if (scoreComputer < scoreHuman) {
                canvas.drawText("Computer Wins!", boxWidth / 2.5f, boxHeight * 1.35f, paintComputer)
            }
            else {
                canvas.drawText("Draw", boxWidth / 2.5f, boxHeight * 1.35f, dotsPaint)
            }
        }
        super.onDraw(canvas)

        val  restartButton = findViewById<Button>(R.id.RestartButton)
        val message = "Game has been restarted!"

    }

    private val gestureDetector = GestureDetectorCompat(context, object:
        GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            try {

                val xCoord = e.x
                val yCoord = e.y

                println(xCoord)
                println(yCoord)

                val colTouched = ((xCoord - dotsSpacing) / (dotsSpacing + dotsDiameter)).toInt()
                val rowTouched = ((yCoord - dotsSpacing) / (dotsSpacing + dotsDiameter)).toInt()

                return if (colTouched in 0 until columns) { //0 until xDrawRange) {
                    if (rowTouched in 0 until rows) { //0 until rows) {

                        val lineToDraw = game.lines[colTouched, rowTouched]
                        println("Line to draw (${lineToDraw.lineX},${lineToDraw.lineY})")

                        if (!lineToDraw.isDrawn) {
                            game.StudentLine(lineToDraw.lineX, lineToDraw.lineY).drawLine()
                            Snackbar
                                .make(this@DotsAndLinesView, "Line to draw in column " + (lineToDraw.lineX + 1).toString() + " and in rows " + (lineToDraw.lineY).toString(), Snackbar.LENGTH_SHORT).show()
                        }
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            } catch (ex: IndexOutOfBoundsException) {
                Snackbar.make(this@DotsAndLinesView, "Click is out of bounds", Snackbar.LENGTH_LONG).show()
            }
            return true
        }
    })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }
}
