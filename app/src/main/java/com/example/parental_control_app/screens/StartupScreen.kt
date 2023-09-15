package com.example.parental_control_app.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.viewmodels.StartupState
import com.example.parental_control_app.viewmodels.StartupViewModel
import com.example.parental_control_app.viewmodels.SurveyAnswers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartupScreen(viewModel: StartupViewModel) {
    val uiState = viewModel.uiState
    val scope = rememberCoroutineScope()
    val passwordSheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        AnimatedVisibility(uiState.creatingProfile) {
            CreateProfileForm(uiState, viewModel)
        }
        AnimatedVisibility(uiState.answeringSurvey) {
            MaturityLevelSurvey(
                state = uiState,
                onChangeAnswer = viewModel::onQuestionAnswerChange,
                onBack = viewModel::stopAnsweringSurvey,
                onContinue = viewModel::calculateSurveyAverage,
                onClearAnswer = viewModel::onClearAnswer
            )
        }
        AnimatedVisibility(!uiState.creatingProfile) {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                if (uiState.passwordBottomSheet.showSheet) {
                    PasswordBottomSheet(
                        sheetState = passwordSheetState,
                        viewModel = viewModel,
                    ) {
                        scope.launch {
                            passwordSheetState.hide()
                            viewModel.stopShowingParentPassword()
                        }
                    }
                }
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Profiles")
                    ProfilesGrid(
                        uiState,
                        viewModel,
                    )

                    if (uiState.user.firstSignIn) {
                        Button(onClick = viewModel::saveProfiles) {
                            Text("Save")
                        }
                    }

                    FilledTonalButton(onClick = viewModel::signOut) {
                        Text("Sign Out")
                    }
                }
            }
        }
    }
}



@Composable
fun ProfilesGrid(
    state: StartupState,
    viewModel: StartupViewModel,
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.surfaceTint,
    )

    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 135.dp)){
        items(state.profiles){profile ->
            val index = remember { Random.nextInt(0, colors.size) }
            ProfileCard(
                username = profile.name,
                color = colors[index],
                onClick = {
                    viewModel.setSharedPreferencesProfile(profile)
                    if (profile.child) viewModel.startChildActivity()
                    else viewModel.getParentPassword(profile.password)
                }
            )
        }

        if (state.user.firstSignIn) {
            item {
                AddProfileButton(onClick = viewModel::startCreatingProfile)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordBottomSheet(
    sheetState: SheetState,
    viewModel: StartupViewModel,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 50.dp, top = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text("Enter your password")
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.uiState.passwordBottomSheet.value,
                onValueChange = viewModel::onBottomSheetPasswordChange,
                label = { Text("Password") },
                visualTransformation = if (viewModel.uiState.passwordBottomSheet.showPassword) VisualTransformation.None else PasswordVisualTransformation()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Checkbox(
                    viewModel.uiState.passwordBottomSheet.showPassword,
                    onCheckedChange = viewModel::onBottomSheetCheckboxChange
                )
                Text("Show Password")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::checkParentPassword
            ) {
                Text("Continue")
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileForm(
    uiState: StartupState,
    viewModel: StartupViewModel,
) {

    val isParent = remember { mutableStateOf(true) }
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.ROOT)
    val openDateDialog = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 10.dp)
    ){

        if (openDateDialog.value) {
            item {
                val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
                DatePickerDialog(
                    onDismissRequest = {
                        openDateDialog.value = false
                        datePickerState.selectedDateMillis = null
                    },
                    confirmButton = {
                        TextButton(onClick = { openDateDialog.value = false }, enabled = confirmEnabled.value) {
                            viewModel.onBirthdayChange(datePickerState.selectedDateMillis!!)
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { openDateDialog.value = false }) {
                            datePickerState.selectedDateMillis = null
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }

        item { Text("Create new Profile", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) }
        item {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth().padding(bottom = 15.dp),
                value = uiState.profileInput.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Name")}
            )
        }
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                value = if (datePickerState.selectedDateMillis != null) formatter.format(Date(datePickerState.selectedDateMillis!!)) else "MM/DD/YYYY",
                onValueChange = {},
                readOnly = true,
                label = { Text("Birthday") },
                trailingIcon = { IconButton(onClick = { openDateDialog.value = true }) {
                    Icon(Icons.Rounded.DateRange, "date picker")
                } }
            )
        }
        item {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth().padding(bottom = 15.dp),
                value = uiState.profileInput.age,
                onValueChange = viewModel::onAgeChange,
                label = { Text("Age")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        item {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth().padding(bottom = 20.dp),
                value = uiState.profileInput.phoneNumber!!,
                onValueChange = viewModel::onPhoneNumberChange,
                label = { Text("Phone Number")}
            )
        }
        item { Text("Profile Type:") }
        item {
            Row(
                Modifier
                    .selectableGroup()
                    .fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isParent.value,
                        onClick = {
                            isParent.value = true
                            viewModel.changeProfileType(true)
                        },
                        modifier = Modifier.semantics { contentDescription = "Parent Profile" }
                    )
                    Text("Parent")
                }
                Spacer(modifier = Modifier.width(20.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    RadioButton(
                        selected = !isParent.value,
                        onClick = {
                            isParent.value = false
                            viewModel.changeProfileType(false)
                        },
                        modifier = Modifier.semantics { contentDescription = "Child Profile" }
                    )
                    Text("Child")
                }
            }
        }
        item {
            AnimatedVisibility(isParent.value) {
                Column {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth().padding(bottom = 20.dp),
                        value = uiState.profileInput.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = { Text("Password") },
                    )
                    Text(
                        text = "Password is required to parents profile to ensure that child users will not be able to access parent specific features.",
                        fontSize = 12.sp
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
        item {
            Row {
                FilledTonalButton(onClick = viewModel::stopCreatingProfile) {
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = "Cancel"
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                if (isParent.value) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = viewModel::createProfile
                    ) {
                        Text("Create Profile")
                    }
                } else {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = viewModel::startAnsweringSurvey
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MaturityLevelSurvey(
    state: StartupState,
    onChangeAnswer: (index: Int, answer: SurveyAnswers) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onClearAnswer: (index: Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ){
        LazyColumn{
            stickyHeader {
                Column {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(9, 36, 116, 255),
                        progress = (state.questions.totalAnswered.toFloat() / state.questions.totalNumber.toFloat())
                    )
                }
            }
            
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

            itemsIndexed(state.questions.questions) { questionIndex, surveyQuestion ->
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
                                    onClick = { onChangeAnswer(questionIndex, answer)}
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
                            TextButton(onClick = { onClearAnswer(questionIndex) }) {
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
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        Text("Back")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileButton(onClick: () -> Unit) {
    Card (
        modifier = Modifier
            .padding(10.dp)
            .aspectRatio(1f),
        onClick = onClick
    ){
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Icon(Icons.Rounded.Add, "")
            Text("Add Profile")
        }
    }
}



@Composable
fun ProfileCard(username: String, color: Color, onClick: () -> Unit) {
    Card (
        modifier = Modifier
            .padding(10.dp)
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ){
        Text(
            username,
            modifier = Modifier.padding(20.dp),
            textAlign = TextAlign.Center
        )
    }
}

