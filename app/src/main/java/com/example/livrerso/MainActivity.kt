package com.example.livrerso

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Instala a Splash API ANTES do super.onCreate
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        // Removido o setContentView para evitar conflito com a Splash API

        // 2. Configura o efeito de saída
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val iconView = splashScreenView.iconView

            val scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 12f)
            val scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 12f)
            val alpha = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)

            AnimatorSet().apply {
                duration = 600L
                interpolator = AnticipateInterpolator()
                playTogether(scaleX, scaleY, alpha)

                doOnEnd {
                    // Primeiro iniciamos a próxima tela
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)

                    // Removemos a view da splash
                    splashScreenView.remove()

                    // Finalizamos a tela de entrada
                    finish()
                }
                start()
            }
        }
    }
}
