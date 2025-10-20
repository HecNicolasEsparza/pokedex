package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.ApolloResponse
import com.example.myapplication.databinding.FragmentFirstBinding
import com.example.myapplication.graphQL.GraphQL
import com.example.myapplication.graphQL.PokemonRepository
import com.example.myapplication2.GetAllPokemonsQuery
import com.example.myapplication2.GetPokemonQuery
import androidx.lifecycle.lifecycleScope
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.navigation.fragment.findNavController
import android.content.Intent
import android.widget.Toast


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        // Configurar RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Usar tu clase GraphQL existente para obtener el cliente Apollo
        val graphQL = GraphQL()
        val apolloClient = graphQL.getApolloClient()

        // Crear implementación del repositorio
        val repository = object : PokemonRepository {
            override suspend fun getPokemon(): ApolloResponse<GetAllPokemonsQuery.Data> {
                return apolloClient.query(GetAllPokemonsQuery()).execute()
            }

            override suspend fun getPokemon(namePokemon: String): ApolloResponse<GetPokemonQuery.Data> {
                return apolloClient.query(GetPokemonQuery(namePokemon)).execute()
            }
        }

        // Crear adaptador básico
        val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            private var pokemonList = listOf<GetAllPokemonsQuery.Pokemon>()

            fun updateData(newList: List<GetAllPokemonsQuery.Pokemon>) {
                Log.d("FirstFragment", "Actualizando datos: ${newList.size} pokémon")
                pokemonList = newList
                notifyDataSetChanged()
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pokemon, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val pokemon = pokemonList[position]
                Log.d("FirstFragment", "Binding pokémon: ${pokemon.name}")

                // Configurar el nombre del Pokémon
                holder.itemView.findViewById<TextView>(R.id.textView)?.text = pokemon.name

                // Configurar el número del Pokémon usando posición simple
                val pokemonNumber = position + 1
                holder.itemView.findViewById<TextView>(R.id.tvNumber)?.text = "#${String.format("%03d", pokemonNumber)}"
                // Configurar la clasificación
                holder.itemView.findViewById<TextView>(R.id.tvClassification)?.text = pokemon.classification ?: "Pokémon"

                // Configurar los tipos
                val typesText = pokemon.types?.joinToString(" • ") ?: "Unknown"
                holder.itemView.findViewById<TextView>(R.id.tvTypes)?.text = typesText

                // Configurar las estadísticas
                val statsText = "CP: ${pokemon.maxCP ?: "?"} • HP: ${pokemon.maxHP ?: "?"}"
                holder.itemView.findViewById<TextView>(R.id.tvStats)?.text = statsText

                // Configurar la imagen del Pokémon usando Glide
                val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)
                pokemon.image?.let { imageUrl ->
                    Glide.with(holder.itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(imageView)
                } ?: run {
                    imageView?.setImageResource(R.drawable.ic_launcher_foreground)
                }

                // Agregar click listener para navegar al detalle del Pokémon
                holder.itemView.setOnClickListener {
                //Versión de Pedrito Sola
                /*    pokemon.name?.let { pokemonName ->
                        val bundle = Bundle().apply {
                            putString("pokemonName", pokemonName)
                        }
                        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
                    }

                */
                    pokemon.name?.let {pokemonName ->
                        val intent = Intent().apply{
                            setClassName("com.example.pokedexahorasi2", "com.example.pokedexahorasi2.MainActivity")
                            putExtra("pokemonName", pokemonName.lowercase())
                        }
                        if(intent.resolveActivity(requireActivity().packageManager) != null){
                            startActivity(intent)
                        }
                        else{
                            Log.e("FirstFragment", "No se encontró la actividad para abrir SecondFragment")
                            Toast.makeText(requireContext(), "No se encontró la actividad para abrir SecondFragment", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun getItemCount() = pokemonList.size
        }

        binding.recyclerView.adapter = adapter

        // Cargar datos usando Dispatchers.IO
        lifecycleScope.launch {
            try {
                Log.d("FirstFragment", "Iniciando carga de datos...")

                withContext(Dispatchers.IO) {
                    val response = repository.getPokemon()
                    Log.d("FirstFragment", "Respuesta recibida: ${response.data}")

                    withContext(Dispatchers.Main) {
                        if (response.hasErrors()) {
                            Log.e("FirstFragment", "Errores en GraphQL: ${response.errors}")
                        } else {
                            response.data?.pokemons?.let { pokemons ->
                                val validPokemons = pokemons.filterNotNull()
                                Log.d("FirstFragment", "Pokémon válidos encontrados: ${validPokemons.size}")
                                adapter.updateData(validPokemons)
                            } ?: Log.e("FirstFragment", "No se encontraron pokémon en la respuesta")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("FirstFragment", "Error completo: ${e.message}", e)
            }
        }
    }

    override fun onResume(){
        super.onResume()
        Log.d("FirstFragment", "Cargamos datos de la base de datos")
        if(2 > 1){  //le puse esta pendejada para obligar a que esto no me marque errores y porque todavía no sé hacer bases de datos
            try {
                Log.d("FirstFragment", "Iniciando carga de datos...")

                withContext(Dispatchers.IO) {
                    val response = repository.getPokemon()
                    Log.d("FirstFragment", "Respuesta recibida: ${response.data}")

                    withContext(Dispatchers.Main) {
                        if (response.hasErrors()) {
                            Log.e("FirstFragment", "Errores en GraphQL: ${response.errors}")
                        } else {
                            response.data?.pokemons?.let { pokemons ->
                                val validPokemons = pokemons.filterNotNull()
                                Log.d("FirstFragment", "Pokémon válidos encontrados: ${validPokemons.size}")
                                adapter.updateData(validPokemons)
                            } ?: Log.e("FirstFragment", "No se encontraron pokémon en la respuesta")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("FirstFragment", "Error completo: ${e.message}", e)
            }
        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}