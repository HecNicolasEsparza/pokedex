package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.ApolloResponse
import com.bumptech.glide.Glide
import com.example.myapplication.Data.PokemonDataDB
import com.example.myapplication.databinding.FragmentFirstBinding
import com.example.myapplication.graphQL.GraphQL
import com.example.myapplication.graphQL.PokemonRepository
import com.example.myapplication2.GetAllPokemonsQuery
import com.example.myapplication2.GetPokemonQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    // Declarar el adaptador y el repositorio como propiedades de la clase
    private lateinit var pokemonAdapter: PokemonAdapter
    private lateinit var repository: PokemonRepository

    private var isFirstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("FirstFragment", "onViewCreated iniciado")

        // Inicializar el adaptador y configurar el RecyclerView
        pokemonAdapter = PokemonAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pokemonAdapter
        }

        // Usar tu clase GraphQL existente para obtener el cliente Apollo
        val graphQL = GraphQL()
        val apolloClient = graphQL.getApolloClient()

        // Crear implementación del repositorio
        repository = object : PokemonRepository {
            override suspend fun getPokemon(): ApolloResponse<GetAllPokemonsQuery.Data> {
                return apolloClient.query(GetAllPokemonsQuery()).execute()
            }

            override suspend fun getPokemon(namePokemon: String): ApolloResponse<GetPokemonQuery.Data> {
                return apolloClient.query(GetPokemonQuery(namePokemon)).execute()
            }
        }

        // Cargar datos iniciales
        loadPokemonData()
    }

    private fun loadPokemonData() {
        // Cargar datos usando Dispatchers.IO
        lifecycleScope.launch {
            try {
                Log.d("FirstFragment", "Iniciando carga de datos...")

                val response = withContext(Dispatchers.IO) {
                    repository.getPokemon()
                }

                Log.d("FirstFragment", "Respuesta recibida: ${response.data}")

                if (response.hasErrors()) {
                    Log.e("FirstFragment", "Errores en GraphQL: ${response.errors}")
                } else {
                    response.data?.pokemons?.let { pokemons ->
                        val validPokemons = pokemons.filterNotNull()
                        Log.d("FirstFragment", "Pokémon válidos encontrados: ${validPokemons.size}")
                        pokemonAdapter.updateData(validPokemons)
                    } ?: Log.e("FirstFragment", "No se encontraron pokémon en la respuesta")
                }
            } catch (e: Exception) {
                Log.e("FirstFragment", "Error completo: ${e.message}", e)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isFirstLoad = false
        Log.d("FirstFragment", "onResume ejecutado. isFirstLoad = $isFirstLoad")
        loadPokemonData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Convertir el adaptador anónimo en una clase interna para poder acceder a él en toda la clase
    private inner class PokemonAdapter : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

        private var pokemonList = listOf<GetAllPokemonsQuery.Pokemon>()

        fun updateData(newList: List<GetAllPokemonsQuery.Pokemon>) {
            Log.d("FirstFragment", "Actualizando datos: ${newList.size} pokémon")
            pokemonList = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pokemon, parent, false)
            return PokemonViewHolder(view)
        }

        override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
            val pokemon = pokemonList[position]
            holder.bind(pokemon, position)
        }

        override fun getItemCount() = pokemonList.size

        inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(pokemon: GetAllPokemonsQuery.Pokemon, position: Int) {
                Log.d("FirstFragment", "Binding pokémon: ${pokemon.name}")

                // Configurar el nombre del Pokémon
                itemView.findViewById<TextView>(R.id.textView)?.text = pokemon.name

                // Configurar el número del Pokémon usando posición simple
                val pokemonNumber = position + 1
                itemView.findViewById<TextView>(R.id.tvNumber)?.text = "#${String.format("%03d", pokemonNumber)}"
                // Configurar la clasificación
                itemView.findViewById<TextView>(R.id.tvClassification)?.text = pokemon.classification ?: "Pokémon"

                // Configurar los tipos
                val typesText = pokemon.types?.joinToString(" • ") ?: "Unknown"
                itemView.findViewById<TextView>(R.id.tvTypes)?.text = typesText

                // Configurar las estadísticas
                val statsText = "CP: ${pokemon.maxCP ?: "?"} • HP: ${pokemon.maxHP ?: "?"}"
                itemView.findViewById<TextView>(R.id.tvStats)?.text = statsText

                // Configurar la imagen del Pokémon usando Glide
                val imageView = itemView.findViewById<ImageView>(R.id.imageView)
                pokemon.image?.let { imageUrl ->
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(imageView)
                } ?: run {
                    imageView?.setImageResource(R.drawable.ic_launcher_foreground)
                }

                // Agregar click listener para navegar al detalle del Pokémon
                // Agregar click listener para navegar al detalle del Pokémon
                itemView.setOnClickListener {
                    val intent = Intent().apply {
                        setClassName("com.example.pokedexahorasi2", "com.example.pokedexahorasi2.MainActivity")

                        // Siempre mandamos el nombre del pokémon
                        putExtra("pokemonName", pokemon.name?.lowercase())

                        // Y ahora, mandamos el booleano que define el comportamiento
                        if (isFirstLoad) {
                            // Si es la primera carga, el receptor debe usar la red (Retrofit)
                            Log.d("FirstFragment", "Clic en primera carga: Enviando nombre + useDatabase=false")
                            putExtra("useDatabase", false)
                        } else {
                            // Si ya no es la primera carga (después de onResume), el receptor debe usar el ContentProvider
                            Log.d("FirstFragment", "Clic subsecuente: Enviando nombre + useDatabase=true")
                            putExtra("useDatabase", true)
                        }
                    }

                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Log.e("FirstFragment", "No se encontró la actividad de destino.")
                        Toast.makeText(requireContext(), "Actividad no encontrada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}