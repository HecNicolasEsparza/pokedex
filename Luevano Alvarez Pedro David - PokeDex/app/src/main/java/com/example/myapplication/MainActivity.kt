package com.example.myapplication

import android.content.ContentValues
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.myapplication.Data.PokeDB
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ya no usamos toolbar ni fab, así que eliminamos esas referencias

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        // Si no tienes toolbar, no necesitas setupActionBarWithNavController
        // Puedes eliminar esta línea si no usas ActionBar:
        // setupActionBarWithNavController(navController, appBarConfiguration)
        val dbHelper = PokeDB(this)
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply{
            put(PokeDB.nombre_pokemon, "pikachu")
            put(PokeDB.ID_pokemon,1)
        }

        db.insert(PokeDB.TABLE_Pokemon, null, values)
        db.close()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}