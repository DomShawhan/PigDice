package com.example.pigdice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pigdice.ui.theme.PigDiceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PigDiceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PigDiceLayout()
                }
            }
        }
    }
}

@Composable
fun PigDiceLayout( modifier: Modifier = Modifier) {
    var gamesPlayed: Play by remember { mutableStateOf(Play()) }
    var nbrOfGames: String by remember { mutableStateOf("0") }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.app_name),
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = modifier
                .fillMaxWidth()
                .background(color = Color.Blue)
                .padding(30.dp)
        )

        TextField(
            value = nbrOfGames,
            onValueChange = { nbrOfGames = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            label = {
                Text(
                    text = stringResource(R.string.input_label),
                    fontSize = 17.sp,
                    color = Color.Blue
                )
            },
            modifier = modifier.padding(top = 8.dp, bottom = 8.dp)
        )

        Button(onClick = { gamesPlayed = play(nbrOfGames.toIntOrNull() ?: 1) }) {
            Text(text = stringResource(R.string.play))
        }

        if(gamesPlayed.scores.isNotEmpty()) {
            DisplaySummary(play = gamesPlayed)
        }
    }
}

fun play(nbrOfGames: Int = 1): Play {
    val play = Play()

    for(i in 1..nbrOfGames) {
        var one = false
        val game = Game()
        while (!one) {
            val roll = (1..6).random()
            play.rolls++
            game.rolls++
            if(roll != 1) {
                game.score += roll
                when(roll) {
                    2 -> play.twos++
                    3 -> play.threes++
                    4 -> play.fours++
                    5 -> play.fives++
                    else -> play.sixes++
                }
            } else {
                play.ones++
                if(game.score > play.highScore) {
                    play.highScore = game.score
                }
                play.scores.add(game)
                one = true
            }
        }
    }

    return play
}

@Composable
fun DisplaySummary(play: Play) {
    var highRolls = 0
    var sumOfRolls = 0
    var sumOfScores = 0
    for(game: Game in play.scores) {
        if(game.rolls > highRolls){
            highRolls = game.rolls
        }
        sumOfRolls += game.rolls
        sumOfScores += game.score
    }

    val list = listOf(play.ones, play.twos, play.threes, play.fours, play.fives, play.sixes)
    val max = list.max()
    val min = list.min()

    val mostCommonNumber = when(max) {
        play.ones -> "1"
        play.twos -> "2"
        play.threes -> "3"
        play.fours -> "4"
        play.fives -> "5"
        else -> "6"
    }

    val leastCommonNumber = when(min) {
        play.ones -> "1"
        play.twos -> "2"
        play.threes -> "3"
        play.fours -> "4"
        play.fives -> "5"
        else -> "6"
    }

    Text(
        text = stringResource(R.string.summary_detail_header),
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Blue,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(top = 10.dp)
            .background(color = Color.LightGray)
            .fillMaxWidth()
    )
    TableRow {
        Cell(text = "High Score", 1)
        Cell(text = play.highScore.toString(), 2)
    }

    TableRow {
        Cell(text = "Number of Rolls", 1)
        Cell(text = play.rolls.toString(), 2)
    }

    TableRow {
        Cell(text = "Most Rolls in a game", 1)
        Cell(text = highRolls.toString(), 2)
    }

    TableRow {
        Cell(text = "Average Number of Rolls", 1)
        Cell(text = (sumOfRolls / play.scores.count()).toString(), 2)
    }

    TableRow {
        Cell(text = "Average Score", 1)
        Cell(text = (sumOfScores / play.scores.count()).toString(), 2)
    }

    TableRow {
        Cell(text = "Most Common Roll", 1)
        Cell(text = mostCommonNumber, 2)
    }

    TableRow {
        Cell(text = "Least Common Roll", 1)
        Cell(text = leastCommonNumber, 2)
    }
}

@Composable
fun Cell(
    text: String,
    column: Int,
    modifier: Modifier = Modifier
) {
    val width: Float = when(column) {
        1 -> .5f
        else -> 1f
    }

    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = Color.Blue,
        modifier = modifier
            .fillMaxWidth(width)
            .padding(8.dp)
    )
}

@Composable
fun TableRow(modifier: Modifier = Modifier, contents: @Composable ()-> Unit) {
    Row (
        modifier = modifier
            .background(color = Color.LightGray)
            .padding(3.dp)
            .border(BorderStroke(2.dp, Color.Blue))
            .padding(2.dp)
    ) {
        contents()
    }
}

class Play(var highScore: Int = 0, var scores: MutableList<Game> = mutableListOf()) {
    var rolls: Int = 0
    var ones: Int = 0
    var twos: Int = 0
    var threes: Int = 0
    var fours: Int = 0
    var fives: Int = 0
    var sixes: Int = 0
}

class Game(var score: Int = 0, var rolls: Int = 0)

@Preview(showBackground = true)
@Composable
fun PigDiceApp() {
    PigDiceTheme {
        PigDiceLayout()
    }
}