package com.example.livrerso

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Verifique se esses IDs existem no seu XML de login
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        btnLogin.setOnClickListener {
            val usuario = etEmail.text.toString().trim()
            if (usuario.isEmpty()) {
                etEmail.error = "Informe o usuário"
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            // Simulação de login
            btnLogin.postDelayed({
                val intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra("NOME_USUARIO", usuario)
                    // Isso impede que o usuário volte para o login ao apertar o botão 'voltar'
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }, 1000)
        }
    }
}