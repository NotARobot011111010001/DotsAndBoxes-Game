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
import uk.ac.bournemouth.ap.dotsandboxeslib.*
import uk.ac.bournemouth.ap.lib.matrix.MutableMatrix


class DotsAndLinesView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    // private val rowCount = get() = ::StudentDotsBoxGame()

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
    private val backCol: Int = Color.rgb(222,222,222)

    /** Human player's text color - matches the line color  */
    private val humanTextCol: Int = Color.BLUE

    /** Computer player's text color - matches the line color */
    private val computerTextColor: Int = Color.RED

    /** sets the paint for the background */
    private val back_paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = backCol
    }
    /** painting the dots */
    private val dots_paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
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
        typeface = Typeface.MONOSPACE
    }
    // painting the Computer Player's font/text
    private val wordsPaintComputer = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = computerTextColor
        //set text properties
        textAlign = Paint.Align.RIGHT
        textSize = 30f * resources.displayMetrics.density
        typeface = Typeface.MONOSPACE
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

    //
    private var xSep: Float = 50f
    private var ySep: Float = 50f

    override fun onDraw(canvas: Canvas?) {

        // Background
        // Measure the size of the canvas, we could take into account padding here
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        // draws a rectangle which fills the whole screen with background color
        canvas?.drawRect(0f, 0f, canvasWidth, canvasHeight, back_paint)

        // Circle
        //get half of the width and height to locate the centre of the screen
        val viewWidthHalf = canvasWidth / 2f
        val viewHeightHalf = canvasHeight / 2f

        // text view height and width
        val humanTextViewHeight = viewHeightHalf / 6f
        val humanTextViewWidth = viewWidthHalf / 1.05f

        // text view height and width
        val computerTextViewHeight = viewHeightHalf / 4f
        val computerTextViewWidth = viewWidthHalf / 1.05f

        //get the radius as half of the width or height, whichever is smaller
        //subtract twenty so that it has some space around it
        val radius: Float = minOf(viewWidthHalf,viewHeightHalf) - 20

        //canvas?.drawCircle(viewWidthHalf, viewHeightHalf, radius, dots_paint)
        canvas?.drawText(humanText, humanTextViewWidth, humanTextViewHeight, wordsPaintHuman)
        canvas?.drawText(computerText, computerTextViewWidth, computerTextViewHeight, wordsPaintComputer)


        // drawing dots


        for (x in 1..10) {
            for (y in 1..20) {
                canvas?.drawPoint(x*xSep, y*ySep,  dots_paint)
            }
        }

        super.onDraw(canvas)
    }




}
