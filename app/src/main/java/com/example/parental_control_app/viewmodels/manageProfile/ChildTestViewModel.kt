package com.example.parental_control_app.viewmodels.manageProfile

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.repositories.users.UserMaturityLevel
import com.example.parental_control_app.viewmodels.NewSurveyOption
import com.example.parental_control_app.viewmodels.NewSurveyQuestion
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ChildTestViewModel : ViewModel() {

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

    private val _questionsState = mutableStateOf(listOf(
        NewSurveyQuestion(
            question = "You are playing with your favorite toys when your younger sibling tries to take one away. What do you do?",
            options = listOf(
                NewSurveyOption(
                    index = 0,
                    letter = "A",
                    value = "Give the toy to their sibling without resistance",
                    points = 4
                ),
                NewSurveyOption(
                    index = 1,
                    letter = "B",
                    value = "Become angry and try to grab the toy back",
                    points = 1
                ),
                NewSurveyOption(
                    index = 2,
                    letter = "C",
                    value = "Suggest an alternative toy for their sibling to play with",
                    points = 3
                ),
                NewSurveyOption(
                    index = 3,
                    letter = "D",
                    value = "Cry and run away with the toy",
                    points = 2
                )
            )
        ),
        NewSurveyQuestion(
            question = "You’re at school and forgot your lunchbox at home. What do you do?",
            options = listOf(
                NewSurveyOption(
                    index = 0,
                    letter = "A",
                    value = "Go hungry all day without asking for help",
                    points = 1
                ),
                NewSurveyOption(
                    index = 1,
                    letter = "B",
                    value = "Cry and worry about not having lunch",
                    points = 2
                ),
                NewSurveyOption(
                    index = 2,
                    letter = "C",
                    value = "Ask a teacher or classmate for help finding food",
                    points = 3
                ),
                NewSurveyOption(
                    index = 3,
                    letter = "D",
                    value = "Share their lunch with another child who also forgot theirs",
                    points = 4
                )
            )
        ),
        NewSurveyQuestion(
            question = "You accidentally spilled juice on your new clothes. What do you do?",
            options = listOf(
                NewSurveyOption(
                    index = 0,
                    letter = "A",
                    value = "Become frustrated and angry at themselves",
                    points = 2
                ),
                NewSurveyOption(
                    index = 1,
                    letter = "B",
                    value = "Blame someone else for the accident",
                    points = 1
                ),
                NewSurveyOption(
                    index = 2,
                    letter = "C",
                    value = "Try to clean up the mess and apologize for the mistake",
                    points = 3
                ),
                NewSurveyOption(
                    index = 3,
                    letter = "D",
                    value = "Ignore the spill and continue playing as if nothing happened",
                    points = 1
                )
            )
        ),
        NewSurveyQuestion(
            question = "You’re playing with a group of friends, but they feel left out and ignored you. What do you do?",
            options = listOf(
                NewSurveyOption(
                    index = 0,
                    letter = "A",
                    value = "Cry and withdraw from the group",
                    points = 2
                ),
                NewSurveyOption(
                    index = 1,
                    letter = "B",
                    value = "Demand attention and try to force their way into the game",
                    points = 1
                ),
                NewSurveyOption(
                    index = 2,
                    letter = "C",
                    value = "Ask a friend to join them in a different activity",
                    points = 3
                ),
                NewSurveyOption(
                    index = 3,
                    letter = "D",
                    value = "Observe the group and wait for an opportunity to join in",
                    points = 4
                )
            )
        ),
        NewSurveyQuestion(
            question = "You have a special treat and your friend asks them to share. What do you do?",
            options = listOf(
                NewSurveyOption(
                    index = 0,
                    letter = "A",
                    value = "Refuse to share and keep the treat for themselves",
                    points = 1
                ),
                NewSurveyOption(
                    index = 1,
                    letter = "B",
                    value = "Share a small portion of the treat with their friend",
                    points = 2
                ),
                NewSurveyOption(
                    index = 2,
                    letter = "C",
                    value = "Offer their friend the whole treat and let them enjoy it",
                    points = 4
                ),
                NewSurveyOption(
                    index = 3,
                    letter = "D",
                    value = "Ask their friend to share their treat with them as well",
                    points = 3
                )
            )
        ),
    ))

    val questions : List<NewSurveyQuestion>
        get() = _questionsState.value

    fun onQuestionAnswerChange(questionIndex: Int, answerIndex: Int) {
        _questionsState.value = _questionsState.value.mapIndexed { index, newSurveyQuestion ->
            if (index == questionIndex) {
                newSurveyQuestion.copy(
                    options = newSurveyQuestion.options.map{ newSurveyOption ->
                        if(newSurveyOption.index == answerIndex) newSurveyOption.copy(isSelected = true)
                        else newSurveyOption.copy(isSelected = false)
                    }
                )
            } else newSurveyQuestion
        }
    }

    fun onClearAnswer(questionIndex: Int) {
        _questionsState.value = _questionsState.value.mapIndexed{ mapIndex, surveyQuestion ->
            if (mapIndex == questionIndex) {
                surveyQuestion.copy(
                    options= surveyQuestion.options.map { surveyOption -> surveyOption.copy(isSelected = false) },
                )
            } else surveyQuestion
        }
    }

    fun calculateScore() {
        var total = 0
        viewModelScope.launch {
            async { _loading.value = true }.await()
            async {
                _questionsState.value.forEach { newSurveyQuestion ->
                    val selected = newSurveyQuestion.options.filter { it.isSelected }
                    if (selected.isNotEmpty()) {
                        total += selected[0].points
                    }
                }
            }.await()
            async {
                if (total in 0..5) _maturityLevel.value = UserMaturityLevel.BELOW_AVERAGE
                if (total in 6..15) _maturityLevel.value = UserMaturityLevel.AVERAGE
                if (total in 16..20) _maturityLevel.value = UserMaturityLevel.ABOVE_AVERAGE
            }.await()
            async {
                _submitted.value = true
                _loading.value = false
                _score.intValue = total
            }.await()
        }
    }

    fun getMaturityLevel() : String {
        return _maturityLevel.value.toString()
    }
}