package cz.kudladev.vehicletracking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.domain.images.ImageRepository
import cz.kudladev.vehicletracking.model.UploadTaskState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun UploadRecovery() {
    val repository = koinInject<ImageRepository>()
    val tasks by remember(repository) { repository.observeUploads() }.collectAsState(emptyList())
    val unresolved = tasks.filter { it.state != UploadTaskState.COMPLETED }
    var photo by remember { mutableStateOf<ByteArray?>(null) }
    var open by remember { mutableStateOf(false) }
    var retryId by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    if (unresolved.isNotEmpty()) TextButton(onClick = { open = true }) { Text("Přenosy fotografií (${unresolved.size})") }
    if (open) AlertDialog(onDismissRequest = { open = false }, title = { Text("Přenosy fotografií") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Fotografie zůstávají v zařízení do potvrzení serverem. Přerušený přenos mohl být uložen i bez doručené odpovědi.")
                message?.let { Text(it) }
                unresolved.forEach { task ->
                    HorizontalDivider()
                    Text("${task.target} • fotografie ${task.position + 1}")
                    Text("ID: ${task.id}", style = MaterialTheme.typography.bodySmall)
                    task.error?.message?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    Text(when (task.state) {
                        UploadTaskState.QUEUED -> "Čeká na přenos"
                        UploadTaskState.UPLOADING -> "Odesílání ${(task.progress * 100).toInt()} %"
                        UploadTaskState.FAILED -> "Vyžaduje prověření"
                        UploadTaskState.CANCELED -> "Zrušeno, fotografie zachována"
                        UploadTaskState.COMPLETED -> "Uloženo"
                    })
                    TextButton(enabled = !busy, onClick = {
                        scope.launch {
                            try {
                                photo = repository.localUploadPhoto(task.id)
                                if (photo == null) message = "Lokální náhled není dostupný (například import z URL)."
                            } catch (e: CancellationException) { throw e }
                            catch (_: Exception) { message = "Náhled se nepodařilo načíst. Soubor nebyl odstraněn." }
                        }
                    }) { Text("Zobrazit místní fotografii") }
                    if (task.state == UploadTaskState.FAILED || task.state == UploadTaskState.CANCELED) {
                        TextButton(enabled = !busy && task.targetKnown, onClick = {
                            busy = true
                            scope.launch {
                                try {
                                    message = if (repository.reconcileUpload(task.id)) "Uložení ověřeno, lokální kopie uvolněna."
                                    else "Uložení nelze jednoznačně potvrdit. Prověřte fotografie na serveru. Nic nebylo znovu odesláno."
                                } catch (e: CancellationException) { throw e }
                                catch (_: Exception) { message = "Ověření se nezdařilo. Lokální fotografie zůstává zachována." }
                                finally { busy = false }
                            }
                        }) { Text("Ověřit na serveru") }
                        TextButton(enabled = !busy && task.targetKnown, onClick = { retryId = task.id }) { Text("Odeslat znovu…") }
                    } else TextButton(onClick = { repository.cancelUpload(task.id) }) { Text("Zrušit přenos") }
                }
            }
        }, confirmButton = { TextButton(onClick = { open = false }) { Text("Zavřít") } })
    photo?.let { bytes ->
        AlertDialog(onDismissRequest = { photo = null }, title = { Text("Zachovaná fotografie") },
            text = { coil3.compose.AsyncImage(model = bytes, contentDescription = "Lokální fotografie čekající na potvrzení", modifier = Modifier.fillMaxWidth().height(240.dp)) },
            confirmButton = { TextButton(onClick = { photo = null }) { Text("Zavřít") } })
    }
    retryId?.let { id ->
        AlertDialog(onDismissRequest = { retryId = null }, title = { Text("Prověřili jste uložení?") },
            text = { Text("Ověřte fotografie na serveru a vyčkejte na dokončení původního přenosu. Opakované odeslání může vytvořit duplicitu. Pokračujte pouze pokud jste ověřili, že tato fotografie chybí.") },
            confirmButton = { TextButton(onClick = {
                retryId = null
                scope.launch {
                    try { repository.retryUpload(id) }
                    catch (e: CancellationException) { throw e }
                    catch (_: Exception) { message = "Přenos nelze obnovit. Fotografie zůstává zachována." }
                }
            }) { Text("Ověřil/a jsem, fotografie chybí") } },
            dismissButton = { TextButton(onClick = { retryId = null }) { Text("Zpět") } })
    }
}
