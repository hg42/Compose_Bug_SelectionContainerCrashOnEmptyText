package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SelectableText()
                }
            }
        }
    }
}

@Composable
fun SelectableText() {
    val text = """
            |
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
            """.trimMargin()
    Column {
        Text("--------------------works-------------------------")
        SelectionContainer {
            Text(text = text)   // works
        }
        Text("--------------------crashes-----------------------")
        SelectionContainer {
            Column {
                text.lines().forEach {
                    Text(text = it)
                }
            }
        }
        Text("--------------------works-------------------------")
        SelectionContainer {
            Column {
                text.lines().forEach {
                    // empty lines replaced by a space works
                    Text(text = if (it == "") " " else it)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        SelectableText()
    }
}