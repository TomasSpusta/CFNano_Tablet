package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.UserRepository
import com.nano_tablet.nanotabletrfid.util.Event
import com.nano_tablet.nanotabletrfid.util.EventBus.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val userRepository: UserRepository // ← Use a repository, not another ViewModel
) : ViewModel() {}
