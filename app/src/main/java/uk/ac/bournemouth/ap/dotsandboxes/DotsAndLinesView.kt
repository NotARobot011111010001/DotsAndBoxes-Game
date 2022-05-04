package uk.ac.bournemouth.ap.dotsandboxes

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
import androidx.core.view.GestureDetectorCompat
import com.google.android.material.snackbar.Snackbar
import org.example.student.dotsboxgame.StudentDotsBoxGame
import uk.ac.bournemouth.ap.dotsandboxeslib.HumanPlayer


class DotsAndLinesView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    val game: StudentDotsBoxGame = StudentDotsBoxGame(10,10, players = listOf(HumanPlayer(), HumanPlayer()))

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

    // unknown line color
    private val unknownLine: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(200,200,200)
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

    /*val  restartButton = findViewById<Button>(R.id.RestartButton)

    val restartimgae = getDrawable(R.drawable.ic_sharp_restart_24)*/

    override fun onDraw(canvas: Canvas) {

        // Background
        // Measure the size of the canvas, we could take into account padding here
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        // measures the size of the playable grid
        val gameWidth = dotsSpacing + ((dotsDiameter + dotsSpacing) * rows)
        val gameHeight = dotsSpacing + ((dotsDiameter + dotsSpacing) * columns)
        //canvas.drawRect(0f, 0f, gameWidth, gameHeight, backgroundPaint)

        val radius = dotsDiameter / 2f // setting the radius for dots

        var scoreHuman = game.getScores()[0]//.toString() // human player score
        var scoreComputer = game.getScores()[1]//.toString() // Computer player score

        val colX = dotsSpacing / 2 + ((dotsDiameter + dotsSpacing) * columns)
        val rowY = dotsSpacing / 2 + ((dotsDiameter + dotsSpacing) * rows)

        val nextColX = dotsSpacing / 2 + ((dotsDiameter + dotsSpacing) * (columns + 1))
        val nextRowY = dotsSpacing / 2 + ((dotsDiameter + dotsSpacing) * (rows + 1))
        val spacing = (dotsSpacing + dotsDiameter)

        /** sets the color of line according to the current player */
        val paint  = when (game.players) {
            game.players[0] -> paintHuman
            game.players[1] -> paintComputer
            else -> unknownLine
        }

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

        if (scoreHuman + scoreComputer == columns * rows) {
            if (scoreHuman > scoreComputer) {
                canvas.drawText("Player One Wins!", gameWidth / 2.5f, gameHeight * 1.35f, paintHuman)
            }
            else if (scoreComputer < scoreHuman) {
                canvas.drawText("Computer Wins!", gameWidth / 2.5f, gameHeight * 1.35f, paintComputer)
            }
            else {
                canvas.drawText("Draw", gameWidth / 2.5f, gameHeight * 1.35f, dotsPaint)
            }
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

        canvas.drawRect(60f, computerTextViewHeight, 120f, 1245f, paintComputer)
        canvas.drawRect(60f, humanTextViewHeight, 120f, 1340f, paintHuman)


        //canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, dots_paint)
        canvas.drawText(humanText + "$scoreHuman", humanTextViewWidth, humanTextViewHeight, paintHuman)
        canvas.drawText(computerText + "$scoreComputer", computerTextViewWidth, computerTextViewHeight, paintComputer)

        val xDrawRange = 1..columns
        val yDrawRange = 1..rows


        // drawing vertical lines
        for (col in xDrawRange) {
            for (rows in yDrawRange) {
                canvas.drawLine(rows*xSep, col*ySep, rows*ySep, rows*xSep, paint) // vertical lines
            }
        }

        // drawing horizontal lines
        for (col in xDrawRange) {
            for (rows in yDrawRange) {
                canvas.drawLine(rows*xSep, col*ySep, col*ySep, col*ySep, paint) // horizontal lines
            }
        }

        // drawing dots
        for (col in xDrawRange) { // for loop is separated from above because dots are drawn above the lines
            for (rows in yDrawRange){
                canvas.drawPoint(col*xSep, rows*ySep, dotsPaint) // dots
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
            val colTouched = ((e.x - dotsSpacing * 0.2f) / (dotsSpacing + dotsDiameter)).toInt()

            val rowTouched = ((e.y - dotsSpacing * 0.2f) / (dotsSpacing + dotsDiameter)).toInt()

            val xDrawRange = 0..columns
            val yDrawRange = 0..rows

            return if (colTouched in xDrawRange) { //0 until xDrawRange) {
                if (rowTouched in yDrawRange) { //0 until rows) {
                    game.StudentLine(rowTouched, colTouched)
                    Snackbar
                        .make(this@DotsAndLinesView, "Line drawn in column " + (colTouched +1).toString() + " and in rows " + (rowTouched +1).toString(), Snackbar.LENGTH_LONG).show()
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
