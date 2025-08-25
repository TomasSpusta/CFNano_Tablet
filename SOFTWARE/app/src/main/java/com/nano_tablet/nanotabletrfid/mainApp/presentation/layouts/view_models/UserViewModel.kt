package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.User
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.UserRepository
import com.nano_tablet.nanotabletrfid.util.Event
import com.nano_tablet.nanotabletrfid.util.EventBus.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _userStateFlow = MutableStateFlow<UserViewState>(
        value = UserViewState.Idle
    )
    val stateFlow: StateFlow<UserViewState> = _userStateFlow.asStateFlow()
    private val tag = "User"

    private lateinit var user: User

    /**
     * Fetches user by a *sanitized* cardId and updates [stateFlow].
     * Idle → Loading → Success | Error
     *
     * Note: if backend returns an empty list, we emit `Success(User(guid = ""))`
     * and show an alert. This matches your UI logic that checks for empty guid.
     */
    fun fetchUser(cardId: String) = viewModelScope.launch {
        _userStateFlow.update { return@update UserViewState.Loading }
        userRepository.fetchUser(cardId = cardId)
            .onRight { userSchema ->
                if (userSchema.isEmpty()) {
                    user = User(guid = "")
                    sendEvent(
                        Event.AlertDialog(
                            "User Data",
                            "User data is empty, please try again and/or check whether your card is registered in booking system"
                        )
                    )
                    _userStateFlow.update { return@update UserViewState.Idle }
                } else {
                    user = User(
                        guid = userSchema[0].contactid
                    )
                    _userStateFlow.update {
                        return@update UserViewState.Success(
                            user = user,
                        )
                    }

                }

            }.onLeft { error ->
                _userStateFlow.update {
                    return@update UserViewState.Error(error.error.message)
                }
                Log.e("$tag Error", error.toString())
                sendEvent(
                    Event.AlertDialog(
                        "$tag Error",
                        "${error.error.code.name}\n${error.error.message}"
                    )
                )
            }
    }

}




