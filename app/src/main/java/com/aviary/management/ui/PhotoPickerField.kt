package com.aviary.management.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Photo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun PhotoPickerField(onImagePicked: (Uri?) -> Unit, currentUri: String?) {
    val uriState = remember { mutableStateOf<Uri?>(currentUri?.let { Uri.parse(it) }) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uriState.value = uri
        onImagePicked(uri)
    }

    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF8)), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            if (uriState.value != null) {
                Image(
                    painter = rememberAsyncImagePainter(uriState.value),
                    contentDescription = "Bird photo",
                    modifier = Modifier.height(80.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(verticalArrangement = Arrangement.Center) {
                    Text("No photo", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
            Button(onClick = { launcher.launch("image/*") }) {
                Icon(Icons.Default.Photo, contentDescription = null)
                Text("Choose photo", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
