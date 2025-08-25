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
 * Loads and holds the user's projects.
 *
 * State machine:
 * - Idle → Loading → Success | Error
 * - Selection is managed inside [ProjectsViewState.Success].
 */
@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectsRepository: ProjectsRepository,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<ProjectsViewState>(ProjectsViewState.Idle)
    val stateFlow: StateFlow<ProjectsViewState> = _stateFlow.asStateFlow()

    private val tag = "Projects"
    private var projects: List<Project> = emptyList()

    /**
     * Fetch projects for [userId]. Skips work if the same data is already loaded, unless [force].
     * Guards against duplicate concurrent loads.
     */
    fun fetchProjects(userId: String) = viewModelScope.launch {
        _stateFlow.update { return@update ProjectsViewState.Loading }

        projectsRepository.fetchProjects(userId = userId)
            .onRight { projectSchemas ->
                projects = projectSchemas.map { projectSchema ->
                    Project(
                        name = projectSchema.psa_name,
                        id = projectSchema.id
                    )
                }
                Log.i("Projects", projects.toString())
                _stateFlow.update {
                    return@update ProjectsViewState.Success(
                        projects = projects,
                        selectedProject = null,
                        defaultProject = null,
                    )
                }
            }.onLeft { error ->
                _stateFlow.update {
                    return@update ProjectsViewState.Error(error = error.error.message)
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


    fun onProjectClicked(project: Project) = viewModelScope.launch {
        _stateFlow.update { currentState ->
            when (currentState) {
                is ProjectsViewState.Success -> {
                    ProjectsViewState.Success(
                        defaultProject = currentState.defaultProject,
                        projects = currentState.projects,
                        selectedProject = project,
                    )
                }

                else -> {
                    currentState
                }
            }
        }
    }
}
