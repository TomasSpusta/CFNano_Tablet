package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Project
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.ProjectsRepository
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
 * Loads a user's default project and exposes it as [DefaultProjectViewState].
 *
 * Lifecycle:
 * - Starts in [DefaultProjectViewState.Idle]
 * - Emits [DefaultProjectViewState.Loading] while fetching
 * - On success → [DefaultProjectViewState.Success]
 * - On failure → [DefaultProjectViewState.Error] (+ UI alert event)
 *
 * Notes:
 * - Repository returns `Either<NetworkError, ProjectSchema>`.
 * - UI should collect [stateFlow] with lifecycle awareness.
 */
@HiltViewModel
class DefaultProjectViewModel @Inject constructor(
    private val defaultProjectRepository: ProjectsRepository,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<DefaultProjectViewState>(DefaultProjectViewState.Idle)
    val stateFlow: StateFlow<DefaultProjectViewState> = _stateFlow.asStateFlow()

    private val tag = "DefaultProject"
    private var defaultProject: Project? = null
    /**
     * Fetches the default project by [projectId].
     *
     * Re-entry guard:
     * - If a load is already in progress, the call is ignored.
     * - If the same [projectId] is already loaded and [force] is false, it skips work.
     */
    fun fetchDefaultProject(projectId: String) = viewModelScope.launch {
        _stateFlow.update { return@update DefaultProjectViewState.Loading }
        defaultProjectRepository.fetchDefaultProject(defaultProjectId = projectId)
            .onRight { projectSchema ->
                defaultProject =
                    Project(
                        name = projectSchema.psa_name,
                        id = projectSchema.id
                    )
                Log.i(tag, defaultProject.toString())
                _stateFlow.update {
                    return@update DefaultProjectViewState.Success(
                        defaultProject = defaultProject,
                    )
                }
            }.onLeft { error ->
                _stateFlow.update {
                    return@update DefaultProjectViewState.Error(error = error.error.message)
                }
                sendEvent(Event.AlertDialog("$tag Error", "${error.error.code.name}\n${error.error.message}"))
                Log.e("$tag Error", error.toString())
            }
    }
}