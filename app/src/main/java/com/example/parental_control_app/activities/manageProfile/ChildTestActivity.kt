package com.example.parental_control_app.activities.manageProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.manageProfile.ChildTestViewModel

class ChildTestActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ChildTestViewModel()

        setContent {
            val questions = viewModel.questions
            val loading = viewModel.loading
            val score = viewModel.score
            val maturityLevel = viewModel.maturityLevel
            val submitted = viewModel.submitted

            ParentalControlAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Maturity Level Test") },
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
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        if (loading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            if (submitted) {
                                LazyColumn(Modifier.padding(20.dp)) {
                                    item {
                                        Column(
                                            Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ){
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ){
                                                Text(
                                                    "Score",
                                                    fontSize = 30.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    score.toString(),
                                                    fontSize = 50.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Text(maturityLevel.toString())
                                        }
                                    }

                                    item {
                                        Spacer(Modifier.height(20.dp))
                                        Button(
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = {
                                                val intent = Intent()
                                                intent.putExtra("ActivityResult", viewModel.getMaturityLevel())
                                                setResult(RESULT_OK, intent)
                                                finish()
                                            }
                                        ) {
                                            Text("Finish")
                                        }
                                    }

                                    item { Spacer(Modifier.height(20.dp)) }
                                    item { Text("Guide Scale: ") }
                                    item { Spacer(Modifier.height(20.dp)) }
                                    item { Text("Above Average Maturity (16-20 points):") }
                                    item { Text("This indicates a well-developed sense of maturity for a child's age group, demonstrating responsible, considerate, and empathetic behavior") }
                                    item { Spacer(Modifier.height(20.dp)) }
                                    item { Text("Average Maturity (6-15 points):") }
                                    item { Text("This suggests a need for additional support and guidance to help the child develop essential skills for managing emotions, resolving conflicts, and interacting positively with others.") }
                                    item { Spacer(Modifier.height(20.dp)) }
                                    item { Text("Below Average Maturity (1-5 points)") }
                                    item { Text("This indicates a significant delay in developmental milestones and emphasizes the need for professional evaluation and intervention to support the child's emotional, social, and behavioral development.") }
                                    item { Spacer(Modifier.height(30.dp)) }
                                    item { Text("This Subjective Maturity Assessment Test is a one tool to identify your maturity but it cannot give you the overall maturity of a person.") }
                                    item { Spacer(Modifier.height(20.dp)) }
                                    item { Text("Consulting with a professional psychologist is advise if you want to know how really matured you are") }
                                    item { Spacer(Modifier.height(20.dp)) }
                                    item { Text("This scoring guide is a tool to facilitate evaluation and should not be used as the sole indicator of a teen's maturity level.") }
                                    item { Spacer(Modifier.height(20.dp)) }
                                    item { Text("Individual differences, cultural contexts, and developmental milestones should be considered when interpreting the results.") }
                                    item { Spacer(Modifier.height(20.dp)) }
                                    item { Text("The assessment should be used to identify areas for growth and support, not to label or judge a teen's maturity") }
                                }
                            } else {
                                LazyColumn(modifier = Modifier.padding(20.dp)) {
                                    item {
                                        Text("Behavioral/Situational Assessment Test for Kids(0-11 Yrs Old)", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    item {
                                        Text(
                                            modifier = Modifier.padding(vertical = 20.dp),
                                            fontSize = 13.sp,
                                            text = "This guide provides a more detailed scoring system for the Behavioral/Situational Assessment Test for Kids, assigning points to each response option along with a brief explanation. Remember, this is a subjective assessment, and the final interpretation should consider your child's age, individual circumstances, and developmental stage."
                                        )
                                    }

                                    itemsIndexed(questions) {questionIndex, surveyQuestion ->
                                        Column(
                                            modifier = Modifier.padding(start = 10.dp, bottom = 30.dp, end = 10.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ){
                                            Text(
                                                modifier= Modifier.fillMaxWidth(),
                                                text = surveyQuestion.question,
                                                fontSize = surveyQuestion.fontSize,
                                                fontWeight = surveyQuestion.fontWeight
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            LazyColumn(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(230.dp)
                                                    .selectableGroup(),
                                            ){
                                                items(surveyQuestion.options) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                    ) {
                                                        RadioButton(selected = it.isSelected, onClick = { viewModel.onQuestionAnswerChange(questionIndex, it.index) })
                                                        Text(
                                                            fontSize = 15.sp,
                                                            text="${it.letter}) ${it.value}"
                                                        )
                                                    }
                                                }
                                            }
                                            AnimatedVisibility(visible = surveyQuestion.options.any { option -> option.isSelected }) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.End
                                                ) {
                                                    TextButton(onClick = { viewModel.onClearAnswer(questionIndex)}) {
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
                                                onClick = viewModel::calculateScore,
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
    }
}