package com.example.parental_control_app.viewmodels


import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.activities.LoginActivity
import com.example.parental_control_app.activities.children.ChildrenMainActivity
import com.example.parental_control_app.activities.parent.ParentMainActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.repositories.users.UserMaturityLevel
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UserState
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import kotlin.random.Random


enum class SurveyAnswers(val weight: Int) {
    ZERO(weight = 0),
    STRONGLY_DISAGREE(weight = 1),
    DISAGREE(weight = 2),
    NEUTRAL(weight = 3),
    AGREE(weight = 4),
    STRONGLY_AGREE(weight = 5)
}

data class SurveyQuestion(
    val question: String,
    var answer: SurveyAnswers = SurveyAnswers.ZERO,
)

data class SurveyQuestionCollection(
    val questions : List<SurveyQuestion>
) {
    internal var totalNumber: Int = questions.size
    internal var totalAnswered: Int = questions.count { question -> question.answer != SurveyAnswers.ZERO }
}

data class PasswordBottomSheetState (
    val value: String,
    val showSheet: Boolean,
    val showPassword: Boolean,
    val hashedPassword: String,
)

data class StartupState(
    val user: UserState = UserState("", ""),
    val profiles: List<UserProfile> = emptyList(),
    val profileInput: UserProfile = UserProfile("","", ""),
    val passwordBottomSheet: PasswordBottomSheetState = PasswordBottomSheetState("", showSheet = false, showPassword = false, ""),
    val creatingProfile: Boolean,
    val questions: SurveyQuestionCollection,
    val answeringSurvey: Boolean,
)


