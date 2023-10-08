package com.example.parental_control_app.activities.manageProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.SurveyAnswers
import com.example.parental_control_app.viewmodels.manageProfile.SurveyViewModel

class SurveyActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val surveyViewModel = SurveyViewModel()

        setContent {
            val questions = surveyViewModel.questions

            ParentalControlAppTheme {
                Scaffold (
                    topBar = {
                        TopAppBar(
                            title = { Text("Maturity Level Survey")},
                            navigationIcon = {
                                IconButton(onClick = {
                                    setResult(RESULT_CANCELED)
                                    finish()
                                }) {
                                    Icon(Icons.Rounded.ArrowBack, "Go Back")
                                }
                            }
                        )
                    }
                ){ innerPadding ->
                    Surface(Modifier.padding(innerPadding)){
                        LazyColumn(Modifier.padding(20.dp)) {
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Maturity Level Test",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 10.dp),
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(15.dp)
                                    ){
                                        Text("1 - Strongly Disagree", fontSize = 12.sp, fontStyle = FontStyle.Italic)
                                        Text("2 - Disagree", fontSize = 12.sp, fontStyle = FontStyle.Italic)
                                        Text("3 - Neutral", fontSize = 12.sp, fontStyle = FontStyle.Italic)
                                        Text("4 - Agree", fontSize = 12.sp, fontStyle = FontStyle.Italic)
                                        Text("5 - Strongly Agree", fontSize = 12.sp, fontStyle = FontStyle.Italic)
                                    }
                                }
                                Spacer(modifier = Modifier.height(30.dp))
                            }

                            itemsIndexed(questions) { questionIndex, surveyQuestion ->
                                val answers = remember { SurveyAnswers.values().slice(1..5).toList() }

                                Column(
                                    modifier = Modifier.padding(start = 10.dp, bottom = 30.dp, end = 10.dp)
                                ){
                                    Text(surveyQuestion.question, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp)
                                            .selectableGroup(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        items(answers) {answer ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ){
                                                RadioButton(
                                                    selected = surveyQuestion.answer == answer,
                                                    onClick = { surveyViewModel.onQuestionAnswerChange(questionIndex, answer)}
                                                )
                                                Text(answer.weight.toString(), fontSize = 13.sp)
                                            }
                                        }
                                    }
                                    AnimatedVisibility(visible = surveyQuestion.answer != SurveyAnswers.ZERO) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(onClick = { surveyViewModel.onClearAnswer(questionIndex)}) {
                                                Text("Clear")
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(
                                    modifier = Modifier.padding(10.dp)
                                ){
                                    FilledTonalButton(
                                        onClick = {
                                            setResult(RESULT_CANCELED)
                                        },
                                        modifier = Modifier.fillMaxWidth(0.5f)
                                    ) {
                                        Text("Back")
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Button(
                                        onClick = {
                                            val intent = Intent()
                                            intent.putExtra("ActivityResult", surveyViewModel.getMaturityLevel())
                                            setResult(RESULT_OK, intent)
                                            finish()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Submit")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}