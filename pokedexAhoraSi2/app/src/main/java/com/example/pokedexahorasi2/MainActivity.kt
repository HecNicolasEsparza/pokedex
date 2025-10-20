package com.example.pokedexahorasi2

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.bumptech.glide.Glide
import com.example.data.ChainLink
import com.example.data.pokemon
import com.example.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var nombre_pokemon: TextView
    private lateinit var numero_pokemon: TextView
    private lateinit var tipos_pokemon: TextView
    private lateinit var imagen_pokemon: ImageView
    private lateinit var stats_pokemon: TextView
    private lateinit var size_pokemon: TextView
    private lateinit var debilidades_pokemon: TextView
    private lateinit var resistencias_pokemon: TextView
    private lateinit var evoluciones_pokemon: TextView
    private lateinit var clasificacion_pokemon: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //val bundle = intent.extras
        //var name = bundle?.getBundle("pokemon")
        //println("nombre pokemon: $name")

        val pokemonName = intent.getStringExtra("pokemonName")?.lowercase() ?: "pikachu"
        Log.d("MainActivity", "Pokémon recibido para buscar: $pokemonName")



        nombre_pokemon = findViewById(R.id.tvName)
        numero_pokemon = findViewById(R.id.tvNumber)
        tipos_pokemon = findViewById(R.id.tvTypes)
        imagen_pokemon = findViewById(R.id.imageView2)
        stats_pokemon = findViewById(R.id.tvStats)
        size_pokemon = findViewById(R.id.tvSize)
        debilidades_pokemon = findViewById(R.id.tvWeaknesses)
        resistencias_pokemon = findViewById(R.id.tvResistant)
        evoluciones_pokemon = findViewById(R.id.tvEvolutions)
        clasificacion_pokemon = findViewById(R.id.tvClassification)


        GlobalScope.launch(Dispatchers.IO){
            try {

                val result = RetrofitInstance.api.getUser(pokemonName)
                //val result = RetrofitInstance.api.getUser("Pikachu")
                val allWeaknesses = mutableSetOf<String>()
                val allResistances = mutableSetOf<String>()
                result.types.forEach { pokemonType ->
                    val typeRelations = RetrofitInstance.api.getTypeData(pokemonType.type.url)
                    typeRelations.damage_relations.double_damage_from.forEach { allWeaknesses.add(it.name.uppercase()) }
                    typeRelations.damage_relations.half_damage_from.forEach { allResistances.add(it.name.uppercase()) }
                }


                val speciesResponse = RetrofitInstance.api.getPokemonSpecies(result.species.url)
                val evolutionChainResponse =
                    RetrofitInstance.api.getEvolutionChain(speciesResponse.evolution_chain.url)
                val evolutionNames = parseEvolutionChain(evolutionChainResponse.chain)

                Log.d("pokemon?: ", result.toString())
                println("Result: $result")
                println(result.base_experience)
                withContext(Dispatchers.Main) {
                    nombre_pokemon.text = result.name
                    numero_pokemon.text = "#${String.format("%03d", result.id)}"
                    tipos_pokemon.text =
                        result.types.joinToString(" • ") { it.type.name.uppercase() }
                    stats_pokemon.text = "Experiencia Base: ${result.base_experience}"

                    val heightInMeters = result.height / 10.0
                    val weightInKg = result.weight / 10.0
                    size_pokemon.text = "Altura: ${heightInMeters} m\nPeso: ${weightInKg} kg"

                    Glide.with(this@MainActivity)
                        .load(result.sprites.frontDefault)
                        .into(imagen_pokemon)

                    clasificacion_pokemon.text = "Pokémon Básico"
                    debilidades_pokemon.text = allWeaknesses.joinToString(", ")
                    resistencias_pokemon.text = allResistances.joinToString(", ")
                    evoluciones_pokemon.text = evolutionNames.joinToString(" → ")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "No se pudo cargar el Pokémon '$pokemonName': ${e.message}")
                withContext(Dispatchers.Main) {
                    // Si algo falla, mostramos un error en la pantalla en lugar de crashear
                    nombre_pokemon.text = "No Encontrado"
                    imagen_pokemon.setImageResource(android.R.drawable.ic_dialog_alert) // Pone un icono de error
                    tipos_pokemon.text = ""
                    debilidades_pokemon.text = ""
                    resistencias_pokemon.text = ""
                    evoluciones_pokemon.text = ""
                }
            }

        }
    }
    private fun parseEvolutionChain(chain: ChainLink): List<String> {
        val evolutions = mutableListOf<String>()
        var currentLink: ChainLink? = chain
        while (currentLink != null) {
            evolutions.add(currentLink.species.name.replaceFirstChar { it.uppercase() })
            currentLink = currentLink.evolves_to.firstOrNull()
        }
        return evolutions
    }
}