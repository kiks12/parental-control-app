package com.example.parental_control_app.viewmodels.manageProfile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.parental_control_app.repositories.users.UserMaturityLevel
import com.example.parental_control_app.viewmodels.SurveyAnswers
import com.example.parental_control_app.viewmodels.SurveyQuestion

class SurveyViewModel : ViewModel() {

    private val _questions = mutableStateOf(listOf(
        SurveyQuestion("First dummy example question"),
        SurveyQuestion("Second dummy example question"),
        SurveyQuestion("Third dummy example question"),
        SurveyQuestion("Fourth dummy example question"),
        SurveyQuestion("Fifth dummy example question"),
        SurveyQuestion("Sixth dummy example question"),
        SurveyQuestion("Seventh dummy example question"),
        SurveyQuestion("Eighth dummy example question"),
        SurveyQuestion("Ninth dummy example question"),
        SurveyQuestion("Tenth dummy example question"),
    ))
    val questions : List<SurveyQuestion>
        get() = _questions.value


    fun onQuestionAnswerChange(index: Int, answer: SurveyAnswers) {
        _questions.value = _questions.value.mapIndexed { mapIndex, question ->
            if (mapIndex == index) question.copy(answer = answer)
            else question
        }
    }

    fun onClearAnswer(index: Int) {
        _questions.value = _questions.value.mapIndexed { mapIndex, question ->
            if (mapIndex == index) question.copy(answer = SurveyAnswers.ZERO)
            else question
        }
    }

    fun getMaturityLevel() : String {
        return UserMaturityLevel.AVERAGE.toString()
    }
}