package com.example.lab10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab10.ui.theme.Lab10Theme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab10Theme {
                val windowSize = calculateWindowSizeClass(this)
                AdaptiveNotesApp(windowSize.widthSizeClass)
            }
        }
    }
}

data class Note(
    val id: Int,
    val title: String,
    val text: String
)

private val sampleNotes = listOf(
    Note(1, "Перша нотатка", "Це приклад нотатки для демонстрації адаптивного UI."),
    Note(2, "Завдання", "Купити молоко, зарядити ноутбук."),
    Note(3, "Sprint recap", "Finalize demo recording and sync with the team.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveNotesApp(widthSizeClass: WindowWidthSizeClass) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> NotesPhoneLayout(
                notes = sampleNotes,
                modifier = contentModifier
            )

            WindowWidthSizeClass.Medium,
            WindowWidthSizeClass.Expanded -> NotesTabletLayout(
                notes = sampleNotes,
                modifier = contentModifier
            )

            else -> NotesPhoneLayout(
                notes = sampleNotes,
                modifier = contentModifier
            )
        }
    }
}

@Composable
fun NotesPhoneLayout(
    notes: List<Note>,
    modifier: Modifier = Modifier
) {
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    if (selectedNote == null) {
        NotesList(
            notes = notes,
            onClick = { selectedNote = it },
            modifier = modifier
        )
    } else {
        NoteDetails(
            note = selectedNote!!,
            onBack = { selectedNote = null },
            modifier = modifier
        )
    }
}

@Composable
fun NotesTabletLayout(
    notes: List<Note>,
    modifier: Modifier = Modifier
) {
    var selectedNote by remember { mutableStateOf(notes.first()) }

    Row(modifier = modifier) {
        NotesList(
            notes = notes,
            onClick = { selectedNote = it },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        NoteDetails(
            note = selectedNote,
            onBack = null,
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
        )
    }
}

@Composable
fun NotesList(
    notes: List<Note>,
    onClick: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(
                text = stringResource(R.string.notes_list),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        items(notes, key = { it.id }) { note ->
            val contentDescription = stringResource(
                id = R.string.note_content_description,
                note.title
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .semantics { this.contentDescription = contentDescription }
                    .clickable { onClick(note) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = note.text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun NoteDetails(
    note: Note,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (onBack != null) {
            val backLabel = stringResource(R.string.back)
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = backLabel
                    }
            ) {
                Text(text = backLabel)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = stringResource(R.string.details),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = note.title,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = note.text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(name = "Phone", widthDp = 360, heightDp = 640, showBackground = true)
@Composable
fun PhonePreview() {
    Lab10Theme {
        AdaptiveNotesApp(WindowWidthSizeClass.Compact)
    }
}

@Preview(name = "Tablet", widthDp = 1280, heightDp = 800, showBackground = true)
@Composable
fun TabletPreview() {
    Lab10Theme {
        AdaptiveNotesApp(WindowWidthSizeClass.Expanded)
    }
}