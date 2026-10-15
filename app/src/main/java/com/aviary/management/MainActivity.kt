package com.aviary.management

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.aviary.management.data.Aviary
import com.aviary.management.data.Bird
import com.aviary.management.data.DatabaseProvider
import com.aviary.management.data.FeedingEntry
import com.aviary.management.data.HealthEntry
import com.aviary.management.notifications.ReminderScheduler
import com.aviary.management.ui.CsvImportButton
import com.aviary.management.ui.theme.AviaryTheme
import com.aviary.management.ui.theme.BrandColors
import com.aviary.management.util.CsvImporter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AviaryTheme {
                AviaryApp()
            }
        }
    }
}

enum class AppTab { Dashboard, Birds, Aviaries, Health, Feed }

@Composable
fun AviaryApp() {
    val context = LocalContext.current
    val db = remember { DatabaseProvider.get(context) }

    var birds by remember { mutableStateOf(listOf<Bird>()) }
    var aviaries by remember { mutableStateOf(listOf<Aviary>()) }
    var healthEntries by remember { mutableStateOf(listOf<HealthEntry>()) }
    var feedingEntries by remember { mutableStateOf(listOf<FeedingEntry>()) }
    var selectedTab by remember { mutableStateOf(AppTab.Dashboard) }

    LaunchedEffect(db) {
        db.birdDao().observeAll().collectLatest { birds = it }
    }
    LaunchedEffect(db) {
        db.aviaryDao().observeAll().collectLatest { aviaries = it }
    }
    LaunchedEffect(db) {
        db.healthEntryDao().observeAll().collectLatest { healthEntries = it }
    }
    LaunchedEffect(db) {
        db.feedingEntryDao().observeAll().collectLatest { feedingEntries = it }
    }

    var showAddBird by remember { mutableStateOf(false) }
    var showAddAviary by remember { mutableStateOf(false) }
    var showAddHealth by remember { mutableStateOf(false) }
    var showAddFeed by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aviary Management") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandColors.GreenDeep,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandColors.GreenDeep)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AppTab.values().forEach { tab ->
                    val selected = tab == selectedTab
                    val icon = when (tab) {
                        AppTab.Dashboard -> androidx.compose.material.icons.Icons.Default.Home
                        AppTab.Birds -> androidx.compose.material.icons.Icons.Default.Pets
                        AppTab.Aviaries -> androidx.compose.material.icons.Icons.Default.EmojiNature
                        AppTab.Health -> androidx.compose.material.icons.Icons.Default.MonitorHeart
                        AppTab.Feed -> androidx.compose.material.icons.Icons.Default.CalendarToday
                    }
                    Column(
                        modifier = Modifier
                            .clickable { selectedTab = tab }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(icon, contentDescription = null, tint = if (selected) Color.White else Color.White.copy(alpha = 0.7f))
                        Text(
                            tab.name,
                            fontSize = 10.sp,
                            color = if (selected) Color.White else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(BrandColors.Background)
        ) {
            when (selectedTab) {
                AppTab.Dashboard -> DashboardScreen(
                    birdCount = birds.size,
                    aviaryCount = aviaries.size,
                    healthCount = healthEntries.size,
                    feedCount = feedingEntries.size,
                    onAddBird = { showAddBird = true },
                    onAddAviary = { showAddAviary = true },
                    onImportCsv = { uri ->
                        kotlinx.coroutines.launch {
                            val count = CsvImporter.importBirdsFromCsv(context, uri)
                            Toast.makeText(context, "Imported $count birds", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onScheduleReminder = {
                        ReminderScheduler.scheduleReminder(context, "Feeding reminder", "Time to check your aviary birds.", 1)
                        Toast.makeText(context, "Reminder scheduled", Toast.LENGTH_SHORT).show()
                    }
                )
                AppTab.Birds -> BirdListScreen(
                    birds = birds,
                    aviaries = aviaries,
                    onAddBird = { showAddBird = true },
                    onDeleteBird = { bird ->
                        androidx.lifecycle.lifecycleScope.launch {
                            db.birdDao().delete(bird)
                        }
                    }
                )
                AppTab.Aviaries -> AviaryListScreen(
                    aviaries = aviaries,
                    birds = birds,
                    onAdd = { showAddAviary = true }
                )
                AppTab.Health -> HealthScreen(
                    entries = healthEntries,
                    birds = birds,
                    onAdd = { showAddHealth = true }
                )
                AppTab.Feed -> FeedScreen(
                    entries = feedingEntries,
                    birds = birds,
                    onAdd = { showAddFeed = true }
                )
            }
        }

        if (showAddBird) {
            AddBirdDialog(
                aviaries = aviaries,
                onDismiss = { showAddBird = false },
                onSave = { name, species, mutation, aviaryId, bandId, notes ->
                    val bird = Bird(
                        name = name,
                        species = species,
                        mutation = mutation,
                        aviaryId = aviaryId,
                        bandId = bandId,
                        notes = notes,
                        createdAt = System.currentTimeMillis()
                    )
                    androidx.lifecycle.lifecycleScope.launch {
                        db.birdDao().insert(bird)
                    }
                    showAddBird = false
                }
            )
        }

        if (showAddAviary) {
            AddAviaryDialog(
                onDismiss = { showAddAviary = false },
                onSave = { name, loc, notes ->
                    androidx.lifecycle.lifecycleScope.launch {
                        db.aviaryDao().insert(Aviary(name = name, location = loc, notes = notes))
                    }
                    showAddAviary = false
                }
            )
        }

        if (showAddHealth) {
            AddHealthDialog(
                birds = birds,
                onDismiss = { showAddHealth = false },
                onSave = { birdId, title, notes ->
                    androidx.lifecycle.lifecycleScope.launch {
                        db.healthEntryDao().insert(HealthEntry(birdId = birdId, title = title, notes = notes))
                    }
                    showAddHealth = false
                }
            )
        }

        if (showAddFeed) {
            AddFeedDialog(
                birds = birds,
                onDismiss = { showAddFeed = false },
                onSave = { birdId, title, amount ->
                    androidx.lifecycle.lifecycleScope.launch {
                        db.feedingEntryDao().insert(FeedingEntry(birdId = birdId, title = title, amount = amount))
                    }
                    showAddFeed = false
                }
            )
        }
    }
}

@Composable
fun DashboardScreen(
    birdCount: Int,
    aviaryCount: Int,
    healthCount: Int,
    feedCount: Int,
    onAddBird: () -> Unit,
    onAddAviary: () -> Unit,
    onImportCsv: (Uri) -> Unit,
    onScheduleReminder: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Overview", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandColors.GreenDeep)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoCard(title = "Birds", value = "$birdCount")
            InfoCard(title = "Aviaries", value = "$aviaryCount")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoCard(title = "Health", value = "$healthCount")
            InfoCard(title = "Feed", value = "$feedCount")
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = BrandColors.Surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Quick actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = BrandColors.GreenDeep)
                Button(onClick = onAddBird, modifier = Modifier.fillMaxWidth()) { Text("Add Bird") }
                Button(onClick = onAddAviary, modifier = Modifier.fillMaxWidth()) { Text("Add Aviary") }
                CsvImportButton(onImport = onImportCsv)
                Button(onClick = onScheduleReminder, modifier = Modifier.fillMaxWidth()) { Text("Schedule reminder test") }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BrandColors.Surface),
        modifier = Modifier
            .weight(1f)
            .height(110.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = BrandColors.GreenDeep, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BrandColors.GreenDeep)
        }
    }
}

