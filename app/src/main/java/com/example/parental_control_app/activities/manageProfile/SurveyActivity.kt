package com.example.parental_control_app.activities.manageProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.manageProfile.SurveyViewModel

class SurveyActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val surveyViewModel = SurveyViewModel()

        setContent {
            val pictureCompletionQuestions = surveyViewModel.pictureCompletionQuestions
            val patternRecognitionQuestions = surveyViewModel.patternRecognitionQuestions
            val vocabularyQuestions = surveyViewModel.vocabularyQuestions
            val logicalReasoningQuestions = surveyViewModel.logicalReasoningQuestions
            val loading = surveyViewModel.loading
            val score = surveyViewModel.score
            val maturityLevel = surveyViewModel.maturityLevel
            val submitted = surveyViewModel.submitted

            ParentalControlAppTheme {
                Scaffold (
                    topBar = {
                        TopAppBar(
                            title = { Text("Maturity Level Test")},
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
                                            intent.putExtra("ActivityResult", surveyViewModel.getMaturityLevel())
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
                                item { Text("0-4: Below Average") }
                                item { Text(" - Suggests areas for improvement and further development.") }
                                item { Spacer(Modifier.height(20.dp)) }
                                item { Text("5-6: Average") }
                                item { Text(" - Indicates a satisfactory level of cognitive abilities for the age.") }
                                item { Spacer(Modifier.height(20.dp)) }
                                item { Text("7-12: Above Average") }
                                item { Text(" - Suggests advanced cognitive abilities and problem-solving skills.") }
                                item { Spacer(Modifier.height(30.dp)) }
                                item { Text("It's important to note that this is a simplified guide scale, and any interpretation should be made in consultation with professionals who specialize in child development and education. IQ tests for children should be used as a tool to identify strengths and weaknesses, not as a definitive measure of a child's intelligence or potential.") }


                            }
                        } else {
                            LazyColumn(Modifier.padding(20.dp)) {
                                item {
                                    Text(
                                        "Section 1: Picture Completion",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(start = 10.dp),
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        "Look at the pictures and choose the best answer.",
                                        modifier = Modifier.padding(start = 10.dp),
                                    )
                                    Spacer(Modifier.height(10.dp))
                                }

                                itemsIndexed(pictureCompletionQuestions) { questionIndex, surveyQuestion ->
                                    Column(
                                        modifier = Modifier.padding(start = 10.dp, bottom = 30.dp, end = 10.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ){
                                        if (surveyQuestion.img != null) {
                                            Image(
                                                painter = painterResource(id = surveyQuestion.img),
                                                contentDescription = surveyQuestion.question,
                                                modifier = Modifier.size(250.dp),
                                            )
                                        }
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
                                                .height(200.dp)
                                                .padding(horizontal = 10.dp)
                                                .selectableGroup(),
                                        ){
                                            items(surveyQuestion.options) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    RadioButton(selected = it.isSelected, onClick = { surveyViewModel.onQuestionAnswerChange(questionIndex, it.index, "PICTURE_COMPLETION") })
                                                    Text(text="${it.letter}.) ${it.value}")
                                                }
                                            }
                                        }
                                        AnimatedVisibility(visible = surveyQuestion.options.any { option -> option.isSelected }) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                TextButton(onClick = { surveyViewModel.onClearAnswer(questionIndex, "PICTURE_COMPLETION")}) {
                                                    Text("Clear")
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Text(
                                        "Section 2: Pattern Recognition",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(start = 10.dp),
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        "Complete the pattern by choosing the correct option.",
                                        modifier = Modifier.padding(start = 10.dp),
                                    )
                                    Spacer(Modifier.height(10.dp))
                                }

                                itemsIndexed(patternRecognitionQuestions) { questionIndex, surveyQuestion ->
                                    Column(
                                        modifier = Modifier.padding(start = 10.dp, bottom = 30.dp, end = 10.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ){
                                        if (surveyQuestion.img != null) {
                                            Image(
                                                painter = painterResource(id = surveyQuestion.img),
                                                contentDescription = surveyQuestion.question,
                                                modifier = Modifier.size(250.dp),
                                            )
                                        }
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
                                                .height(200.dp)
                                                .padding(horizontal = 10.dp)
                                                .selectableGroup(),
                                        ){
                                            items(surveyQuestion.options) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    RadioButton(selected = it.isSelected, onClick = { surveyViewModel.onQuestionAnswerChange(questionIndex, it.index, "PATTERN_RECOGNITION") })
                                                    Text(text="${it.letter}.) ${it.value}")
                                                }
                                            }
                                        }
                                        AnimatedVisibility(visible = surveyQuestion.options.any { option -> option.isSelected }) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                TextButton(onClick = { surveyViewModel.onClearAnswer(questionIndex, "PATTERN_RECOGNITION")}) {
                                                    Text("Clear")
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Text(
                                        "Section 3: Vocabulary",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(start = 10.dp),
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        "Choose the word that is most similar in meaning to the given word.",
                                        modifier = Modifier.padding(start = 10.dp),
                                    )
                                    Spacer(Modifier.height(10.dp))
                                }

                                itemsIndexed(vocabularyQuestions) { questionIndex, surveyQuestion ->
                                    Column(
                                        modifier = Modifier.padding(start = 10.dp, bottom = 30.dp, end = 10.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ){
                                        if (surveyQuestion.img != null) {
                                            Image(
                                                painter = painterResource(id = surveyQuestion.img),
                                                contentDescription = surveyQuestion.question,
                                                modifier = Modifier.size(250.dp),
                                            )
                                        }
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
                                                .height(200.dp)
                                                .padding(horizontal = 10.dp)
                                                .selectableGroup(),
                                        ){
                                            items(surveyQuestion.options) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    RadioButton(selected = it.isSelected, onClick = { surveyViewModel.onQuestionAnswerChange(questionIndex, it.index, "VOCABULARY") })
                                                    Text(text="${it.letter}.) ${it.value}")
                                                }
                                            }
                                        }
                                        AnimatedVisibility(visible = surveyQuestion.options.any { option -> option.isSelected }) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                TextButton(onClick = { surveyViewModel.onClearAnswer(questionIndex, "VOCABULARY")}) {
                                                    Text("Clear")
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Text(
                                        "Section 4: Logical Reasoning",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(start = 10.dp),
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        "Answer the following questions based on the information provided.",
                                        modifier = Modifier.padding(start = 10.dp),
                                    )
                                    Spacer(Modifier.height(10.dp))
                                }

                                itemsIndexed(logicalReasoningQuestions) { questionIndex, surveyQuestion ->
                                    Column(
                                        modifier = Modifier.padding(start = 10.dp, bottom = 30.dp, end = 10.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ){
                                        if (surveyQuestion.img != null) {
                                            Image(
                                                painter = painterResource(id = surveyQuestion.img),
                                                contentDescription = surveyQuestion.question,
                                                modifier = Modifier.size(250.dp),
                                            )
                                        }
                                        Text(
                                            modifier= Modifier.fillMaxWidth(),
                                            text = surveyQuestion.question,
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .padding(horizontal = 10.dp)
                                                .selectableGroup(),
                                        ){
                                            items(surveyQuestion.options) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    RadioButton(selected = it.isSelected, onClick = { surveyViewModel.onQuestionAnswerChange(questionIndex, it.index, "LOGICAL_REASONING") })
                                                    Text(text="${it.letter}.) ${it.value}")
                                                }
                                            }
                                        }
                                        AnimatedVisibility(visible = surveyQuestion.options.any { option -> option.isSelected }) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                TextButton(onClick = { surveyViewModel.onClearAnswer(questionIndex, "LOGICAL_REASONING")}) {
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
                                            onClick = surveyViewModel::calculateMaturityLevel,
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