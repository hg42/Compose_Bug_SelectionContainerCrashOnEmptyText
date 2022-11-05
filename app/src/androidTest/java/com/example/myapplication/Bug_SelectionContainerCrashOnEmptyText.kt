package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class Bug_SelectionContainerCrashOnEmptyText {


    val duration = 3000L
    val durationLongPress = 1000L

    @Composable
    fun SelectableText() {
        val text = """
            |Line
            |Line start selecting here and swipe over the empty lines
            |Line or select a word and extend it over the empty lines
            |Line
            |
            |
            |
            |Line
            |Line
            |Line
            |Line
            """.trimMargin()
        Column {
            SelectionContainer {
                Column {
                    Text("simple")
                    Text(text = text)   // works
                }
            }
            SelectionContainer {
                Column {
                    Text("crash")
                    text.lines().forEach {
                        Text(text = it)
                    }
                }
            }
            SelectionContainer {
                Column {
                    Text("space")
                    text.lines().forEach {
                        // empty lines replaced by a space works
                        Text(text = if (it == "") " " else it)
                    }
                }
            }
        }
    }

    @Rule
    @JvmField
    var test: ComposeContentTestRule = createComposeRule()

    @Before
    fun setUp() {
        test.setContent { SelectableText() }
        test.onRoot().printToLog("root")
    }

    val clock get() = test.mainClock

    fun inRealTime(what: String? = null, duration: Long = 0, todo: () -> Unit) {
        clock.autoAdvance = false
        what?.let { Log.d("%%%%%%%%%%", it) }
        val startVirt = clock.currentTime
        val startReal = System.currentTimeMillis()

        todo()

        while (true) {
            val virt = clock.currentTime - startVirt
            val real = System.currentTimeMillis() - startReal
            Log.d("..........", "virt: $virt real: $real")
            if (virt > real)
                Thread.sleep(virt-real)
            else
                clock.advanceTimeByFrame()
            if ((virt > duration) and (real > duration))
                break
        }
        clock.autoAdvance = true
    }

    fun selectVertical(anchor: SemanticsNodeInteraction, parent: SemanticsNodeInteraction) {

        inRealTime("down(center)", durationLongPress) {
            anchor.performTouchInput {
                down(center)
            }
        }

        val nSteps = 100
        val timeStep = (duration-durationLongPress)/nSteps
        Log.d("----------", "timeStep = $timeStep")

        var step = Offset(1f,1f)
        parent.performTouchInput {
            step = (bottomCenter-topCenter)*0.8f/ nSteps.toFloat()
        }

        repeat(nSteps) {
            parent.performTouchInput {
                inRealTime("moveBy($step, $timeStep)", timeStep) {
                    moveBy(step)
                }
            }
        }

        parent.performTouchInput {
            inRealTime("up()") {
                up()
            }
        }
    }

    @Test
    fun works_simple() {
        val anchor = test.onNodeWithText("simple")
        val textNode = anchor.onParent()
        textNode.printToLog("simple")
        selectVertical(anchor, textNode)
    }

    @Test
    fun crash() {
        val anchor = test.onNodeWithText("crash")
        val textNode = anchor.onParent()
        textNode.printToLog("crash")
        selectVertical(anchor, textNode)
    }

    @Test
    fun works_space() {
        val anchor = test.onNodeWithText("space")
        val textNode = anchor.onParent()
        textNode.printToLog("space")
        selectVertical(anchor, textNode)
    }
}