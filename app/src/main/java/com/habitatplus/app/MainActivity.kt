package com.habitatplus.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.habitatplus.app.navigation.NavGraph
import com.habitatplus.app.ui.theme.HabitatPlusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HabitatPlusTheme {
                NavGraph()
            }
        }
    }
}