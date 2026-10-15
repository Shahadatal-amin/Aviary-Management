package com.aviary.management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aviary.management.ui.theme.AviaryTheme
import com.aviary.management.ui.theme.BrandColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AviaryTheme {
                AviaryManagementApp()
            }
        }
    }
}

@Composable
fun AviaryManagementApp() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Aviary Management")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandColors.GreenDeep,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandColors.Background)
                .padding(paddingValues),
            color = BrandColors.Background
        ) {
            BirdFormScreen()
        }
    }
}

@Composable
fun BirdFormScreen() {
    val catalog = GeneticsCatalog
    var birdName by remember { mutableStateOf("") }
    var aviaryName by remember { mutableStateOf("Main Flight") }
    var selectedSpecies by remember { mutableStateOf(catalog.species.first()) }
    var selectedMutation by remember { mutableStateOf(catalog.mutationsFor(selectedSpecies).first()) }
    var notes by remember { mutableStateOf("") }

    val mutationOptions = catalog.mutationsFor(selectedSpecies)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Add a new bird",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = BrandColors.GreenDeep
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = BrandColors.Card),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabeledField(label = "Bird name") {
                    OutlinedTextField(
                        value = birdName,
                        onValueChange = { birdName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Sunshine") }
                    )
                }

                LabeledField(label = "Aviary") {
                    OutlinedTextField(
                        value = aviaryName,
                        onValueChange = { aviaryName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Aviary name") }
                    )
                }

                SpeciesDropdown(
                    label = "Species",
                    selected = selectedSpecies,
                    options = catalog.species,
                    onValueChange = {
                        selectedSpecies = it
                        val newMutation = catalog.mutationsFor(it).firstOrNull() ?: "Normal"
                        selectedMutation = newMutation
                    }
                )

                SpeciesDropdown(
                    label = "Genetics / Mutation",
                    selected = selectedMutation,
                    options = mutationOptions,
                    onValueChange = { selectedMutation = it }
                )

                LabeledField(label = "Notes") {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        placeholder = { Text("Health notes, pedigree, ring ID, etc.") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { /* TODO: save bird to database */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Save Bird", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = BrandColors.Card),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Sparkles, contentDescription = null, tint = BrandColors.Gold)
                    Text(
                        text = "Genetics guide",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandColors.GreenDeep
                    )
                }

                Text(
                    text = "Data source: curated local genetics catalog. Do not import or display third-party copyrighted content without permission.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = BrandColors.Gold)
                    Text(
                        text = "Suggested pairing: ${selectedSpecies} / ${selectedMutation}",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandColors.GreenDeep,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = BrandColors.GreenDeep,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpeciesDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
