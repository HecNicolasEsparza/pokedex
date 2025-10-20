package com.example.pokedexahorasi2

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.data.ChainLink
// Ya no necesitamos PokemonDataDB, así que se puede quitar el import
import com.example.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
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

        // Inicializamos vistas
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

        // --- LÓGICA DE CARGA BASADA EN BOOLEANO ---
        val pokemonName = intent.getStringExtra("pokemonName")?.lowercase() ?: "pikachu"
        val useDatabase = intent.getBooleanExtra("useDatabase", false) // Leemos el booleano

        if (useDatabase) {
            // CASO "TRUE": El emisor nos pide que usemos su base de datos
            Log.d("MainActivity", "Recibido: '$pokemonName' con useDatabase=true. Usando ContentProvider.")
            fetchPokemonFromProvider(pokemonName)
        } else {
            // CASO "FALSE": El emisor nos pide que usemos la red
            Log.d("MainActivity", "Recibido: '$pokemonName' con useDatabase=false. Usando Retrofit.")
            fetchAndDisplayPokemon(pokemonName)
        }
    }

    /**
     * Obtiene los datos de un Pokémon usando el ContentProvider de la app emisora.
     * Esta función ASUME que la app emisora tiene un ContentProvider funcional
     * que responde en la URI "content://com.example.myapplication/pokemons/{nombre}"
     */
    private fun fetchPokemonFromProvider(pokemonName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Usamos la URI que definiste en tu clase DBContentResolver
                val authority = "com.example.myapplication"
                val pokemonUri = Uri.parse("content://$authority/pokemons/$pokemonName")

                // Pedimos todas las columnas que necesitamos. Estos nombres deben ser EXACTOS
                // a los que el ContentProvider del emisor ofrece.
                val projection = arrayOf(
                    "name", "number", "classification", "types", "maxCP", "maxHP",
                    "image", "height", "weight", "weaknesses", "resistances", "evolutions"
                )

                // Ejecutamos la consulta
                val cursor = contentResolver.query(pokemonUri, projection, null, null, null)

                if (cursor != null && cursor.moveToFirst()) {
                    // Si el cursor tiene al menos una fila, leemos los datos
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val number = cursor.getString(cursor.getColumnIndexOrThrow("number"))
                    val classification = cursor.getString(cursor.getColumnIndexOrThrow("classification"))
                    val types = cursor.getString(cursor.getColumnIndexOrThrow("types")) // Asumimos formato "Planta,Veneno"
                    val cp = cursor.getInt(cursor.getColumnIndexOrThrow("maxCP"))
                    val hp = cursor.getInt(cursor.getColumnIndexOrThrow("maxHP"))
                    val image = cursor.getString(cursor.getColumnIndexOrThrow("image"))
                    val height = cursor.getDouble(cursor.getColumnIndexOrThrow("height"))
                    val weight = cursor.getDouble(cursor.getColumnIndexOrThrow("weight"))
                    val weaknesses = cursor.getString(cursor.getColumnIndexOrThrow("weaknesses"))
                    val resistances = cursor.getString(cursor.getColumnIndexOrThrow("resistances"))
                    val evolutions = cursor.getString(cursor.getColumnIndexOrThrow("evolutions")) // Asumimos "Bulbasaur,Ivysaur,Venusaur"

                    // Una vez leídos, actualizamos la UI en el hilo principal
                    withContext(Dispatchers.Main) {
                        nombre_pokemon.text = name.replaceFirstChar { it.uppercase() }
                        numero_pokemon.text = number
                        clasificacion_pokemon.text = classification
                        tipos_pokemon.text = types?.replace(",", " • ")?.uppercase()
                        stats_pokemon.text = "CP: $cp • HP: $hp"
                        size_pokemon.text = "Altura: ${height}m / Peso: ${weight}kg"
                        debilidades_pokemon.text = weaknesses
                        resistencias_pokemon.text = resistances
                        evoluciones_pokemon.text = evolutions?.replace(",", " → ")
                        Glide.with(this@MainActivity).load(image).into(imagen_pokemon)
                    }
                } else {
                    Log.e("ProviderError", "No se encontró '$pokemonName' o el cursor vino vacío.")
                    // Opcional: Mostrar un error en la UI
                }
                cursor?.close() // ¡Muy importante cerrar el cursor!
            } catch (e: Exception) {
                Log.e("ProviderError", "Falló la consulta al ContentProvider para '$pokemonName': ${e.message}")
            }
        }
    }

    // La función de Retrofit se mantiene igual, como fallback o para la primera carga
    private fun fetchAndDisplayPokemon(pokemonName: String) {
        // ... esta función no tiene cambios ...
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = RetrofitInstance.api.getUser(pokemonName)
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

                withContext(Dispatchers.Main) {
                    nombre_pokemon.text = result.name.replaceFirstChar { it.uppercase() }
                    numero_pokemon.text = "#${String.format("%03d", result.id)}"
                    tipos_pokemon.text = result.types.joinToString(" • ") { it.type.name.uppercase() }
                    stats_pokemon.text = "Experiencia Base: ${result.base_experience}"
                    val heightInMeters = result.height / 10.0
                    val weightInKg = result.weight / 10.0
                    size_pokemon.text = "Altura: ${heightInMeters}m / Peso: ${weightInKg}kg"
                    Glide.with(this@MainActivity).load(result.sprites.frontDefault).into(imagen_pokemon)
                    debilidades_pokemon.text = allWeaknesses.joinToString(", ")
                    resistencias_pokemon.text = allResistances.joinToString(", ")
                    evoluciones_pokemon.text = evolutionNames.joinToString(" → ")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "No se pudo cargar el Pokémon '$pokemonName': ${e.message}")
                withContext(Dispatchers.Main) {
                    nombre_pokemon.text = "No Encontrado"
                    imagen_pokemon.setImageResource(android.R.drawable.ic_dialog_alert)
                }
            }
        }
    }

    private fun parseEvolutionChain(chain: ChainLink): List<String> {
        // ... esta función no tiene cambios ...
        val evolutions = mutableListOf<String>()
        var currentLink: ChainLink? = chain
        while (currentLink != null) {
            evolutions.add(currentLink.species.name.replaceFirstChar { it.uppercase() })
            currentLink = currentLink.evolves_to.firstOrNull()
        }
        return evolutions
    }
}