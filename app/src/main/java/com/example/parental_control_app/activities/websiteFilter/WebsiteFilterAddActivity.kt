package com.example.parental_control_app.activities.websiteFilter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.parental_control_app.data.Site
import com.example.parental_control_app.repositories.SiteRepository
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class WebsiteFilterAddActivity : AppCompatActivity() {

    private val usersRepository = UsersRepository()
    private val siteRepository = SiteRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kidProfileId = intent.getStringExtra("kidProfileId").toString()

        setContent {

            var urlState by remember { mutableStateOf("") }
            val snackBarHostState = remember { SnackbarHostState() }

            ParentalControlAppTheme {

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackBarHostState)
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        OutlinedTextField(
                            value = urlState,
                            onValueChange = { urlState = it },
                            label = { Text("Website URL") }
                        )
                        Button(onClick = {
                            lifecycleScope.launch {
                                continueCallback(kidProfileId, urlState, snackBarHostState)
                            }
                        }) {
                            Text("Add Site")
                        }
                        FilledTonalButton(onClick = { finish() }) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }

    private suspend fun continueCallback(kidProfileId: String, url: String, state: SnackbarHostState) {
        coroutineScope {
            launch {
                val uid = usersRepository.getProfileUID(kidProfileId)
                val newSite = Site(url)
                val message = siteRepository.addSite(uid, newSite)
                state.showSnackbar(message = message)
                finish()
            }
        }
    }
}