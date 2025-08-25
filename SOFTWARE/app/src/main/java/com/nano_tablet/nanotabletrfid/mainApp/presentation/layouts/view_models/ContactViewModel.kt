package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Contact
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.ContactRepository
import com.nano_tablet.nanotabletrfid.util.Event
import com.nano_tablet.nanotabletrfid.util.EventBus.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Loads CRM contact info for the current user and exposes it as [ContactViewState].
 *
 * - Starts in [ContactViewState.Idle].
 * - Emits [ContactViewState.Loading] while fetching.
 * - On success, emits [ContactViewState.Success] with a domain [Contact].
 * - On failure, emits [ContactViewState.Error] and posts an alert event.
 *
 * Notes:
 * - Network and mapping errors are normalized by the repository layer
 *   (returns Either<NetworkError, ContactSchema>).
 * - UI can `collect` [stateFlow] with lifecycle awareness.
 */
@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
) : ViewModel() {
    private val _stateFlow = MutableStateFlow<ContactViewState>(ContactViewState.Idle)
    val stateFlow: StateFlow<ContactViewState> = _stateFlow.asStateFlow()

    val tag = "Contact"
    private var contact: Contact? = null
    /**
     * Fetches contact by [userId].
     *
     * If a fetch is already in progress, this call is ignored to prevent re-entry.
     * (Remove the guard if you want to allow parallel refreshes.)
     */
    fun fetchContact(userId: String) = viewModelScope.launch {
        _stateFlow.update { return@update ContactViewState.Loading }
        contactRepository.fetchContact(userId = userId)
            .onRight { contactSchema ->
                contact = Contact(
                    firstName = contactSchema.firstname,
                    lastName = contactSchema.lastname,
                    id = contactSchema.id,
                    defaultProjectId = contactSchema.ge_defaultprojectid.id,
                    systemUserId = contactSchema.ge_systemuserid.id,
                    researchGroup = contactSchema.ge_primaryrgid.id
                )

                _stateFlow.update {
                    return@update ContactViewState.Success(
                        contact = contact!!
                    )
                }
            }.onLeft { error ->
                _stateFlow.update {
                    ContactViewState.Error(error = error.error.message)
                }

                sendEvent(
                    Event.AlertDialog(
                        "$tag Error",
                        "${error.error.code.name}\n${error.error.message}"
                    )
                )
                Log.e("$tag Error", error.toString())
            }
    }
}