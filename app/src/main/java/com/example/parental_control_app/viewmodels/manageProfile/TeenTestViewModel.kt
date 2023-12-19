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

class TeenTestViewModel : ViewModel() {

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
            question = "You're at a party with friends. Someone offers you alcohol or drugs. What do you do?",
            options = listOf(
                NewSurveyOption(
                    index = 0,
                    letter = "A",
                    value = "Accept the offer and go along with the crowd, fearing social exclusion",
                    points = 1
                ),
                NewSurveyOption(
                    index = 1,
                    letter = "B",
                    value = "Decline politely but firmly, explaining your personal reasons for not participating",
                    points = 4
                ),
                NewSurveyOption(
                    index = 2,
                    letter = "C",
                    value = "Try to deflect the offer by suggesting an alternative activity or conversation topic",
                    points = 3
                ),
                NewSurveyOption(
                    index = 3,
                    letter = "D",
                    value = "Confide in a trusted friend at the party and seek their support in navigating the situation",
                    points = 3
                )
            )
        ),
        NewSurveyQuestion(
            question = "You're having an argument with your parents. How do you handle the situation? What do you do?",
            options = listOf(
                NewSurveyOption(
                    index = 0,
                    letter = "A",
                    value = "Resort to yelling and insults, escalating the conflict and damaging the communication",
                    points = 1
                ),
                NewSurveyOption(
                    index = 1,
                    letter = "B",
                    value = "Withdraw and shut down, refusing to engage in any further conversation",
                    points = 2
                ),
                NewSurveyOption(
                    index = 2,
                    letter = "C",
                    value = "Calmly state your perspective and try to understand their reasoning behind the curfew",
                    points = 4
                ),
                NewSurveyOption(
                    index = 3,
                    letter = "D",
                    value = "Propose a compromise that considers both your needs and your parents' concerns",
                    points = 3
                )
            )
        ),
        NewSurveyQuestion(
            question = "You're feeling overwhelmed with schoolwork and extracurricular activities. What do you do? What will you do?",
            options = listOf(
                NewSurveyOption(
                    index = 0,
                    letter = "A",
                    value = "Procrastinate and avoid tackling the tasks, hoping the pressure will eventually subside",
                    points = 1
                ),
                NewSurveyOption(
                    index = 1,
                    letter = "B",
                    value = "Seek help from teachers or classmates, seeking guidance and clarification on challenging assignments",
                    points = 3
                ),
                NewSurveyOption(
                    index = 2,
                    letter = "C",
                    value = "Create a detailed schedule and prioritize your tasks, allocating time efficiently for each commitment",
                    points = 4
                ),
                NewSurveyOption(
                    index = 3,
                    letter = "D",
                    value = "Delegate some tasks to others, such as asking a sibling to help with chores or forming study groups",
                    points = 3
                )
            )
        ),
        NewSurveyQuestion(
            question = "You see someone being bullied or harassed. What do you do?",
            options = listOf(
                NewSurveyOption(
                    index = 0,
                    letter = "A",
                    value = "Ignore the situation and walk away, fearing potential retaliation from the bullies",
                    points = 2
                ),
                NewSurveyOption(
                    index = 1,
                    letter = "B",
                    value = "Join in with the bullying or harassment, conforming to the group pressure",
                    points = 1
                ),
                NewSurveyOption(
                    index = 2,
                    letter = "C",
                    value = "Stand up for the victim and speak out against the bullying, demonstrating courage and empathy",
                    points = 4
                ),
                NewSurveyOption(
                    index = 3,
                    letter = "D",
                    value = "Discreetly report the incident to a teacher or other authority figure, ensuring the victim receives help",
                    points = 3
                )
            )
        ),
        NewSurveyQuestion(
            question = "You're faced with a difficult decision that could have serious consequences. What do you do?",
            options = listOf(
                NewSurveyOption(
                    index = 0,
                    letter = "A",
                    value = "Make a hasty decision based on impulse or immediate gratification, disregarding long-term consequences",
                    points = 1
                ),
                NewSurveyOption(
                    index = 1,
                    letter = "B",
                    value = "Seek advice from trusted adults or mentors, valuing their experience and guidance",
                    points = 3
                ),
                NewSurveyOption(
                    index = 2,
                    letter = "C",
                    value = "Conduct thorough research, gathering information about potential options and their implications",
                    points = 4
                ),
                NewSurveyOption(
                    index = 3,
                    letter = "D",
                    value = "Weigh the pros and cons of each option carefully, considering personal values and future goals",
                    points = 4
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
                        if (newSurveyOption.index == answerIndex) newSurveyOption.copy(isSelected = true)
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