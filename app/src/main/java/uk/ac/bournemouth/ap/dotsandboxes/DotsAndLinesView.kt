package uk.ac.bournemouth.ap.dotsandboxes

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.GestureDetectorCompat
import com.google.android.material.snackbar.Snackbar
import org.example.student.dotsboxgame.StudentDotsBoxGame
import uk.ac.bournemouth.ap.dotsandboxeslib.AbstractDotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.ComputerPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.HumanPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.Player


class DotsAndLinesView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    val game: StudentDotsBoxGame = StudentDotsBoxGame(10,10, players = listOf(HumanPlayer(), HumanPlayer()))

    val rows = game.rows
    val columns = game.columns

    // text for players
    private val humanText: String = "Human: "
    private val computerText: String = "Computer: "

    // Dots size and spacing
    private var dotsDiameter: Float = 0f
    private var dotsSpacing: Float = 0f
    private var dotsSpacingRatio: Float = 0.1f

    /** dots color */
    private val dotsCol: Int = Color.rgb(153,153,153)

    /** background color */
    private val backCol: Int = Color.rgb(230,230,230)

    /** Human player's text color - matches the line color  */
    private val humanTextCol: Int = Color.BLUE

    /** Computer player's text color - matches the line color */
    private val computerTextColor: Int = Color.RED

    /** sets the paint for the background */
    private val backPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = backCol
    }
    /** sets the paint for dots */
    private val dotsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        //set the paint color using the dots color specified
        color = dotsCol
        // Controls the size of the dot
        setStrokeWidth(15f)
        setStrokeCap(Paint.Cap.ROUND)
    }
    // painting the Human player's font/text and setting text properties
    private val wordsPaintHuman = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = humanTextCol
        //set text properties
        textAlign = Paint.Align.RIGHT
        textSize = 30f * resources.displayMetrics.density
        typeface = Typeface.DEFAULT_BOLD
    }
    // painting the Computer Player's font/text and setting text properties
    private val wordsPaintComputer = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = computerTextColor
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
        color = Color.rgb(210,210,210)
    }

    // dots and Line separating x and y values
    private var xSep: Float = 100f
    private var ySep: Float = 100f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        /*
        When the amount of the dots changes, this function changes the view
         */
        val diameterX = width / (columns + (columns + 1) * dotsSpacingRatio)
        val diameterY = height / (rows + (rows + 1) * dotsSpacingRatio)

        dotsDiameter = minOf(diameterX, diameterY)
        dotsSpacing = dotsDiameter * dotsSpacingRatio
    }

    val  restartButton = findViewById<Button>(R.id.RestartButton)

    // val restartimgae = getDrawable(R.drawable.ic_sharp_restart_24)

    override fun onDraw(canvas: Canvas) {

        // Background
        // Measure the size of the canvas, we could take into account padding here
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        // draws a rectangle which fills the whole screen with background color
        canvas.drawRect(0f, 0f, canvasWidth, canvasHeight, backPaint)


        //get half of the width and height to locate the centre of the screen
        val viewWidthHalf = canvasWidth / 2f
        val viewHeightHalf = canvasHeight / 2f

        // HUMAN text view height and width
        val humanTextViewHeight = viewHeightHalf / 0.7f
        val humanTextViewWidth = viewWidthHalf / 1.25f

        // COMPUTER text view height and width
        val computerTextViewHeight = viewHeightHalf / 0.75f
        val computerTextViewWidth = viewWidthHalf / 1.25f

        /** sets the color of line according to the current player */
        val paint  = when (game.players) {
            game.players[0] -> player1Line
            game.players[1] -> computerLine
            else -> unknownLine
        }

        //canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, dots_paint)
        canvas.drawText(humanText, humanTextViewWidth, humanTextViewHeight, wordsPaintHuman)
        canvas.drawText(computerText, computerTextViewWidth, computerTextViewHeight, wordsPaintComputer)

        val scoreHuman = game.getScores()[0].toString() // human player score
        val scoreComputer = game.getScores()[1].toString() // Computer player score

        canvas.drawText(scoreHuman, humanTextViewWidth * 1.1f, humanTextViewHeight, wordsPaintHuman)
        canvas.drawText(scoreComputer, computerTextViewWidth * 1.1f, computerTextViewHeight, wordsPaintComputer)


        val xDrawRange = 1..columns
        val yDrawRange = 1..rows

        //val radius = dotsDiameter / 2f

        // drawing vertical lines
        for (col in xDrawRange) {
            for (row in yDrawRange) {
                canvas.drawLine(row*xSep, col*ySep, row*ySep, row*xSep, paint) // vertical lines
            }
        }

        // drawing horizontal lines
        for (col in xDrawRange) {
            for (row in yDrawRange) {
                canvas.drawLine(row*xSep, col*ySep, col*ySep, col*ySep, paint) // horizontal lines
            }
        }

        // drawing dots
        for (col in xDrawRange) { // for loop is separated from above because dots are drawn above the lines
            for (row in yDrawRange){
                canvas.drawPoint(col*xSep, row*ySep, dotsPaint) // dots
            }
        }
        super.onDraw(canvas)
    }

    private val gestureDetector = GestureDetectorCompat(context, object:
        GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            val colTouched = ((e.x - (dotsSpacing + 1.5f)) / (dotsSpacing + dotsDiameter)).toInt()

            val rowTouched = ((e.y - dotsSpacing + 1.5f) / (dotsSpacing + dotsDiameter)).toInt()

            val xDrawRange = 0..columns
            val yDrawRange = 0..rows

            return if (colTouched in xDrawRange) {//0 until xDrawRange) {
                if (rowTouched in yDrawRange) {//0 until rows) {
                    game.StudentLine(rowTouched, colTouched)
                    Snackbar
                        .make(this@DotsAndLinesView, "Line drawn in column " + (colTouched +1).toString() + " and in row " + (rowTouched +1).toString(), Snackbar.LENGTH_LONG).show()
                    invalidate()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

})

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }
}