@Composable
fun BirdListScreen(
    birds: List<Bird>,
    aviaries: List<Aviary>,
    onAddBird: () -> Unit,
    onDeleteBird: (Bird) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Birds", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandColors.GreenDeep)
            Button(onClick = onAddBird) { Text("Add") }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(birds) { bird ->
                val aviaryName = aviaries.firstOrNull { it.id == bird.aviaryId }?.name ?: "Unassigned"
                Card(
                    colors = CardDefaults.cardColors(containerColor = BrandColors.Surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(bird.name, fontWeight = FontWeight.Bold, color = BrandColors.GreenDeep)
                            Text("${bird.species} • ${bird.mutation}")
                            Text("Aviary: $aviaryName", color = Color.Gray)
                        }
                        TextButton(onClick = { onDeleteBird(bird) }) { Text("Delete") }
                    }
                }
            }
        }
    }
}

@Composable
fun AviaryListScreen(
    aviaries: List<Aviary>,
    birds: List<Bird>,
    onAdd: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Aviaries", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandColors.GreenDeep)
            Button(onClick = onAdd) { Text("Add") }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(aviaries) { aviary ->
                val count = birds.count { it.aviaryId == aviary.id }
                Card(colors = CardDefaults.cardColors(containerColor = BrandColors.Surface), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(aviary.name, fontWeight = FontWeight.Bold, color = BrandColors.GreenDeep)
                        if (aviary.location.isNotBlank()) Text("Location: ${aviary.location}")
                        if (aviary.notes.isNotBlank()) Text(aviary.notes)
                        Text("Birds: $count")
                    }
                }
            }
        }
    }
}