class StartupViewModel(
    private val toastHelper: ToastHelper,
    private val activityStarterHelper: ActivityStarterHelper,
//    private val profileSignOutHelper: ProfileSignOutHelper,
    private val usersRepository: UsersRepository = UsersRepository(),
) : ViewModel() {

    private val _clickedProfile = mutableStateOf(UserProfile())
    private val auth = Firebase.auth
    private val _uiState = mutableStateOf(
        StartupState(
            profiles = emptyList(),
            profileInput = UserProfile(
                profileId = "",
                name = "",
                phoneNumber = "",
                password = "",
                userId = "",
                parent = true,
            ),
            creatingProfile = false,
            answeringSurvey = false,
            questions = SurveyQuestionCollection(questions = listOf(
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
        )
    )
    var googleSignOut : () -> Unit = {}

    val uiState : StartupState
        get() = _uiState.value

    private lateinit var sharedPreferences: SharedPreferences

    init {
        updateUser()
        updateProfiles()
    }

    private fun updateUser() {
        viewModelScope.launch {
            val authUser = auth.currentUser
            val user = usersRepository.findUser(authUser?.uid.toString())
            _uiState.value = _uiState.value.copy(user = user)
        }
    }

    private fun updateProfiles() {
        viewModelScope.launch {
            val authUser = auth.currentUser
            val profiles = usersRepository.findUserProfiles(authUser?.uid.toString())
            _uiState.value = _uiState.value.copy(profiles = profiles)
        }
    }

    fun setSharedPreferences(preferences: SharedPreferences) {
        sharedPreferences = preferences
    }

    fun signOut() {
        auth.signOut()
        googleSignOut()
        activityStarterHelper.startNewActivity(LoginActivity::class.java)
    }

    private fun includesParentAndChild(profiles: List<UserProfile>) : Boolean {
        return profiles.any{ profile -> profile.parent } && profiles.any { profile -> profile.child }
    }

    fun saveProfiles() {
        if (_uiState.value.profiles.isEmpty() || _uiState.value.profiles.size < 2) {
            toastHelper.makeToast("Create at least two profiles first, one parent, one child")
            return
        }

        if (!includesParentAndChild(_uiState.value.profiles)) {
            toastHelper.makeToast("Profiles should have at least 1 parent and 1 child")
            return
        }

        viewModelScope.launch(Dispatchers.IO){
            async {
                val msg = usersRepository.saveProfiles(_uiState.value.profiles)
                withContext(Dispatchers.Main) {
                    toastHelper.makeToast(msg)
                }
            }.await()
            async {
                val msgTwo = usersRepository.updateFirstSignIn(_uiState.value.user.userId)
                withContext(Dispatchers.Main) {
                    toastHelper.makeToast(msgTwo)
                }
            }.await()
            async {
                updateUser()
                updateProfiles()
            }.await()
        }
    }



    /**
     * Profile Creation Form related methods
     */
    fun onNameChange(newString: String) {
        _uiState.value = _uiState.value.copy(profileInput = _uiState.value.profileInput.copy(name = newString))
    }

    fun onPhoneNumberChange(newString: String) {
        _uiState.value = _uiState.value.copy(profileInput = _uiState.value.profileInput.copy(phoneNumber = newString))
    }

    fun onPasswordChange(newString: String) {
        _uiState.value = _uiState.value.copy(profileInput = _uiState.value.profileInput.copy(password = newString))
    }

    fun onAgeChange(newAge: String) {
        _uiState.value = _uiState.value.copy(profileInput = _uiState.value.profileInput.copy(age = newAge))
    }

    fun onBirthdayChange(newBirthday: Long) {
        _uiState.value = _uiState.value.copy(profileInput = _uiState.value.profileInput.copy(birthday = newBirthday))
    }

    fun startCreatingProfile() {
        _uiState.value = _uiState.value.copy(
            creatingProfile = true
        )
    }

    fun stopCreatingProfile() {
        _uiState.value = _uiState.value.copy(
            creatingProfile = false
        )
    }

    fun changeProfileType(state: Boolean) {
        if (state) {
            _uiState.value = _uiState.value.copy(
                profileInput = _uiState.value.profileInput.copy(
                    parent = true,
                    child = false,
                )
            )
        } else {
            _uiState.value = _uiState.value.copy(
                profileInput = _uiState.value.profileInput.copy(
                    parent = false,
                    child = true,
                )
            )
        }
    }


    /*
    Maturity Level Survey related methods
     */
    fun startAnsweringSurvey() {
        _uiState.value = _uiState.value.copy(
            answeringSurvey = true,
            creatingProfile = false,
        )
    }

    fun stopAnsweringSurvey() {
        _uiState.value = _uiState.value.copy(
            answeringSurvey = false,
            creatingProfile = true,
        )
    }

    fun onQuestionAnswerChange(index: Int, answer: SurveyAnswers) {
        _uiState.value = _uiState.value.copy(
            questions = _uiState.value.questions.copy(
                questions = _uiState.value.questions.questions.mapIndexed{ mapIndex, question ->
                    if (mapIndex == index)
                        question.copy(answer = answer)
                    else
                        question
                }
            )
        )
    }

    fun onClearAnswer(index: Int) {
        _uiState.value = _uiState.value.copy(
            questions = _uiState.value.questions.copy(
                questions = _uiState.value.questions.questions.mapIndexed{ mapIndex, question ->
                    if (mapIndex == index)
                        question.copy(answer = SurveyAnswers.ZERO)
                    else
                        question
                }
            )
        )
    }

    fun calculateSurveyAverage() {
        Log.w("Maturity Level", _uiState.value.questions.questions.toString())
        Log.w("Maturity Level", "Calculate Maturity Level")

        val random = Random.nextInt(0, 2)
        _uiState.value = _uiState.value.copy(
            profileInput = _uiState.value.profileInput.copy(
                maturityLevel = UserMaturityLevel.values()[random].toString()
            )
        )

        createProfile()
        stopAnsweringSurvey()
    }
    /*
    Maturity Level Survey related methods
     */


    fun createProfile() {
        if (_uiState.value.profileInput.name.isEmpty()
            || _uiState.value.profileInput.age.isEmpty()
            || _uiState.value.profileInput.birthday == 0L
            ) {
            toastHelper.makeToast("Make sure to fill up all the fields")
            return
        }

        if (_uiState.value.profileInput.parent && _uiState.value.profileInput.password.isEmpty()) {
            toastHelper.makeToast("Parent profile needs a password!")
            return
        }

        val profileId = _uiState.value.user.userId + _uiState.value.profileInput.name
        val plainPass = _uiState.value.profileInput.password
        val hashedPassword = BCrypt.hashpw(plainPass, BCrypt.gensalt())
        val hashedProfileId = BCrypt.hashpw(profileId, BCrypt.gensalt())

        _uiState.value = _uiState.value.copy(
            profileInput = _uiState.value.profileInput.copy(
                profileId = hashedProfileId,
                password = hashedPassword,
                userId = _uiState.value.user.userId,
            )
        )

        Log.w("USER PROFILE FORM", _uiState.value.profileInput.toString())

        _uiState.value = _uiState.value.copy(
            profiles = _uiState.value.profiles.plus(_uiState.value.profileInput),
            profileInput = UserProfile(
                profileId = "",
                name = "",
                userId = "",
                phoneNumber = "",
                password = "",
                parent = true,
            )
        )

//        stopCreatingProfile()
    }
    /**
     * Profile Creation Form related methods
     */


    fun setClickedProfile(profile: UserProfile) {
        _clickedProfile.value = profile
    }

    private fun setSharedPreferencesProfile() {
        val profile = _clickedProfile.value

        val editor = sharedPreferences.edit()
        editor.putString(SharedPreferencesManager.PROFILE_KEY, SharedPreferencesManager.createJsonString(profile))
        editor.apply()
        viewModelScope.launch {
            val newEditor = sharedPreferences.edit()
            val uid = usersRepository.getProfileUID(profile.profileId)
            newEditor.putString(SharedPreferencesManager.UID_KEY, uid)
            newEditor.apply()
        }
    }

    fun startChildActivity() {
        setSharedPreferencesProfile()
        activityStarterHelper.startNewActivity(ChildrenMainActivity::class.java)
    }



    /*
    Parent Profile related methods
    */
    fun onBottomSheetPasswordChange(string: String) {
        _uiState.value = _uiState.value.copy(
            passwordBottomSheet = _uiState.value.passwordBottomSheet.copy(
                value = string
            )
        )
    }

    fun onBottomSheetCheckboxChange(newState: Boolean) {
        _uiState.value = _uiState.value.copy(
            passwordBottomSheet = _uiState.value.passwordBottomSheet.copy(
                showPassword = newState
            )
        )
    }

    fun getParentPassword(hashedPassword: String) {
        _uiState.value = _uiState.value.copy(
            passwordBottomSheet = _uiState.value.passwordBottomSheet.copy(
                showSheet = true,
                hashedPassword = hashedPassword
            )
        )
    }

    fun stopShowingParentPassword() {
        _uiState.value = _uiState.value.copy(
            passwordBottomSheet = _uiState.value.passwordBottomSheet.copy(
                showSheet = false
            )
        )
    }

    fun checkParentPassword() {
        val passwordInput = _uiState.value.passwordBottomSheet.value
        val hashedPassword = _uiState.value.passwordBottomSheet.hashedPassword
        if (BCrypt.checkpw(passwordInput, hashedPassword)) startParentActivity()
        else toastHelper.makeToast("Incorrect password!")
    }

    private fun startParentActivity() {
        setSharedPreferencesProfile()
        activityStarterHelper.startNewActivity(ParentMainActivity::class.java)
    }
    /*
    Parent Profile related methods
    */

}