import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.RowHeader
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.operation_types.OperationCheckbox
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.operation_types.OperationTextInput
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.operation_types.OperationType
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens.ReservationNameScreen
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens.TimePicker
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.PrepareReservationViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InstrumentsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.OperationsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.OperationsViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.ProjectsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.ProjectsViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SamplesViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SamplesViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TimeViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.ButtonToggle
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.LoadingDialog
import com.nano_tablet.nanotabletrfid.util.UiConstants.BASE_PADDING


@Composable
internal fun SelectionScreen(
    operationsViewModel: OperationsViewModel,
    samplesViewModel: SamplesViewModel,
    projectsViewModel: ProjectsViewModel,
    sharedViewModel: SharedViewModel,
    inactivityViewModel: InactivityViewModel,
    sharedState: SharedViewState.Saved,
    reservationViewModel: PrepareReservationViewModel,
    instrumentsViewModel: InstrumentsViewModel,
    timeViewModel: TimeViewModel,
    navController: NavController,

    ) {
    val operationsState by operationsViewModel.stateFlow.collectAsStateWithLifecycle()
    val samplesState by samplesViewModel.stateFlow.collectAsStateWithLifecycle()
    val projectsState by projectsViewModel.stateFlow.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                ReservationNameScreen(
                    sharedViewModel = sharedViewModel,
                    sharedViewState = sharedState,
                    inactivityViewModel = inactivityViewModel
                )
                Spacer(modifier = Modifier.width(50.dp))
                TimePicker(
                    timeViewModel = timeViewModel,
                    inactivityViewModel = inactivityViewModel
                )
            }


            when (operationsState) {
                is OperationsViewState.Loading -> {
                    LoadingDialog(true, "Loading operations")
                }

                is OperationsViewState.Success -> {
                    OperationsRow(
                        viewModel = operationsViewModel,
                        state = operationsState as OperationsViewState.Success,
                        sharedViewModel = sharedViewModel,
                        inactivityViewModel = inactivityViewModel,
                    )
                }

                else -> {}
            }

            when (val state = samplesState) {
                is SamplesViewState.Loading -> {
                    LoadingDialog(true, "Loading samples")
                }

                is SamplesViewState.Success -> {
                    SamplesRow(
                        state = state,
                        sharedViewModel = sharedViewModel,
                        samplesViewModel = samplesViewModel,
                        inactivityViewModel = inactivityViewModel,
                    )
                }

                else -> {}
            }

            when (projectsState) {
                is ProjectsViewState.Loading -> {
                    LoadingDialog(true, "Loading projects")
                }

                is ProjectsViewState.Success -> {
                    ProjectsRow(
                        state = projectsState as ProjectsViewState.Success,
                        sharedViewModel = sharedViewModel,
                        projectsViewModel = projectsViewModel,
                        inactivityViewModel = inactivityViewModel,

                    )
                }

                else -> {}
            }

        }
    }
}


@Composable
fun OperationsRow(
    viewModel: OperationsViewModel,
    state: OperationsViewState.Success,
    sharedViewModel: SharedViewModel,
    inactivityViewModel: InactivityViewModel,
) {


    var textInputs by remember { mutableStateOf(mutableMapOf<String, String>()) }


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.fetchedOperations.forEach { operation ->
            Row(
                modifier = Modifier
                    .padding(bottom = BASE_PADDING)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                RowHeader(operation.name)

                when (OperationType.from(operation.type)) {
                    is OperationType.TextInput, OperationType.TextArea, OperationType.NumberInput -> {
                        OperationTextInput(
                            operation = operation, value = textInputs[operation.name] ?: "",
                            onValueChange = {
                                textInputs = textInputs.toMutableMap().apply {
                                    this[operation.name] = it
                                }
                                viewModel.operationDetailSelection(
                                    operation = operation,
                                    userInput = it,
                                    sharedViewModel = sharedViewModel
                                )
                            }
                        )
                    }

                    is OperationType.CheckBox -> {
                        OperationCheckbox(
                            operation = operation,
                            isChecked = state.selectedOperationDetailsMap[operation.id]?.contains("ON") == true,
                            onToggle = { isChecked ->
                                viewModel.operationDetailSelection(
                                    operation = operation,
                                    userInput = if (isChecked) "ON" else "OFF",
                                    sharedViewModel = sharedViewModel
                                )

                            }
                        )
                    }

                    is OperationType.SingleChoice, OperationType.MultipleSelection -> {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            items(operation.operationDetails) { detail ->

                                val isSelected =
                                    state.selectedOperationDetailsMap[operation.id]?.contains(
                                        detail.name
                                    ) == true
                                Box(
                                    modifier = Modifier
                                        .padding(start = BASE_PADDING)
                                ) {
                                    ButtonToggle(
                                        onClick = {
                                            viewModel.operationDetailSelection(
                                                operation = operation,
                                                operationDetail = detail,
                                                sharedViewModel = sharedViewModel
                                            )
                                        },
                                        text = detail.name,
                                        isSelected = isSelected,
                                        inactivityViewModel = inactivityViewModel,
                                    )
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}


@Composable
fun SamplesRow(
    state: SamplesViewState.Success,
    sharedViewModel: SharedViewModel,
    samplesViewModel: SamplesViewModel,
    inactivityViewModel: InactivityViewModel,
) {
    val samples = state.samples
    val currentSelectedSample = state.selectedSample

    LaunchedEffect(currentSelectedSample) {
        sharedViewModel.updateState(selectedSample = currentSelectedSample)
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = BASE_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RowHeader("Samples")

        LazyRow {
            items(samples) { sample ->

                val isSelected = sample.id == currentSelectedSample?.id
                Log.d("SamplesRow", "Rendering ${sample.name}, selected=${isSelected}")
                Box(
                    modifier = Modifier.padding(start = BASE_PADDING)
                ) {

                    ButtonToggle(
                        onClick = {
                            samplesViewModel.onSampleClicked(sample)
                        },
                        isSelected = isSelected,
                        text = sample.name,
                        inactivityViewModel = inactivityViewModel
                    )

                }

            }
        }
    }
}


@Composable
fun ProjectsRow(
    state: ProjectsViewState.Success,
    sharedViewModel: SharedViewModel,
    projectsViewModel: ProjectsViewModel,
    inactivityViewModel: InactivityViewModel,
) {

    val projects = state.projects
    val currentSelectedProject = state.selectedProject
    val defaultProject =
        (sharedViewModel.sharedState.collectAsStateWithLifecycle().value as? SharedViewState.Saved)?.defaultProject



    LaunchedEffect(defaultProject) {
        defaultProject?.let {
            projectsViewModel.onProjectClicked(it)
            sharedViewModel.updateState(selectedProject = it)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = BASE_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowHeader("Projects")

        LazyRow {
            items(projects) { project ->

                val isSelected = currentSelectedProject?.id == project.id
                Box(
                    modifier = Modifier.padding(start = BASE_PADDING)

                ) {
                    ButtonToggle(
                        onClick = {
                            projectsViewModel.onProjectClicked(project)
                        },
                        isSelected = isSelected,
                        text = project.name,
                        inactivityViewModel = inactivityViewModel
                    )

                }
            }
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
fun SelectionScreenContentPreview() {
    MaterialTheme {

    }
}