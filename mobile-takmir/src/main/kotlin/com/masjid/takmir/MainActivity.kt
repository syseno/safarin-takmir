package com.masjid.takmir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.masjid.takmir.data.local.SettingsManager
import com.masjid.takmir.navigation.TakmirNavGraph
import com.masjid.takmir.security.EncryptedTokenManager
import com.masjid.takmir.ui.theme.TakmirTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.navigation.compose.rememberNavController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: EncryptedTokenManager

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            val isLoggedIn = tokenManager.getToken() != null
            
            setContent {
                val themeMode by settingsManager.themeMode.collectAsState(initial = 0)

                TakmirTheme(themeMode = themeMode) {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        val navController = rememberNavController()
                        TakmirNavGraph(
                            navController = navController,
                            isLoggedIn = isLoggedIn
                        )
                    }
                }
            }
        }
    }
}
