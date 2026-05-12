package com.example.livrerso

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder
import kotlin.math.PI
import kotlin.math.sin

class DesafioDoDialActivity : AppCompatActivity() {

    data class Livro(val titulo: String, val autor: String, val coverUrl: String)

    private lateinit var ivCapa: ImageView
    private lateinit var tvTitulo: TextView
    private lateinit var tvAutor: TextView
    private lateinit var btnAleatorio: MaterialButton
    private lateinit var btnFavoritar: ImageButton
    private lateinit var cardLivro: MaterialCardView
    private lateinit var layoutLoading: View
    private lateinit var tvPontos: TextView

    private val livros = mutableListOf<Livro>()
    private val filaAleatoria = mutableListOf<Livro>()
    private val handler = Handler(Looper.getMainLooper())
    private var girando = false
    private var livroAtual: Livro? = null

    // ─── Fallbacks por gênero para quando a API falha ou retorna pouco ───────────
    private val fallbacksPorGenero = mapOf(
        "brazilian_literature" to listOf(
            Livro("Dom Casmurro", "Machado de Assis", "https://covers.openlibrary.org/b/id/8222526-L.jpg"),
            Livro("Grande Sertão: Veredas", "João Guimarães Rosa", "https://covers.openlibrary.org/b/id/8301542-L.jpg"),
            Livro("O Cortiço", "Aluísio Azevedo", "https://covers.openlibrary.org/b/id/8239887-L.jpg"),
            Livro("Capitães da Areia", "Jorge Amado", "https://covers.openlibrary.org/b/id/8301543-L.jpg"),
            Livro("Iracema", "José de Alencar", "https://covers.openlibrary.org/b/id/8301544-L.jpg"),
            Livro("Memórias Póstumas de Brás Cubas", "Machado de Assis", "https://covers.openlibrary.org/b/id/8301545-L.jpg"),
            Livro("A Moreninha", "Joaquim Manuel de Macedo", "https://covers.openlibrary.org/b/id/8301546-L.jpg"),
            Livro("Vidas Secas", "Graciliano Ramos", "https://covers.openlibrary.org/b/id/8301547-L.jpg"),
            Livro("O Alquimista", "Paulo Coelho", "https://covers.openlibrary.org/b/id/8239888-L.jpg"),
            Livro("Sagarana", "João Guimarães Rosa", "https://covers.openlibrary.org/b/id/8301548-L.jpg")
        ),
        "romance" to listOf(
            Livro("Orgulho e Preconceito", "Jane Austen", "https://covers.openlibrary.org/b/id/8231856-L.jpg"),
            Livro("O Morro dos Ventos Uivantes", "Emily Brontë", "https://covers.openlibrary.org/b/id/8301550-L.jpg"),
            Livro("Carta para o Fim dos Tempos", "Nicholas Sparks", "https://covers.openlibrary.org/b/id/8301551-L.jpg"),
            Livro("Me Before You", "Jojo Moyes", "https://covers.openlibrary.org/b/id/8301552-L.jpg"),
            Livro("Outlander", "Diana Gabaldon", "https://covers.openlibrary.org/b/id/8301553-L.jpg"),
            Livro("It Ends with Us", "Colleen Hoover", "https://covers.openlibrary.org/b/id/12818863-L.jpg"),
            Livro("Bridgerton: O Duque e Eu", "Julia Quinn", "https://covers.openlibrary.org/b/id/8301555-L.jpg"),
            Livro("A Culpa é das Estrelas", "John Green", "https://covers.openlibrary.org/b/id/8301556-L.jpg"),
            Livro("Razão e Sensibilidade", "Jane Austen", "https://covers.openlibrary.org/b/id/8301557-L.jpg"),
            Livro("Crepúsculo", "Stephenie Meyer", "https://covers.openlibrary.org/b/id/8301558-L.jpg")
        ),
        "crime_fiction" to listOf(
            Livro("O Assassinato de Roger Ackroyd", "Agatha Christie", "https://covers.openlibrary.org/b/id/8301560-L.jpg"),
            Livro("O Nome da Rosa", "Umberto Eco", "https://covers.openlibrary.org/b/id/8301561-L.jpg"),
            Livro("Garota Exemplar", "Gillian Flynn", "https://covers.openlibrary.org/b/id/8301562-L.jpg"),
            Livro("Menina que Roubava Livros... não, A Garota no Trem", "Paula Hawkins", "https://covers.openlibrary.org/b/id/8301563-L.jpg"),
            Livro("Sherlock Holmes: Estudo em Vermelho", "Arthur Conan Doyle", "https://covers.openlibrary.org/b/id/8301564-L.jpg"),
            Livro("O Silêncio dos Inocentes", "Thomas Harris", "https://covers.openlibrary.org/b/id/8301565-L.jpg"),
            Livro("Dez Negrinhos", "Agatha Christie", "https://covers.openlibrary.org/b/id/8301566-L.jpg"),
            Livro("Big Little Lies", "Liane Moriarty", "https://covers.openlibrary.org/b/id/8301567-L.jpg"),
            Livro("O Código Da Vinci", "Dan Brown", "https://covers.openlibrary.org/b/id/8301568-L.jpg"),
            Livro("In Cold Blood", "Truman Capote", "https://covers.openlibrary.org/b/id/8301569-L.jpg")
        ),
        "horror" to listOf(
            Livro("It: A Coisa", "Stephen King", "https://covers.openlibrary.org/b/id/8231855-L.jpg"),
            Livro("O Iluminado", "Stephen King", "https://covers.openlibrary.org/b/id/8301571-L.jpg"),
            Livro("Dracula", "Bram Stoker", "https://covers.openlibrary.org/b/id/8301572-L.jpg"),
            Livro("Frankenstein", "Mary Shelley", "https://covers.openlibrary.org/b/id/8301573-L.jpg"),
            Livro("O Exorcista", "William Peter Blatty", "https://covers.openlibrary.org/b/id/8301574-L.jpg"),
            Livro("Carrie", "Stephen King", "https://covers.openlibrary.org/b/id/8301575-L.jpg"),
            Livro("O Chamado de Cthulhu", "H.P. Lovecraft", "https://covers.openlibrary.org/b/id/8301576-L.jpg"),
            Livro("A Casa dos Espíritos... House of Leaves", "Mark Z. Danielewski", "https://covers.openlibrary.org/b/id/8301577-L.jpg"),
            Livro("Pet Sematary", "Stephen King", "https://covers.openlibrary.org/b/id/8301578-L.jpg"),
            Livro("O Médico e o Monstro", "R.L. Stevenson", "https://covers.openlibrary.org/b/id/8301579-L.jpg")
        ),
        "manga" to listOf(
            Livro("Naruto Vol. 1", "Masashi Kishimoto", "https://covers.openlibrary.org/b/id/8301580-L.jpg"),
            Livro("One Piece Vol. 1", "Eiichiro Oda", "https://covers.openlibrary.org/b/id/8301581-L.jpg"),
            Livro("Dragon Ball Vol. 1", "Akira Toriyama", "https://covers.openlibrary.org/b/id/8301582-L.jpg"),
            Livro("Attack on Titan Vol. 1", "Hajime Isayama", "https://covers.openlibrary.org/b/id/8301583-L.jpg"),
            Livro("Death Note Vol. 1", "Tsugumi Ohba", "https://covers.openlibrary.org/b/id/8301584-L.jpg"),
            Livro("Fullmetal Alchemist Vol. 1", "Hiromu Arakawa", "https://covers.openlibrary.org/b/id/8301585-L.jpg"),
            Livro("Demon Slayer Vol. 1", "Koyoharu Gotouge", "https://covers.openlibrary.org/b/id/8301586-L.jpg"),
            Livro("My Hero Academia Vol. 1", "Kohei Horikoshi", "https://covers.openlibrary.org/b/id/8301587-L.jpg"),
            Livro("Berserk Vol. 1", "Kentaro Miura", "https://covers.openlibrary.org/b/id/8301588-L.jpg"),
            Livro("Vagabond Vol. 1", "Takehiko Inoue", "https://covers.openlibrary.org/b/id/8301589-L.jpg")
        ),
        "hq" to listOf(
            Livro("Batman: A Piada Mortal", "Alan Moore", "https://covers.openlibrary.org/b/id/12818862-L.jpg"),
            Livro("Turma da Mônica: Laços", "Vitor Cafaggi", "https://covers.openlibrary.org/b/id/8301542-L.jpg"),
            Livro("Watchmen", "Alan Moore", "https://covers.openlibrary.org/b/id/8301591-L.jpg"),
            Livro("Sandman Vol. 1", "Neil Gaiman", "https://covers.openlibrary.org/b/id/8301592-L.jpg"),
            Livro("V de Vingança", "Alan Moore", "https://covers.openlibrary.org/b/id/8301593-L.jpg"),
            Livro("Maus", "Art Spiegelman", "https://covers.openlibrary.org/b/id/8301594-L.jpg"),
            Livro("The Walking Dead Vol. 1", "Robert Kirkman", "https://covers.openlibrary.org/b/id/8301595-L.jpg"),
            Livro("Saga Vol. 1", "Brian K. Vaughan", "https://covers.openlibrary.org/b/id/8301596-L.jpg"),
            Livro("Homem-Aranha: Azul", "Jeph Loeb", "https://covers.openlibrary.org/b/id/8301597-L.jpg"),
            Livro("Superman: Cavaleiro Vermelho", "Mark Millar", "https://covers.openlibrary.org/b/id/8301598-L.jpg")
        )
    )

