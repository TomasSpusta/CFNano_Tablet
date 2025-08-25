package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import androidx.lifecycle.ViewModel
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.OperationsRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.ProjectsRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.SamplesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor (
    private val operationsRepository: OperationsRepository,
    private val projectRepository: ProjectsRepository,
    private val samplesRepository: SamplesRepository

    //operations
    //operation details

): ViewModel()