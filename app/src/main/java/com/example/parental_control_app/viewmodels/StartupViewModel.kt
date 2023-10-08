package com.example.parental_control_app.viewmodels


import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.activities.LoginActivity
import com.example.parental_control_app.activities.children.ChildrenMainActivity
import com.example.parental_control_app.activities.manageProfile.CreateProfileActivity
import com.example.parental_control_app.activities.parent.ParentMainActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.helpers.ResultLauncherHelper
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
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

data class PasswordBottomSheetState (
    val value: String,
    val showSheet: Boolean,
    val showPassword: Boolean,
    val hashedPassword: String,
)

data class StartupState(
    val user: UserState = UserState("", ""),
    val passwordBottomSheet: PasswordBottomSheetState = PasswordBottomSheetState("", showSheet = false, showPassword = false, ""),
)


class StartupViewModel(
    private val toastHelper: ToastHelper,
    private val activityStarterHelper: ActivityStarterHelper,
    private val resultLauncherHelper: ResultLauncherHelper,
    private val usersRepository: UsersRepository = UsersRepository(),
) : ViewModel() {

    private lateinit var sharedPreferences: SharedPreferences
    private val _clickedProfile = mutableStateOf(UserProfile())
    private val auth = Firebase.auth

    private val _uiState = mutableStateOf(StartupState())

    private val _profilesState = mutableStateOf<List<UserProfile>>(listOf())
    val profilesState : List<UserProfile>
        get() = _profilesState.value

    var googleSignOut : () -> Unit = {}

    val uiState : StartupState
        get() = _uiState.value


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
            _profilesState.value = profiles
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
        if (_profilesState.value.isEmpty() || _profilesState.value.size < 2) {
            toastHelper.makeToast("Create at least two profiles first")
            return
        }

        if (!includesParentAndChild(_profilesState.value)) {
            toastHelper.makeToast("Profiles should have at least 1 parent and 1 child")
            return
        }

        viewModelScope.launch {
            async {
                val msg = usersRepository.saveProfiles(_profilesState.value)
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

    fun addProfile(newProfile: UserProfile) {
        val profileId = _uiState.value.user.userId + newProfile.name
        val plainPass = newProfile.password
        val hashedPassword = BCrypt.hashpw(plainPass, BCrypt.gensalt())
        val hashedProfileId = BCrypt.hashpw(profileId, BCrypt.gensalt())

        newProfile.profileId = hashedProfileId
        newProfile.password = hashedPassword
        newProfile.userId = _uiState.value.user.userId

        _profilesState.value = _profilesState.value.plus(newProfile)
    }

    fun deleteProfile(name: String) {
        _profilesState.value = _profilesState.value.filter { profile -> profile.name != name }
    }

    fun startCreatingProfile() {
        resultLauncherHelper.launch(CreateProfileActivity::class.java)
    }


    /*
    Maturity Level Survey related methods
     */
//    fun startAnsweringSurvey() {
//        _uiState.value = _uiState.value.copy(
//            answeringSurvey = true,
////            creatingProfile = false,
//        )
//    }

//    fun stopAnsweringSurvey() {
//        _uiState.value = _uiState.value.copy(
//            answeringSurvey = false,
////            creatingProfile = true,
//        )
//    }

//    fun onQuestionAnswerChange(index: Int, answer: SurveyAnswers) {
//        _uiState.value = _uiState.value.copy(
//            questions = _uiState.value.questions.copy(
//                questions = _uiState.value.questions.questions.mapIndexed{ mapIndex, question ->
//                    if (mapIndex == index)
//                        question.copy(answer = answer)
//                    else
//                        question
//                }
//            )
//        )
//    }

//    fun onClearAnswer(index: Int) {
//        _uiState.value = _uiState.value.copy(
//            questions = _uiState.value.questions.copy(
//                questions = _uiState.value.questions.questions.mapIndexed{ mapIndex, question ->
//                    if (mapIndex == index)
//                        question.copy(answer = SurveyAnswers.ZERO)
//                    else
//                        question
//                }
//            )
//        )
//    }

//    fun calculateSurveyAverage() {
//        Log.w("Maturity Level", _uiState.value.questions.questions.toString())
//        Log.w("Maturity Level", "Calculate Maturity Level")
//
//        val random = Random.nextInt(0, 2)
//        _uiState.value = _uiState.value.copy(
//            profileInput = _uiState.value.profileInput.copy(
//                maturityLevel = UserMaturityLevel.values()[random].toString()
//            )
//        )
//
//        createProfile()
//        stopAnsweringSurvey()
//    }
    /*
    Maturity Level Survey related methods
     */



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
//
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