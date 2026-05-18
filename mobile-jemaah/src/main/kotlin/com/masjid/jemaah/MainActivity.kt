package com.masjid.jemaah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.masjid.jemaah.navigation.JemaahNavGraph
import com.masjid.jemaah.security.EncryptedTokenManager
import com.masjid.jemaah.ui.theme.JemaahTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.navigation.compose.rememberNavController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: EncryptedTokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            val isLoggedIn = tokenManager.getToken() != null
            
            setContent {
                JemaahTheme {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        val navController = rememberNavController()
                        JemaahNavGraph(
                            navController = navController,
                            isLoggedIn = isLoggedIn
                        )
                    }
                }
            }
        }
    }
}
