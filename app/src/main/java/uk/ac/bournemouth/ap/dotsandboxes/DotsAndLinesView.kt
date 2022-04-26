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
import androidx.core.view.GestureDetectorCompat
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

    private var rows = 10
    private var columns = 10

    // text for players
    private val humanText: String = "Human: "
    private val computerText: String = "Computer: "

    // scores count
    //private var humanScores: Int = getScores()

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
    /** painting the dots */
    private val dotsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        //set the paint color using the dots color specified
        color = dotsCol
        // Controls the size of the dot
        setStrokeWidth(15f)
        setStrokeCap(Paint.Cap.ROUND)
    }
    // painting the Human player's font/text
    private val wordsPaintHuman = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = humanTextCol
        //set text properties
        textAlign = Paint.Align.RIGHT
        textSize = 30f * resources.displayMetrics.density
        typeface = Typeface.DEFAULT_BOLD
    }
    // painting the Computer Player's font/text
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
    /* private val unknownLine2: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        //color = Color.RED
        color = Color.rgb(220,220,220)
    }*/

    // dots and Line separating x and y values
    private var xSep: Float = 100f
    private var ySep: Float = 100f

    val game: StudentDotsBoxGame = StudentDotsBoxGame(columns,rows, players = listOf(HumanPlayer(),
        HumanPlayer()
    ))

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        /*
        When the amount of the dots changes, this function changes the view
         */
        val diameterX = width/(columns + (columns+1)*dotsSpacingRatio)
        val diameterY = height/(rows + (rows+1)*dotsSpacingRatio)

        dotsDiameter = minOf(diameterX, diameterY)
        dotsSpacing = dotsDiameter*dotsSpacingRatio
    }


    override fun onDraw(canvas: Canvas?) {

        // Background
        // Measure the size of the canvas, we could take into account padding here
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        // draws a rectangle which fills the whole screen with background color
        canvas?.drawRect(0f, 0f, canvasWidth, canvasHeight, backPaint)

        // Circle
        //get half of the width and height to locate the centre of the screen
        val viewWidthHalf = canvasWidth / 2f
        val viewHeightHalf = canvasHeight / 2f

        // HUMAN text view height and width
        val humanTextViewHeight = viewHeightHalf / 0.8f
        val humanTextViewWidth = viewWidthHalf / 1.25f

        // COMPUTER text view height and width
        val computerTextViewHeight = viewHeightHalf / 0.75f
        val computerTextViewWidth = viewWidthHalf / 1.25f


        //canvas?.drawCircle(viewWidthHalf, viewHeightHalf, radius, dots_paint)
        canvas?.drawText(humanText, humanTextViewWidth, humanTextViewHeight, wordsPaintHuman)
        canvas?.drawText(computerText, computerTextViewWidth, computerTextViewHeight, wordsPaintComputer)

        val xDrawRange = 1..columns
        val yDrawRange = 1..rows

        val radius = dotsDiameter / 2f

        // drawing vertical lines
        for (col in xDrawRange) {
            for (row in yDrawRange) {
                canvas?.drawLine(row*xSep, col*ySep, row*ySep, row*xSep, unknownLine) // vertical lines
            }
        }

        // drawing horizontal lines
        for (col in xDrawRange) {
            for (row in yDrawRange) {
                canvas?.drawLine(row*xSep, col*ySep, col*ySep, col*ySep, unknownLine) // horizontal lines
            }
        }

        // drawing dots
        for (col in xDrawRange) { // for loop is separated from above because dots are drawn above the lines
            for (row in yDrawRange){
                canvas?.drawPoint(col*xSep, row*ySep, dotsPaint) // dots
            }
        }
        

        val scores = game.getScores().contentToString()


        super.onDraw(canvas)
    }
    private val gestureDetector = GestureDetectorCompat(context, object:
        GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            val lineTouched = ((e?.x?.minus(dotsSpacing * 0.5f))?.div((dotsSpacing + dotsDiameter)))?.toInt()

            if (lineTouched in 0 until columns) {
                game.lines
                invalidate()
                return true
            } else {
                return false
            }


        }


    })




}
