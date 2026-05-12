package com.example.livrerso

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_activity)

        // 1. Saudação
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        val nomeUsuario = intent.getStringExtra("NOME_USUARIO") ?: "Usuário"

        // Pega o primeiro nome com segurança
        val primeiroNome = nomeUsuario.split(" ").firstOrNull() ?: "Usuário"
        tvGreeting?.text = "Olá, $primeiroNome"

        // 2. Clique no Desafio do Dia (Card Azul Superior)
        findViewById<View>(R.id.cardDesafioDoDia)?.setOnClickListener {
            irParaRoleta()
        }

        // 3. Clique no Desafio do Dia (Card Verde da Lista)
        findViewById<View>(R.id.cardDesafioLista)?.setOnClickListener {
            irParaRoleta()
        }

        // 4. Clique na Biblioteca de Alexandria
        findViewById<View>(R.id.cardBiblioteca)?.setOnClickListener {
            Toast.makeText(this, "Logo logo essa biblioteca voltará!", Toast.LENGTH_SHORT).show()
        }

        // 5. Navegação para Perfil
        findViewById<View>(R.id.btnProfileNav)?.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("NOME_USUARIO", nomeUsuario)
            startActivity(intent)
            // Aplica a animação bonita de deslizar que criamos
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    // Função auxiliar para evitar repetir código
    private fun irParaRoleta() {
        val intent = Intent(this, DesafioDoDialActivity::class.java)
        startActivity(intent)
        // Aplica a animação bonita de deslizar
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}