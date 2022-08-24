package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.udacity.R.styleable.LoadingButton_backgroundColorButton
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private lateinit var buttonText: String
    private var ColorPrimaryBackground = context.getColor(R.color.colorPrimary)
    private var ColorPrimaryTxt = context.getColor(R.color.white)
    private var ColorsAccentyellow = context.getColor(R.color.colorAccent)
    private var ColorInprogressDarkBlue = context.getColor(R.color.colorPrimaryDark)

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        // Set the button text depending on the state of the button
        buttonText = context.getString(buttonState.customTextButton)

        when(new) {

            ButtonState.Loading -> {
                if (old != ButtonState.Loading) {
                    SettingColorBacgroundOfButton(ColorPrimaryBackground)
                    settingAnimation()
                }
            }

            ButtonState.Completed -> {
                settingProgressOfButton(1f)
                SettingColorTextOfButton(ColorPrimaryTxt)

            }
        }
        invalidate()
        requestLayout()
    }

    private var ValusAnim = ValueAnimator()
    private var InProgress: Float = 0f
    private var EndOfProgress: Float = 0f

    init {
        buttonState=ButtonState.Clicked
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            defStyleAttr,
            0
        ).apply {

            try {
                ColorPrimaryBackground =getColor(LoadingButton_backgroundColorButton,ColorPrimaryBackground)
                ColorPrimaryTxt =getColor(R.styleable.LoadingButton_textColor,ColorPrimaryTxt)
                ColorInprogressDarkBlue = getColor(R.styleable.LoadingButton_colorOfInProgressBackground, ColorInprogressDarkBlue)
                ColorsAccentyellow = getColor(R.styleable.LoadingButton_colorOfCircleProgress, ColorsAccentyellow)
            }finally {
                recycle()
            }

        }

    }



    private var paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color=ColorPrimaryBackground

    }

    private fun SettingColorBacgroundOfButton(colorPrimaryBackground: Int) {
        this.paintButton.color =colorPrimaryBackground

    }


    private var paintLoadingButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorPrimaryDark)
        isAntiAlias = true
    }
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style =Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 41.0f
        color=ColorPrimaryTxt


        typeface = Typeface.create("", Typeface.BOLD)

    }
    private fun SettingColorTextOfButton(colorPrimaryText: Int) {

        this.paintText.color =colorPrimaryText
    }

    private val colorOfBackgroundInProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ColorInprogressDarkBlue
    }

    private val colorCurveInprogress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ColorsAccentyellow
        style = Paint.Style.FILL
    }


    private var paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorAccent)
        isAntiAlias = true
    }






    fun settingProgressOfButton(floatProgress: Float) {
        if (InProgress < floatProgress){

            EndOfProgress = floatProgress
            settingAnimation()
        }
    }


    fun AddingButtonProgress(floatProgress: Float) {

        settingProgressOfButton(InProgress + floatProgress)

    }

    private val rectangleLoading = Rect()
    private val rectangleTxt = Rect()
    private val rectangleCurve = RectF()
    private val pathAngle = Path()
    private val radiusAngle = 50f
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            save()
            clipPath(pathAngle)
            drawColor(paintButton.color)
            paintText.getTextBounds(buttonText, 0, buttonText.length, rectangleTxt)
            val xText = width / 2f - rectangleTxt.width() / 2f
            val yText = height / 2f + rectangleTxt.height() / 2f - rectangleTxt.bottom
            var offsetText = 0

            rectangleLoading.set(0, 0, (width * InProgress).roundToInt(), height)
            drawRect(rectangleLoading, colorOfBackgroundInProgress)

            if (buttonState == ButtonState.Loading) {
                val startOfCurveX = width / 2f + rectangleTxt.width() / 2f
                val startOfCurveY = height / 2f - 19
                rectangleCurve.set(startOfCurveX, startOfCurveY, startOfCurveX + 38, startOfCurveY + 38)
                drawArc(
                    rectangleCurve, InProgress, InProgress * 360f, true, paintCircle
                )
                offsetText = 36
            }

            drawText(buttonText, xText - offsetText, yText, paintText)
            restore()
        }
    }



    override fun onSizeChanged(width: Int, height: Int, widthOld: Int, heightOld: Int) {
        super.onSizeChanged(width, height, widthOld, heightOld)
        pathAngle.reset()
        pathAngle.addRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            radiusAngle,
            radiusAngle,
            Path.Direction.CW
        )
        pathAngle.close()
    }

    private var widSize = 0
    private var heitSize = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widSize = w
        heitSize = h
        setMeasuredDimension(w, h)
    }

    private fun ResetProgress() {
        EndOfProgress = 0f
        settingAnimation()
    }


    private fun settingAnimation() {
        ValusAnim.cancel()
        ValusAnim = ValueAnimator.ofFloat(InProgress, EndOfProgress).apply {
            interpolator = AccelerateDecelerateInterpolator()
            val AnimDuration = abs(1500 * ((EndOfProgress - InProgress) / 100)).toLong()
            duration = if (AnimDuration >= 400){
                AnimDuration
            }else{
                400
            }
            addUpdateListener { animation ->
                InProgress = animation.animatedValue as Float
                postInvalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (InProgress == 1f){


                        ResetProgress()
                    }else{
                        ValusAnim.cancel()
                    }
                }
            })
            start()
        }
    }
}