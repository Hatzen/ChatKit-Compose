package de.hartz.software.chatkitcompose.dialogs

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import de.hartz.software.chatkitcompose.models.IDialog
import de.hartz.software.chatkitcompose.models.IMessage

/**
 * Migration-friendly state holder for [DialogsList] mirroring legacy `DialogsListAdapter`
 * operations while driving Compose snapshot state and smooth item animations.
 */
@Stable
class DialogsListState<M : IMessage, D : IDialog<M>>(
    initialDialogs: List<D> = emptyList(),
    val lazyListState: LazyListState = LazyListState(),
) {
    private val _dialogs = mutableStateListOf<D>().apply {
        addAll(initialDialogs)
    }

    /** Current observable list of dialogs. */
    val dialogs: List<D>
        get() = _dialogs

    /**
     * Replaces all dialogs with [items], mirroring `DialogsListAdapter.setItems(...)`.
     */
    fun setItems(items: List<D>) {
        _dialogs.clear()
        _dialogs.addAll(items)
    }

    /**
     * Appends [dialog] to the list if not already present, or updates it at its existing index.
     */
    fun addItem(dialog: D) {
        val index = _dialogs.indexOfFirst { it.id == dialog.id }
        if (index >= 0) {
            _dialogs[index] = dialog
        } else {
            _dialogs.add(dialog)
        }
    }

    /**
     * Inserts or updates [dialog] and optionally keeps the list sorted newest-first by last message.
     */
    fun upsertItem(dialog: D, sortByNewest: Boolean = true) {
        addItem(dialog)
        if (sortByNewest) {
            sortByLastMessage()
        }
    }

    /**
     * Updates the dialog matching [dialogId] when a new [message] arrives and optionally resorts
     * the list, mirroring `DialogsListAdapter.updateDialogWithMessage(...)`.
     */
    fun updateDialogWithMessage(
        dialogId: String,
        message: M,
        sortByNewest: Boolean = true,
        transform: (D, M) -> D,
    ): Boolean {
        val index = _dialogs.indexOfFirst { it.id == dialogId }
        if (index < 0) return false
        _dialogs[index] = transform(_dialogs[index], message)
        if (sortByNewest) {
            sortByLastMessage()
        }
        return true
    }

    /**
     * Deletes the dialog with [id], returning `true` if removed.
     */
    fun deleteById(id: String): Boolean = _dialogs.removeAll { it.id == id }

    /**
     * Clears all dialogs from the list.
     */
    fun clear() {
        _dialogs.clear()
    }

    /**
     * Sorts dialogs descending by `lastMessage?.createdAt?.time` (dialogs without messages appear last).
     */
    fun sortByLastMessage() {
        val sorted = _dialogs.sortedByDescending { it.lastMessage?.createdAt?.time ?: Long.MIN_VALUE }
        _dialogs.clear()
        _dialogs.addAll(sorted)
    }
}

/**
 * Creates and remembers a [DialogsListState] instance across recompositions.
 */
@Composable
fun <M : IMessage, D : IDialog<M>> rememberDialogsListState(
    initialDialogs: List<D> = emptyList(),
    lazyListState: LazyListState = rememberLazyListState(),
): DialogsListState<M, D> = remember(lazyListState) {
    DialogsListState(
        initialDialogs = initialDialogs,
        lazyListState = lazyListState,
    )
}

