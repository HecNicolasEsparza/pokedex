package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentSecondBinding
import com.example.myapplication.graphQL.GraphQL
import com.example.myapplication.graphQL.PokemonRepository
import com.example.myapplication2.GetPokemonQuery
import com.apollographql.apollo3.api.ApolloResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.myapplication.utils.PokemonTypeColors
import android.graphics.drawable.GradientDrawable
import android.graphics.Color

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pokemonName = arguments?.getString("pokemonName") ?: ""
        Log.d("SecondFragment", "Pokémon seleccionado: $pokemonName")

        if (pokemonName.isNotEmpty()) {
            loadPokemonDetails(pokemonName)
        }
    }

    private fun loadPokemonDetails(pokemonName: String) {
        val graphQL = GraphQL()
        val apolloClient = graphQL.getApolloClient()

        val repository = object : PokemonRepository {
            override suspend fun getPokemon(): ApolloResponse<com.example.myapplication2.GetAllPokemonsQuery.Data> {
                return apolloClient.query(com.example.myapplication2.GetAllPokemonsQuery()).execute()
            }

            override suspend fun getPokemon(namePokemon: String): ApolloResponse<GetPokemonQuery.Data> {
                return apolloClient.query(GetPokemonQuery(namePokemon)).execute()
            }
        }

        lifecycleScope.launch {
            try {
                Log.d("SecondFragment", "Cargando detalles de $pokemonName...")

                withContext(Dispatchers.IO) {
                    val response = repository.getPokemon(pokemonName)

                    withContext(Dispatchers.Main) {
                        if (response.hasErrors()) {
                            Log.e("SecondFragment", "Errores en GraphQL: ${response.errors}")
                        } else {
                            response.data?.pokemon?.let { pokemon ->
                                displayPokemonDetails(pokemon)
                            } ?: Log.e("SecondFragment", "No se encontró el pokémon: $pokemonName")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SecondFragment", "Error cargando detalles: ${e.message}", e)
            }
        }
    }

    private fun displayPokemonDetails(pokemon: GetPokemonQuery.Pokemon) {
        Log.d("SecondFragment", "Mostrando detalles para: ${pokemon.name}")

        // Cambiar color de fondo de la card según el tipo de Pokémon
        val types = pokemon.types?.filterNotNull() // Filtrar nulls
        val (startColor, endColor) = PokemonTypeColors.getTypeColor(types)
        setCardBackgroundGradient(startColor, endColor)

        // Cargar imagen
        pokemon.image?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.imageView2)
        }

        // Información básica
        binding.tvName.text = pokemon.name ?: "Desconocido"

        // Número completo de Pokédex para la primera generación
        val pokemonNumber = when(pokemon.name?.lowercase()) {
            "bulbasaur" -> "#001"
            "ivysaur" -> "#002"
            "venusaur" -> "#003"
            "charmander" -> "#004"
            "charmeleon" -> "#005"
            "charizard" -> "#006"
            "squirtle" -> "#007"
            "wartortle" -> "#008"
            "blastoise" -> "#009"
            "caterpie" -> "#010"
            "metapod" -> "#011"
            "butterfree" -> "#012"
            "weedle" -> "#013"
            "kakuna" -> "#014"
            "beedrill" -> "#015"
            "pidgey" -> "#016"
            "pidgeotto" -> "#017"
            "pidgeot" -> "#018"
            "rattata" -> "#019"
            "raticate" -> "#020"
            "spearow" -> "#021"
            "fearow" -> "#022"
            "ekans" -> "#023"
            "arbok" -> "#024"
            "pikachu" -> "#025"
            "raichu" -> "#026"
            "sandshrew" -> "#027"
            "sandslash" -> "#028"
            "nidoran-f" -> "#029"
            "nidorina" -> "#030"
            "nidoqueen" -> "#031"
            "nidoran-m" -> "#032"
            "nidorino" -> "#033"
            "nidoking" -> "#034"
            "clefairy" -> "#035"
            "clefable" -> "#036"
            "vulpix" -> "#037"
            "ninetales" -> "#038"
            "jigglypuff" -> "#039"
            "wigglytuff" -> "#040"
            "zubat" -> "#041"
            "golbat" -> "#042"
            "oddish" -> "#043"
            "gloom" -> "#044"
            "vileplume" -> "#045"
            "paras" -> "#046"
            "parasect" -> "#047"
            "venonat" -> "#048"
            "venomoth" -> "#049"
            "diglett" -> "#050"
            "dugtrio" -> "#051"
            "meowth" -> "#052"
            "persian" -> "#053"
            "psyduck" -> "#054"
            "golduck" -> "#055"
            "mankey" -> "#056"
            "primeape" -> "#057"
            "growlithe" -> "#058"
            "arcanine" -> "#059"
            "poliwag" -> "#060"
            "poliwhirl" -> "#061"
            "poliwrath" -> "#062"
            "abra" -> "#063"
            "kadabra" -> "#064"
            "alakazam" -> "#065"
            "machop" -> "#066"
            "machoke" -> "#067"
            "machamp" -> "#068"
            "bellsprout" -> "#069"
            "weepinbell" -> "#070"
            "victreebel" -> "#071"
            "tentacool" -> "#072"
            "tentacruel" -> "#073"
            "geodude" -> "#074"
            "graveler" -> "#075"
            "golem" -> "#076"
            "ponyta" -> "#077"
            "rapidash" -> "#078"
            "slowpoke" -> "#079"
            "slowbro" -> "#080"
            "magnemite" -> "#081"
            "magneton" -> "#082"
            "farfetchd" -> "#083"
            "doduo" -> "#084"
            "dodrio" -> "#085"
            "seel" -> "#086"
            "dewgong" -> "#087"
            "grimer" -> "#088"
            "muk" -> "#089"
            "shellder" -> "#090"
            "cloyster" -> "#091"
            "gastly" -> "#092"
            "haunter" -> "#093"
            "gengar" -> "#094"
            "onix" -> "#095"
            "drowzee" -> "#096"
            "hypno" -> "#097"
            "krabby" -> "#098"
            "kingler" -> "#099"
            "voltorb" -> "#100"
            "electrode" -> "#101"
            "exeggcute" -> "#102"
            "exeggutor" -> "#103"
            "cubone" -> "#104"
            "marowak" -> "#105"
            "hitmonlee" -> "#106"
            "hitmonchan" -> "#107"
            "lickitung" -> "#108"
            "koffing" -> "#109"
            "weezing" -> "#110"
            "rhyhorn" -> "#111"
            "rhydon" -> "#112"
            "chansey" -> "#113"
            "tangela" -> "#114"
            "kangaskhan" -> "#115"
            "horsea" -> "#116"
            "seadra" -> "#117"
            "goldeen" -> "#118"
            "seaking" -> "#119"
            "staryu" -> "#120"
            "starmie" -> "#121"
            "mr-mime" -> "#122"
            "scyther" -> "#123"
            "jynx" -> "#124"
            "electabuzz" -> "#125"
            "magmar" -> "#126"
            "pinsir" -> "#127"
            "tauros" -> "#128"
            "magikarp" -> "#129"
            "gyarados" -> "#130"
            "lapras" -> "#131"
            "ditto" -> "#132"
            "eevee" -> "#133"
            "vaporeon" -> "#134"
            "jolteon" -> "#135"
            "flareon" -> "#136"
            "porygon" -> "#137"
            "omanyte" -> "#138"
            "omastar" -> "#139"
            "kabuto" -> "#140"
            "kabutops" -> "#141"
            "aerodactyl" -> "#142"
            "snorlax" -> "#143"
            "articuno" -> "#144"
            "zapdos" -> "#145"
            "moltres" -> "#146"
            "dratini" -> "#147"
            "dragonair" -> "#148"
            "dragonite" -> "#149"
            "mewtwo" -> "#150"
            "mew" -> "#151"
            else -> ""
        }
        binding.tvNumber.text = pokemonNumber

        binding.tvClassification.text = pokemon.classification ?: ""

        // Tipos
        val typesText = types?.joinToString(" • ") ?: ""
        binding.tvTypes.text = typesText

        // Estadísticas
        val statsText = buildString {
            append("CP Máximo: ${pokemon.maxCP ?: "?"}\n")
            append("HP Máximo: ${pokemon.maxHP ?: "?"}\n")
            append("Tasa de Huida: ${pokemon.fleeRate?.let { "${(it * 100).toInt()}%" } ?: "?"}")
        }
        binding.tvStats.text = statsText

        // Tamaño (peso y altura)
        val sizeText = buildString {
            pokemon.height?.let { height ->
                append("Altura: ${height.minimum} - ${height.maximum}\n")
            } ?: append("Altura: Desconocida\n")

            pokemon.weight?.let { weight ->
                append("Peso: ${weight.minimum} - ${weight.maximum}")
            } ?: append("Peso: Desconocido")
        }
        binding.tvSize.text = sizeText

        // Resistencias
        val resistantText = pokemon.resistant?.joinToString(", ") ?: "Sin resistencias conocidas"
        binding.tvResistant.text = resistantText

        // Debilidades
        val weaknessesText = pokemon.weaknesses?.joinToString(", ") ?: "Sin debilidades conocidas"
        binding.tvWeaknesses.text = weaknessesText

        // Evoluciones
        val evolutionsText = if (pokemon.evolutions?.isNotEmpty() == true) {
            "Evoluciona a: ${pokemon.evolutions.joinToString(", ") { it?.name ?: "Desconocido" }}"
        } else {
            "Sin evoluciones"
        }
        binding.tvEvolutions.text = evolutionsText

        Log.d("SecondFragment", "Detalles del pokémon cargados correctamente")
    }

    private fun setCardBackgroundGradient(startColor: String, endColor: String) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.parseColor(startColor),
                Color.parseColor(endColor)
            )
        )
        gradientDrawable.cornerRadius = 60f
        binding.mainCardBackground.background = gradientDrawable
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
