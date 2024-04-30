package by.marcel.cancer_clasification.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import by.marcel.cancer_clasification.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    companion object {
        private const val SPLASH_DELAY_MS = 1500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        lifecycleScope.launch {
            delay(SPLASH_DELAY_MS)
            goToMain()
            finish()
        }
    }
    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}