    private val pontosRunnable = object : Runnable {
        override fun run() {
            val count = (tvPontos.text.length % 3) + 1
            tvPontos.text = ".".repeat(count)
            handler.postDelayed(this, 400)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desafio_do_dial)

        ivCapa = findViewById(R.id.ivCapa)
        tvTitulo = findViewById(R.id.tvTituloLivro)
        tvAutor = findViewById(R.id.tvAutorLivro)
        btnAleatorio = findViewById(R.id.btnAleatorio)
        btnFavoritar = findViewById(R.id.btnFavoritar)
        cardLivro = findViewById(R.id.cardLivro)
        layoutLoading = findViewById(R.id.layoutLoading)
        tvPontos = findViewById(R.id.tvPontos)

        findViewById<Chip>(R.id.chipClassicos).setOnClickListener { carregarLivrosDaAPI("brazilian_literature") }
        findViewById<Chip>(R.id.chipRomance).setOnClickListener { carregarLivrosDaAPI("romance") }
        findViewById<Chip>(R.id.chipMisterio).setOnClickListener { carregarLivrosDaAPI("crime_fiction") }
        findViewById<Chip>(R.id.chipTerror).setOnClickListener { carregarLivrosDaAPI("horror") }
        findViewById<Chip>(R.id.chipManga).setOnClickListener { carregarLivrosDaAPI("manga") }
        findViewById<Chip>(R.id.chipHQ).setOnClickListener { carregarLivrosDaAPI("hq") }

        btnAleatorio.setOnClickListener {
            if (!girando && livros.isNotEmpty()) iniciarSlotMachine()
        }

        btnFavoritar.setOnClickListener {
            livroAtual?.let { favoritarLivro(it) }
        }

        carregarLivrosDaAPI("brazilian_literature")
    }