@Composable
fun HealthScreen(entries: List<HealthEntry>, birds: List<Bird>, onAdd: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Health", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandColors.GreenDeep)
            Button(onClick = onAdd) { Text("Add") }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(entries) { entry ->
                val birdName = birds.firstOrNull { it.id == entry.birdId }?.name ?: "Unknown bird"
                Card(colors = CardDefaults.cardColors(containerColor = BrandColors.Surface), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(entry.title, fontWeight = FontWeight.Bold, color = BrandColors.GreenDeep)
                        Text("Bird: $birdName")
                        if (entry.notes.isNotBlank()) Text(entry.notes)
                        Text(formatDate(entry.timestamp))
                    }
                }
            }
        }
    }
}

@Composable
fun FeedScreen(entries: List<FeedingEntry>, birds: List<Bird>, onAdd: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Feeding", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandColors.GreenDeep)
            Button(onClick = onAdd) { Text("Add") }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(entries) { entry ->
                val birdName = birds.firstOrNull { it.id == entry.birdId }?.name ?: "Unknown bird"
                Card(colors = CardDefaults.cardColors(containerColor = BrandColors.Surface), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(entry.title, fontWeight = FontWeight.Bold, color = BrandColors.GreenDeep)
                        Text("Bird: $birdName")
                        if (entry.amount.isNotBlank()) Text("Amount: ${entry.amount}")
                        Text(formatDate(entry.timestamp))
                    }
                }
            }
        }
    }
}

private fun formatDate(ts: Long): String = DateFormat.format("dd MMM yyyy, HH:mm", Date(ts)).toString()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBirdDialog(
    aviaries: List<Aviary>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Long, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedSpecies by remember { mutableStateOf(GeneticsCatalog.species.first()) }
    var selectedMutation by remember { mutableStateOf(GeneticsCatalog.mutationsFor(selectedSpecies).first()) }
    var selectedAviaryId by remember { mutableStateOf(aviaries.firstOrNull()?.id ?: 0L) }
    var bandId by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bird") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                SpeciesDropdown(label = "Species", selected = selectedSpecies, options = GeneticsCatalog.species, onChange = {
                    selectedSpecies = it
                    selectedMutation = GeneticsCatalog.mutationsFor(it).firstOrNull() ?: "Normal"
                })
                SpeciesDropdown(label = "Mutation", selected = selectedMutation, options = GeneticsCatalog.mutationsFor(selectedSpecies), onChange = { selectedMutation = it })
                if (aviaries.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = aviaries.firstOrNull { it.id == selectedAviaryId }?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Aviary") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            aviaries.forEach { aviary ->
                                DropdownMenuItem(
                                    text = { Text(aviary.name) },
                                    onClick = {
                                        selectedAviaryId = aviary.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(value = bandId, onValueChange = { bandId = it }, label = { Text("Band ID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) {
                    onSave(name.trim(), selectedSpecies, selectedMutation, selectedAviaryId, bandId.trim(), notes.trim())
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAviaryDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Aviary") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) {
                    onSave(name.trim(), location.trim(), notes.trim())
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthDialog(
    birds: List<Bird>,
    onDismiss: () -> Unit,
    onSave: (Long, String, String) -> Unit
) {
    var birdId by remember { mutableStateOf(birds.firstOrNull()?.id ?: 0L) }
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Health Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (birds.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = birds.firstOrNull { it.id == birdId }?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Bird") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            birds.forEach { bird ->
                                DropdownMenuItem(text = { Text(bird.name) }, onClick = { birdId = bird.id; expanded = false })
                            }
                        }
                    }
                }
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank()) {
                    onSave(birdId, title.trim(), notes.trim())
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeedDialog(
    birds: List<Bird>,
    onDismiss: () -> Unit,
    onSave: (Long, String, String) -> Unit
) {
    var birdId by remember { mutableStateOf(birds.firstOrNull()?.id ?: 0L) }
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Feeding Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (birds.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = birds.firstOrNull { it.id == birdId }?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Bird") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            birds.forEach { bird ->
                                DropdownMenuItem(text = { Text(bird.name) }, onClick = { birdId = bird.id; expanded = false })
                            }
                        }
                    }
                }
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank()) {
                    onSave(birdId, title.trim(), amount.trim())
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpeciesDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onChange(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
