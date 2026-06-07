package com.starterkim.widgetbuddy.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.starterkim.widgetbuddy.R
import com.starterkim.widgetbuddy.domain.PetStatus
import com.starterkim.widgetbuddy.presentation.theme.WidgetBuddyTheme

@Composable
fun SettingsScreen(
    status: PetStatus,
    onSavePetName: (String) -> Unit,
    onSaveUserName: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
) {
    var petNameInput by remember { mutableStateOf("") }
    var userNameInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.current_settings),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            stringResource(
                R.string.pet_status_info,
                status.getDisplayName(context),
                status.getDisplayUserName(context),
            ),
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.input_new_pet_name_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = petNameInput,
            onValueChange = { petNameInput = it },
            label = { Text(stringResource(R.string.new_pet_name_label)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (petNameInput.isNotBlank()) {
                    onSavePetName(petNameInput)
                    Toast
                        .makeText(
                            context,
                            context.getString(R.string.pet_name_save_complete),
                            Toast.LENGTH_SHORT,
                        ).show()
                    petNameInput = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.save_pet_name))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.input_user_name_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = userNameInput,
            onValueChange = { userNameInput = it },
            label = { Text(stringResource(R.string.user_name_label)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (userNameInput.isNotBlank()) {
                    onSaveUserName(userNameInput)
                    Toast
                        .makeText(
                            context,
                            context.getString(R.string.user_name_save_complete),
                            Toast.LENGTH_SHORT,
                        ).show()
                    userNameInput = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.save_user_name))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.language_settings_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { onLanguageChange("ko") },
                modifier = Modifier.weight(1f),
                colors = if (status.language == "ko") ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
            ) {
                Text(stringResource(R.string.lang_ko))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onLanguageChange("en") },
                modifier = Modifier.weight(1f),
                colors = if (status.language == "en") ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
            ) {
                Text(stringResource(R.string.lang_en))
            }
        }
    }
}

@Preview(name = "설정 화면 - 한국어", showBackground = true)
@Composable
private fun SettingsScreenKoreanPreview() {
    WidgetBuddyTheme {
        SettingsScreen(
            status = PetStatus(name = "겸댕", userName = "주인님", language = "ko"),
            onSavePetName = {},
            onSaveUserName = {},
            onLanguageChange = {},
        )
    }
}

@Preview(name = "설정 화면 - 영어", showBackground = true)
@Composable
private fun SettingsScreenEnglishPreview() {
    WidgetBuddyTheme {
        SettingsScreen(
            status = PetStatus(name = "Buddy", userName = "Master", language = "en"),
            onSavePetName = {},
            onSaveUserName = {},
            onLanguageChange = {},
        )
    }
}