    private fun carregarLivrosDaAPI(genero: String) {
        handler.removeCallbacksAndMessages(null)
        livros.clear()
        filaAleatoria.clear()
        cardLivro.visibility = View.INVISIBLE
        layoutLoading.visibility = View.VISIBLE
        btnAleatorio.isEnabled = false
        handler.post(pontosRunnable)

        // ✅ Queries precisas por gênero — sem duplo filtro, sem underscore
        val query = when (genero) {
            "brazilian_literature" -> "subject:\"literatura brasileira\""
            "romance"              -> "subject:romance OR subject:\"romance fiction\""
            "crime_fiction"        -> "subject:mystery OR subject:\"crime fiction\" OR subject:detective"
            "horror"               -> "subject:horror OR subject:terror OR subject:\"horror fiction\""
            "manga"                -> "subject:manga publisher:Panini OR publisher:JBC OR publisher:NewPOP OR publisher:Devir"
            "hq"                   -> "subject:comics OR subject:\"graphic novels\" OR subject:quadrinhos language:por"
            else                   -> "subject:$genero"
        }

        val url = "https://openlibrary.org/search.json?q=${URLEncoder.encode(query, "UTF-8")}&limit=100&fields=title,author_name,cover_i"

        OkHttpClient().newCall(Request.Builder().url(url).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { usarFallbackLocal(genero) }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.body?.string() ?: ""
                    val docs = JSONObject(body).getJSONArray("docs")

                    for (i in 0 until docs.length()) {
                        val doc = docs.getJSONObject(i)
                        val title = doc.optString("title").takeIf { it.isNotBlank() } ?: continue
                        val coverId = doc.optInt("cover_i", -1)
                        val author = doc.optJSONArray("author_name")?.optString(0) ?: "Autor desconhecido"

                        // ✅ Aceita livros mesmo sem capa — usa placeholder da Open Library
                        val coverUrl = if (coverId != -1)
                            "https://covers.openlibrary.org/b/id/$coverId-L.jpg"
                        else
                            "https://openlibrary.org/images/icons/avatar_book-sm.png"

                        livros.add(Livro(title, author, coverUrl))
                    }

                    runOnUiThread {
                        // ✅ Mínimo de 5 livros antes de considerar a API válida
                        if (livros.size >= 5) {
                            livros.shuffle()
                            exibirPrimeiroLivro()
                        } else {
                            usarFallbackLocal(genero)
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread { usarFallbackLocal(genero) }
                }
            }
        })
    }

    private fun exibirPrimeiroLivro() {
        handler.removeCallbacks(pontosRunnable)
        recarregarFila()
        val livro = proximoLivroSemRepeticao()
        atualizarCard(livro, false)
        layoutLoading.visibility = View.GONE
        cardLivro.visibility = View.VISIBLE
        cardLivro.alpha = 0f
        cardLivro.animate()
            .alpha(1f).scaleX(1f).scaleY(1f)
            .setDuration(500)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()
        btnAleatorio.isEnabled = true
    }

    private fun iniciarSlotMachine() {
        girando = true
        btnAleatorio.isEnabled = false
        val livroFinal = proximoLivroSemRepeticao()
        var iter = 0
        fun girar() {
            if (iter < 12) {
                atualizarCard(livros.random(), false)
                iter++
                handler.postDelayed({ girar() }, 120L)
            } else {
                atualizarCard(livroFinal, true)
                tocarMelodiaFeliz()
                girando = false
                btnAleatorio.isEnabled = true
            }
        }
        girar()
    }

    private fun atualizarCard(livro: Livro, animado: Boolean) {
        livroAtual = livro
        tvTitulo.text = livro.titulo
        tvAutor.text = livro.autor
        btnFavoritar.setImageResource(android.R.drawable.btn_star_big_off)

        Glide.with(this)
            .load(livro.coverUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(ivCapa)

        cardLivro.setOnClickListener {
            if (!girando) {
                val busca = URLEncoder.encode("${livro.titulo} ${livro.autor}", "UTF-8")
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com.br/s?k=$busca")))
            }
        }

        if (animado) {
            val sX = ObjectAnimator.ofFloat(cardLivro, "scaleX", 1f, 1.1f, 1f)
            val sY = ObjectAnimator.ofFloat(cardLivro, "scaleY", 1f, 1.1f, 1f)
            AnimatorSet().apply {
                playTogether(sX, sY)
                duration = 600
                start()
            }
        }
    }

    private fun favoritarLivro(livro: Livro) {
        val prefs = getSharedPreferences("LivrersoPrefs", Context.MODE_PRIVATE)
        val favs = prefs.getStringSet("favoritos", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val entry = "${livro.titulo}|${livro.autor}"

        if (favs.contains(entry)) {
            Toast.makeText(this, "Já está nos favoritos!", Toast.LENGTH_SHORT).show()
        } else {
            favs.add(entry)
            prefs.edit().putStringSet("favoritos", favs).apply()
            btnFavoritar.setImageResource(android.R.drawable.btn_star_big_on)
            Toast.makeText(this, "Favoritado! ❤️", Toast.LENGTH_SHORT).show()
        }
    }

    private fun recarregarFila() {
        filaAleatoria.clear()
        filaAleatoria.addAll(livros.shuffled())
    }

    private fun proximoLivroSemRepeticao(): Livro {
        if (filaAleatoria.isEmpty()) recarregarFila()
        return filaAleatoria.removeAt(0)
    }

    // ✅ Fallback agora usa lista específica do gênero, não mais 2 livros fixos
    private fun usarFallbackLocal(genero: String = "hq") {
        livros.clear()
        val lista = fallbacksPorGenero[genero] ?: fallbacksPorGenero["hq"]!!
        livros.addAll(lista)
        exibirPrimeiroLivro()
    }

    private fun tocarMelodiaFeliz() {
        Thread {
            try {
                val sampleRate = 44100
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .build()
                    )
                    .setBufferSizeInBytes(
                        AudioTrack.getMinBufferSize(
                            sampleRate,
                            AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT
                        )
                    )
                    .build()

                audioTrack.play()
                val freqs = listOf(523.25, 659.25, 783.99, 1046.50)
                for (f in freqs) {
                    val n = 4410
                    val s = ShortArray(n)
                    for (i in 0 until n) {
                        s[i] = (sin(2.0 * PI * i * f / sampleRate) * 12000).toInt().toShort()
                    }
                    audioTrack.write(s, 0, s.size)
                }
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) { /* silencioso */ }
        }.start()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}