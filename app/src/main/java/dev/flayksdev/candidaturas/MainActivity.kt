package dev.flayksdev.candidaturas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.flayksdev.candidaturas.ui.ApplicationsScreen
import dev.flayksdev.candidaturas.ui.ApplicationsViewModel
import dev.flayksdev.candidaturas.ui.theme.CandidaturasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CandidaturasTheme {
                ApplicationsScreen(viewModel<ApplicationsViewModel>())
            }
        }
    }
}
