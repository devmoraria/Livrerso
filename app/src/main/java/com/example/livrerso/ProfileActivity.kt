package com.example.livrerso

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // 1. Pega o nome do usuário
        val nomeRecebido = intent.getStringExtra("NOME_USUARIO") ?: "Usuário"
        findViewById<TextView>(R.id.tvNomePerfil)?.text = nomeRecebido

        // 2. Clique em Favoritos (Abre a lista salva)
        findViewById<TextView>(R.id.btnFavoritos)?.setOnClickListener {
            mostrarFavoritos()
        }

        // 3. Clique em Livros Baixados (Mensagem de "Em breve")
        findViewById<TextView>(R.id.btnBaixados)?.setOnClickListener {
            Toast.makeText(this, "Em breve você poderá baixar livros para ler offline!", Toast.LENGTH_SHORT).show()
        }

        // 4. Botão Sair
        findViewById<TextView>(R.id.btnSair)?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            // Transição bonita de volta
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun mostrarFavoritos() {
        val prefs = getSharedPreferences("LivrersoPrefs", Context.MODE_PRIVATE)
        val favs = prefs.getStringSet("favoritos", setOf()) ?: setOf()

        if (favs.isEmpty()) {
            Toast.makeText(this, "Nenhum livro favoritado ainda.", Toast.LENGTH_SHORT).show()
        } else {
            val listaFormatada = favs.map { it.replace("|", " - ") }.sorted().joinToString("\n\n")

            AlertDialog.Builder(this)
                .setTitle("❤️ Meus Favoritos")
                .setMessage(listaFormatada)
                .setPositiveButton("OK", null)
                .setNeutralButton("Limpar Lista") { _, _ ->
                    prefs.edit().remove("favoritos").apply()
                    Toast.makeText(this, "Lista limpa!", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    // Mantém a transição suave ao usar o botão voltar do celular
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}