package com.example.parental_control_app.viewmodels.manageProfile

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.R
import com.example.parental_control_app.repositories.users.UserMaturityLevel
import com.example.parental_control_app.viewmodels.SurveyAnswer
import com.example.parental_control_app.viewmodels.SurveyOption
import com.example.parental_control_app.viewmodels.SurveyQuestion
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SurveyViewModel : ViewModel() {

    private val _score = mutableIntStateOf(0)
    private val _loading = mutableStateOf(false)
    private val _submitted = mutableStateOf(false)
    private val _maturityLevel = mutableStateOf(UserMaturityLevel.AVERAGE)
    val loading: Boolean
        get() = _loading.value
    val submitted: Boolean
        get() = _submitted.value
    val maturityLevel : UserMaturityLevel
        get() = _maturityLevel.value
    val score : Int
        get() = _score.intValue

    private val _pictureCompletionQuestions = mutableStateOf(listOf(
        SurveyQuestion(
            question = "",
            options = listOf(
                SurveyOption(0, "A", "Circle"),
                SurveyOption(1, "B", "Square"),
                SurveyOption(2, "C", "Triangle"),
                SurveyOption(3, "D", "Star"),
            ),
            answer = SurveyAnswer(0, "Circle"),
            img = R.mipmap.question_1_foreground,
        ),
        SurveyQuestion(
            question = "",
            options = listOf(
                SurveyOption(0, "A", "Sun"),
                SurveyOption(1, "B", "Moon"),
                SurveyOption(2, "C", "Cloud"),
                SurveyOption(3, "D", "Tree"),
            ),
            answer = SurveyAnswer(0, "Sun"),
            img = R.mipmap.question_2_foreground,
        ),
        SurveyQuestion(
            question = "",
            options = listOf(
                SurveyOption(0, "A", "Car"),
                SurveyOption(1, "B", "Boat"),
                SurveyOption(2, "C", "Plane"),
                SurveyOption(3, "D", "Bicycle"),
            ),
            answer = SurveyAnswer(0, "Car"),
            img = R.mipmap.question_3_foreground,
        ),
    ))

    private val _patternRecognitionQuestions = mutableStateOf(listOf(
        SurveyQuestion(
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            question = "A,B,C,D,_,F,G",
            options = listOf(
                SurveyOption(0, "A", "H"),
                SurveyOption(1, "B", "I"),
                SurveyOption(2, "C", "J"),
                SurveyOption(3, "D", "E"),
            ),
            answer = SurveyAnswer(3, "E")
        ),
        SurveyQuestion(
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            question = "1,2,3,_,5,6,7",
            options = listOf(
                SurveyOption(0, "A", "8"),
                SurveyOption(1, "B", "4"),
                SurveyOption(2, "C", "11"),
                SurveyOption(3, "D", "14"),
            ),
            answer = SurveyAnswer(1, "4")
        ),
        SurveyQuestion(
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            question = "2,4,6,_,10,12",
            options = listOf(
                SurveyOption(0, "A", "8"),
                SurveyOption(1, "B", "9"),
                SurveyOption(2, "C", "11"),
                SurveyOption(3, "D", "14"),
            ),
            answer = SurveyAnswer(0, "8")
        ),
    ))

    private val _vocabularyQuestions = mutableStateOf(listOf(
        SurveyQuestion(
            question = "",
            options = listOf(
                SurveyOption(0, "A", "Sad"),
                SurveyOption(1, "B", "Angry"),
                SurveyOption(2, "C", "Excited"),
                SurveyOption(3, "D", "Tired"),
            ),
            answer = SurveyAnswer(2, "Excited"),
            img = R.mipmap.question_5_foreground
        ),
        SurveyQuestion(
            question = "",
            options = listOf(
                SurveyOption(0, "A", "Slow"),
                SurveyOption(1, "B", "Big"),
                SurveyOption(2, "C", "Small"),
                SurveyOption(3, "D", "Quick"),
            ),
            answer = SurveyAnswer(3, "Quick"),
            img = R.mipmap.question_6_foreground
        ),
        SurveyQuestion(
            question = "",
            options = listOf(
                SurveyOption(0, "A", "Fear"),
                SurveyOption(1, "B", "Intelligent"),
                SurveyOption(2, "C", "Small"),
                SurveyOption(3, "D", "Pleased"),
            ),
            answer = SurveyAnswer(1, "Intelligent"),
            img = R.mipmap.question_4_foreground
        )
    ))

    private val _logicalReasoningQuestions = mutableStateOf(listOf(
        SurveyQuestion(
            fontWeight = FontWeight.Normal,
            question = "if all birds can fly and Tweety is a bird, can Tweety fly?",
            options = listOf(
                SurveyOption(0, "A", "Yes"),
                SurveyOption(1, "B", "No"),
            ),
            answer = SurveyAnswer(0, "Yes")
        ),
        SurveyQuestion(
            fontWeight = FontWeight.Normal,
            question = "if it is raining outside what do you need to use?",
            options = listOf(
                SurveyOption(0, "A", "Umbrella"),
                SurveyOption(1, "B", "Sunglasses"),
                SurveyOption(2, "C", "Hat"),
                SurveyOption(3, "D", "Gloves"),
            ),
            answer = SurveyAnswer(0, "Umbrella")
        ),
        SurveyQuestion(
            fontWeight = FontWeight.Normal,
            question = "3 + 4 = ?",
            options = listOf(
                SurveyOption(0, "A", "9"),
                SurveyOption(1, "B", "5"),
                SurveyOption(2, "C", "7"),
                SurveyOption(3, "D", "6"),
            ),
            answer = SurveyAnswer(2, "7")
        ),
    ))

    val pictureCompletionQuestions : List<SurveyQuestion>
        get() = _pictureCompletionQuestions.value
    val patternRecognitionQuestions: List<SurveyQuestion>
        get() = _patternRecognitionQuestions.value
    val vocabularyQuestions: List<SurveyQuestion>
        get() = _vocabularyQuestions.value
    val logicalReasoningQuestions: List<SurveyQuestion>
        get() = _logicalReasoningQuestions.value



    fun onQuestionAnswerChange(questionIndex: Int, optionIndex: Int, type: String) {
        when(type) {
            "PICTURE_COMPLETION" -> {
                _pictureCompletionQuestions.value = _pictureCompletionQuestions.value.mapIndexed { mapIndex, surveyQuestion ->
                    if (mapIndex == questionIndex) {
                        surveyQuestion.copy(
                            options = surveyQuestion.options.map { surveyOption ->
                                if (surveyOption.index == optionIndex) surveyOption.copy(isSelected = true)
                                else surveyOption.copy(isSelected = false)
                            },
                            selectedAnswer = SurveyAnswer(optionIndex, "")
                        )
                    } else surveyQuestion
                }
            }
            "PATTERN_RECOGNITION" -> {
                _patternRecognitionQuestions.value = _patternRecognitionQuestions.value.mapIndexed { mapIndex, surveyQuestion ->
                    if (mapIndex == questionIndex) {
                        surveyQuestion.copy(
                            options = surveyQuestion.options.map { surveyOption ->
                                if (surveyOption.index == optionIndex) surveyOption.copy(isSelected = true)
                                else surveyOption.copy(isSelected = false)
                            },
                            selectedAnswer = SurveyAnswer(optionIndex, "")
                        )
                    } else surveyQuestion
                }
            }
            "VOCABULARY" -> {
                _vocabularyQuestions.value = _vocabularyQuestions.value.mapIndexed { mapIndex, surveyQuestion ->
                    if (mapIndex == questionIndex) {
                        surveyQuestion.copy(
                            options = surveyQuestion.options.map { surveyOption ->
                                if (surveyOption.index == optionIndex) surveyOption.copy(isSelected = true)
                                else surveyOption.copy(isSelected = false)
                            },
                            selectedAnswer = SurveyAnswer(optionIndex, "")
                        )
                    } else surveyQuestion
                }
            }
            "LOGICAL_REASONING" -> {
                _logicalReasoningQuestions.value = _logicalReasoningQuestions.value.mapIndexed { mapIndex, surveyQuestion ->
                    if (mapIndex == questionIndex) {
                        surveyQuestion.copy(
                            options = surveyQuestion.options.map { surveyOption ->
                                if (surveyOption.index == optionIndex) surveyOption.copy(isSelected = true)
                                else surveyOption.copy(isSelected = false)
                            },
                            selectedAnswer = SurveyAnswer(optionIndex, "")
                        )
                    } else surveyQuestion
                }
            }
        }

    }

    fun onClearAnswer(questionIndex: Int, type: String) {
        when(type) {
            "PICTURE_COMPLETION" -> {
                _pictureCompletionQuestions.value = _pictureCompletionQuestions.value.mapIndexed{ mapIndex, surveyQuestion ->
                    if (mapIndex == questionIndex) {
                        surveyQuestion.copy(
                            options= surveyQuestion.options.map { surveyOption -> surveyOption.copy(isSelected = false) },
                            selectedAnswer = null
                        )
                    } else surveyQuestion
                }
            }
            "PATTERN_RECOGNITION" -> {
                _patternRecognitionQuestions.value = _patternRecognitionQuestions.value.mapIndexed{ mapIndex, surveyQuestion ->
                    if (mapIndex == questionIndex) {
                        surveyQuestion.copy(
                            options= surveyQuestion.options.map { surveyOption -> surveyOption.copy(isSelected = false) },
                            selectedAnswer = null
                        )
                    } else surveyQuestion
                }
            }
            "VOCABULARY" -> {
                _vocabularyQuestions.value = _vocabularyQuestions.value.mapIndexed{ mapIndex, surveyQuestion ->
                    if (mapIndex == questionIndex) {
                        surveyQuestion.copy(
                            options= surveyQuestion.options.map { surveyOption -> surveyOption.copy(isSelected = false) },
                            selectedAnswer = null
                        )
                    } else surveyQuestion
                }
            }
            "LOGICAL_REASONING" -> {
                _logicalReasoningQuestions.value = _logicalReasoningQuestions.value.mapIndexed{ mapIndex, surveyQuestion ->
                    if (mapIndex == questionIndex) {
                        surveyQuestion.copy(
                            options= surveyQuestion.options.map { surveyOption -> surveyOption.copy(isSelected = false) },
                            selectedAnswer = null
                        )
                    } else surveyQuestion
                }
            }
        }

    }

    fun calculateMaturityLevel() {
        viewModelScope.launch {
            var total = 0
            async { _loading.value = true }.await()
            async {
                _pictureCompletionQuestions.value.forEach { question ->
                    val selected = question.options.filter { option -> option.isSelected }
                    if (selected.isNotEmpty()) {
                        if (selected[0].index == question.answer.index) total += 1
                    }
                }
                _patternRecognitionQuestions.value.forEach { question ->
                    val selected = question.options.filter { option -> option.isSelected }
                    if (selected.isNotEmpty()) {
                        if (selected[0].index == question.answer.index) total += 1
                    }
                }
                _vocabularyQuestions.value.forEach { question ->
                    val selected = question.options.filter { option -> option.isSelected }
                    if (selected.isNotEmpty()) {
                        if (selected[0].index == question.answer.index) total += 1
                    }
                }
                _logicalReasoningQuestions.value.forEach { question ->
                    val selected = question.options.filter { option -> option.isSelected }
                    if (selected.isNotEmpty()) {
                        if (selected[0].index == question.answer.index) total += 1
                    }
                }
            }.await()
            async {
                if (total in 0..4) _maturityLevel.value = UserMaturityLevel.BELOW_AVERAGE
                if (total in 5..6) _maturityLevel.value = UserMaturityLevel.AVERAGE
                if (total in 7..12) _maturityLevel.value = UserMaturityLevel.ABOVE_AVERAGE
            }.await()
            async {
                _loading.value = false
                _submitted.value = true
                _score.intValue = total
            }.await()
        }
    }

    fun getMaturityLevel() : String {
        return _maturityLevel.value.toString()
    }
}