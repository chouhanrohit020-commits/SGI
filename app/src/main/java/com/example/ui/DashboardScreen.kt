package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.CastingPrediction
import com.example.data.model.CastingParameters
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// Custom Foundry / Flame Color Palette
val IndustrialCharcoal = Color(0xFF121417)
val MoltenSteelDark = Color(0xFF1E2126)
val MoltenFlameAmber = Color(0xFFFF7B00)
val MoltenFlameGlow = Color(0xFFFF9E00)
val CoolSteelText = Color(0xFFB1B9C5)
val SafetyGreen = Color(0xFF00C853)
val DangerRed = Color(0xFFFF3D00)
val WarningYellow = Color(0xFFFFD600)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: CastingViewModel) {
    val inputFields by viewModel.inputFields.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val predictionHistory by viewModel.predictionHistory.collectAsStateWithLifecycle()
    
    var currentTab by remember { mutableStateOf(0) } // 0: Predictor, 1: History

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SGI Casting AI Predictor",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = "Ductile Iron Foundry Quality Control",
                            fontSize = 11.sp,
                            color = CoolSteelText,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                },
                actions = {
                    if (currentTab == 0) {
                        IconButton(
                            onClick = { viewModel.loadIdealValues() },
                            modifier = Modifier.testTag("reset_defaults_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Load standard values",
                                tint = MoltenFlameAmber
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IndustrialCharcoal,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // Sticky Footer: Made by Rohit Chouhan (Mandatory Requirement)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(IndustrialCharcoal)
            ) {
                // Horizontal divider
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MoltenSteelDark))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MoltenFlameAmber.copy(alpha = 0.15f),
                                        MoltenFlameGlow.copy(alpha = 0.05f),
                                        Color.Transparent
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified by Rohit",
                                tint = MoltenFlameAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Made by Rohit Chouhan",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        },
        containerColor = IndustrialCharcoal
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = currentTab,
                containerColor = IndustrialCharcoal,
                contentColor = MoltenFlameAmber
            ) {
                Tab(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Predictor Form", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = MoltenFlameAmber,
                    unselectedContentColor = CoolSteelText
                )
                Tab(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Predictions History (${predictionHistory.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = MoltenFlameAmber,
                    unselectedContentColor = CoolSteelText
                )
            }

            // Screen Content
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                if (currentTab == 0) {
                    PredictorTabContent(
                        inputFields = inputFields,
                        uiState = uiState,
                        onValueChange = viewModel::updateField,
                        onSubmit = { viewModel.getPrediction() },
                        onReset = { viewModel.loadIdealValues() },
                        onClear = { viewModel.clearFields() },
                        onBack = { viewModel.resetUiState() },
                        onSubmitFeedback = { id, rating, notes -> viewModel.submitFeedback(id, rating, notes) }
                    )
                } else {
                    HistoryTabContent(
                        history = predictionHistory,
                        onDelete = { viewModel.deleteHistoryRecord(it) },
                        onClearAll = { viewModel.clearAllHistory() },
                        onSelect = { record ->
                            viewModel.loadPredictionRecord(record)
                            currentTab = 0 // jump to predictor
                        }
                    )
                }
            }
        }
    }
}

// ---------------- PREDICTOR SCREEN -----------------

@Composable
fun PredictorTabContent(
    inputFields: Map<String, String>,
    uiState: PredictionUiState,
    onValueChange: (String, String) -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
    onSubmitFeedback: (Int, String, String) -> Unit
) {
    if (uiState is PredictionUiState.Success) {
        PredictionResultPanel(
            result = uiState,
            onBack = onBack,
            onSubmitFeedback = onSubmitFeedback
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Batch Quality Parameters",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Check the live composition values. Warning signs flag out-of-range keys.",
                            fontSize = 12.sp,
                            color = CoolSteelText
                        )
                    }
                }
            }

            // Quick Actions Block
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onReset,
                        modifier = Modifier.weight(1f).testTag("quick_ideal_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MoltenSteelDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MoltenFlameAmber.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fill Ideal (Default)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onClear,
                        modifier = Modifier.weight(1f).testTag("clear_fields_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MoltenSteelDark,
                            contentColor = CoolSteelText
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Clear Formula", fontSize = 11.sp)
                    }
                }
            }

            // chemical panel
            item {
                CategoryHeading(title = "Primary Heat Identification", icon = Icons.Default.Info)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MoltenSteelDark),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MoltenFlameAmber.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Heat Code Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.3f)) {
                                Text(
                                    text = "Heat Code (Identification)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Identifier of metallurgical melt run",
                                    color = CoolSteelText,
                                    fontSize = 11.sp
                                )
                            }

                            OutlinedTextField(
                                value = inputFields["heatCode"] ?: "",
                                onValueChange = { onValueChange("heatCode", it) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("input_heatCode"),
                                placeholder = { Text("e.g. HC-24A", color = CoolSteelText) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = IndustrialCharcoal,
                                    unfocusedContainerColor = IndustrialCharcoal,
                                    focusedBorderColor = MoltenFlameAmber,
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Gray.copy(alpha = 0.15f)))

                        // 2. Batch Number/Heat Entry (e.g. 1, 2, 3)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.3f)) {
                                Text(
                                    text = "Batch / Heat Entry",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Sequential run number (e.g. 1, 2, 3)",
                                    color = CoolSteelText,
                                    fontSize = 11.sp
                                )
                            }

                            OutlinedTextField(
                                value = inputFields["batchNumber"] ?: "",
                                onValueChange = { onValueChange("batchNumber", it) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("input_batchNumber"),
                                placeholder = { Text("e.g. 1", color = CoolSteelText) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = IndustrialCharcoal,
                                    unfocusedContainerColor = IndustrialCharcoal,
                                    focusedBorderColor = MoltenFlameAmber,
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Gray.copy(alpha = 0.15f)))

                        // 3. Shift (e.g. A, B, C)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.3f)) {
                                Text(
                                    text = "Working Shift",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Shift running this casting batch",
                                    color = CoolSteelText,
                                    fontSize = 11.sp
                                )
                            }

                            val activeShift = inputFields["shift"] ?: ""
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf("A", "B", "C").forEach { shiftLabel ->
                                    val isSelected = activeShift.equals(shiftLabel, ignoreCase = true)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp) // Accessibility-compliant 48dp height minimum
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isSelected) MoltenFlameAmber else IndustrialCharcoal
                                            )
                                            .border(
                                                border = BorderStroke(
                                                    width = 1.dp,
                                                    color = if (isSelected) MoltenFlameAmber else Color.Gray.copy(alpha = 0.4f)
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onValueChange("shift", shiftLabel) }
                                            .testTag("shift_chip_$shiftLabel"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = shiftLabel,
                                            color = if (isSelected) Color.White else CoolSteelText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                CategoryHeading(title = "1. Chemical Composition (%)", icon = Icons.Default.Info)
            }
            
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MoltenSteelDark),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val chemKeys = listOf("carbon", "silicon", "manganese", "phosphorus", "sulphur", "chrome", "copper", "magnesium")
                        for (key in chemKeys) {
                            ParameterInputField(
                                key = key,
                                value = inputFields[key] ?: "",
                                onValueChange = { onValueChange(key, it) }
                            )
                        }
                    }
                }
            }

            // process panel
            item {
                CategoryHeading(title = "2. Process Properties", icon = Icons.Default.PlayArrow)
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MoltenSteelDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        ParameterInputField(
                            key = "pouringTemp",
                            value = inputFields["pouringTemp"] ?: "",
                            onValueChange = { onValueChange("pouringTemp", it) }
                        )
                    }
                }
            }

            // green sand panel
            item {
                CategoryHeading(title = "3. Green Moulding Sand", icon = Icons.Default.Warning)
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MoltenSteelDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val sandKeys = listOf("sandMoisture", "compactability", "mouldHardness", "coreHardness", "permeability", "gcsValue")
                        for (key in sandKeys) {
                            ParameterInputField(
                                key = key,
                                value = inputFields[key] ?: "",
                                onValueChange = { onValueChange(key, it) }
                            )
                        }
                    }
                }
            }

            // Predict button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                if (uiState is PredictionUiState.Loading) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MoltenSteelDark),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = MoltenFlameAmber)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Running AI Solidification Assessment...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Evaluating chemical balance and sand properties",
                                color = CoolSteelText,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("predict_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MoltenFlameAmber,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "AI Predict Rejection Risk",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                if (uiState is PredictionUiState.Error) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = "Error icon", tint = DangerRed)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("AI Evaluation Error", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text(uiState.message, color = CoolSteelText, fontSize = 11.sp)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun CategoryHeading(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MoltenFlameAmber, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = MoltenFlameAmber,
            fontSize = 14.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun ParameterInputField(
    key: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val range = CastingParameters.RANGES[key]
    val label = CastingParameters.LABELS[key] ?: key
    
    val parsedVal = value.toDoubleOrNull()
    val isOutOfRange = range != null && parsedVal != null && parsedVal !in range

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.3f)) {
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            if (range != null) {
                Text(
                    text = "QC Limit: ${range.start} - ${range.endInclusive}",
                    color = if (isOutOfRange) MoltenFlameGlow else CoolSteelText,
                    fontSize = 11.sp,
                    fontWeight = if (isOutOfRange) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("input_$key"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = IndustrialCharcoal,
                unfocusedContainerColor = IndustrialCharcoal,
                focusedBorderColor = if (isOutOfRange) DangerRed else MoltenFlameAmber,
                unfocusedBorderColor = if (isOutOfRange) MoltenFlameAmber.copy(alpha = 0.8f) else Color.Gray.copy(alpha = 0.4f),
                focusedTextColor = if (isOutOfRange) MoltenFlameGlow else Color.White,
                unfocusedTextColor = if (isOutOfRange) MoltenFlameGlow else Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

// ---------------- PREDICTION LOGICS & GAUGE SCREEN -----------------

@Composable
fun PredictionResultPanel(
    result: PredictionUiState.Success,
    onBack: () -> Unit,
    onSubmitFeedback: (Int, String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("result_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = MoltenFlameAmber)
                }
                Text(
                    text = "Evaluation Results",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Circular Gauge Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MoltenSteelDark),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MoltenFlameAmber.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AI REJECTION PROBABILITY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoolSteelText,
                        letterSpacing = 1.sp
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (result.parameters.heatCode.isNotBlank()) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text("Code: ${result.parameters.heatCode}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    labelColor = MoltenFlameAmber,
                                    containerColor = IndustrialCharcoal
                                ),
                                border = BorderStroke(1.dp, MoltenFlameAmber.copy(alpha = 0.5f))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        if (result.parameters.batchNumber.isNotBlank()) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text("Batch: ${result.parameters.batchNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    labelColor = MoltenFlameGlow,
                                    containerColor = IndustrialCharcoal
                                ),
                                border = BorderStroke(1.dp, MoltenFlameGlow.copy(alpha = 0.5f))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        if (result.parameters.shift.isNotBlank()) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text("Shift: ${result.parameters.shift}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    labelColor = Color.White,
                                    containerColor = IndustrialCharcoal
                                ),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(160.dp)
                    ) {
                        val score = result.probability
                        val color = when {
                            score < 20.0 -> SafetyGreen
                            score < 50.0 -> WarningYellow
                            else -> DangerRed
                        }

                        // Drawing Custom Gauge Canvas
                        Canvas(modifier = Modifier.size(150.dp)) {
                            // Track
                            drawArc(
                                color = Color.White.copy(alpha = 0.08f),
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Progress
                            drawArc(
                                color = color,
                                startAngle = 135f,
                                sweepAngle = (score.toFloat() / 100f) * 270f,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${score.roundToInt()}%",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = result.status.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val statusText = when (result.status.uppercase()) {
                        "GOOD" -> "Batch is metallurgically and physically stable. Extremely low risk of defective elements."
                        "CAUTION" -> "Marginal deviations identified. Adjust parameters soon to avoid localized shrinkages."
                        else -> "Critical deviations found. Correct immediately to prevent high batch foundry rejection!"
                    }
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        color = CoolSteelText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }

        // AI Metallurgical Corrections Panel (Triggers on higher risks or param deviations)
        item {
            MetallurgicalSuggestionsPanel(
                params = result.parameters,
                rejectionProbability = result.probability
            )
        }

        // Potential defects predicted
        if (result.defects.isNotEmpty() && result.defects.firstOrNull()?.isNotBlank() == true) {
            item {
                Text("Potential Casting Defects Warning", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (defect in result.defects) {
                        if (defect.isNotBlank()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.1f)),
                                border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(defect.trim(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Primary chemical deviations / risks
        if (result.risks.isNotEmpty() && result.risks.firstOrNull()?.isNotBlank() == true) {
            item {
                Text("Causal Chemical/Sand Deviations", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MoltenSteelDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (risk in result.risks) {
                            if (risk.isNotBlank()) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Text("•", color = MoltenFlameGlow, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.width(16.dp))
                                    Text(risk.trim(), color = CoolSteelText, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Corrective recommendations
        if (result.recommendations.isNotEmpty() && result.recommendations.firstOrNull()?.isNotBlank() == true) {
            item {
                Text("Metallurgical Corrective Guidance", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MoltenSteelDark),
                    border = BorderStroke(1.dp, SafetyGreen.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (rec in result.recommendations) {
                            if (rec.isNotBlank()) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Guideline Action",
                                        tint = SafetyGreen,
                                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(rec.trim(), color = Color.White, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Foundry Experience Feedback (Reinforcement Learning) loop
        item {
            var rating by remember(result.id) { mutableStateOf(result.initialRating) }
            var correctionText by remember(result.id) { mutableStateOf(result.initialCorrections) }
            var isSubmitted by remember(result.id) { mutableStateOf(result.initialRating != "NONE") }

            Text(
                text = "Machine Learning Feedback Loop",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MoltenSteelDark),
                border = BorderStroke(1.dp, MoltenFlameAmber.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (!isSubmitted) {
                        Text(
                            text = "Did SGI AI classify this melt correctly?",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Correcting errors helps SGI AI instantly learn chemical behaviors for future predictions.",
                            color = CoolSteelText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    rating = "CORRECT"
                                    isSubmitted = true
                                    onSubmitFeedback(result.id, "CORRECT", "Accurate AI prediction")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("feedback_accurate"),
                                colors = ButtonDefaults.buttonColors(containerColor = SafetyGreen.copy(alpha = 0.15f), contentColor = SafetyGreen),
                                border = BorderStroke(1.dp, SafetyGreen.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Yes, Correct", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = {
                                    rating = "INCORRECT"
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("feedback_incorrect"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (rating == "INCORRECT") DangerRed.copy(alpha = 0.25f) else DangerRed.copy(alpha = 0.12f),
                                    contentColor = DangerRed
                                ),
                                border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("No, Incorrect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (rating == "INCORRECT") {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Enter actual physical foundry findings:",
                                color = MoltenFlameGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = correctionText,
                                onValueChange = { correctionText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .testTag("feedback_correction_text"),
                                placeholder = {
                                    Text(
                                        text = "e.g. Actually observed severe blowholes or carbide chill or bad nodularity.",
                                        fontSize = 11.sp,
                                        color = CoolSteelText.copy(alpha = 0.6f)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = IndustrialCharcoal,
                                    unfocusedContainerColor = IndustrialCharcoal,
                                    focusedBorderColor = MoltenFlameAmber,
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    if (correctionText.isNotBlank()) {
                                        isSubmitted = true
                                        onSubmitFeedback(result.id, "INCORRECT", correctionText)
                                    }
                                },
                                enabled = correctionText.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .testTag("submit_correction_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = MoltenFlameAmber, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Train AI on Actual Shopfloor Defects", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Success Feedback
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MoltenFlameGlow,
                                modifier = Modifier.size(20.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AI Self-Correcting Loop Active!",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (rating == "CORRECT") {
                                        "Thank you! Your feedback confirms prediction accuracy for Heat ${result.parameters.heatCode}."
                                    } else {
                                        "Groundtruth physical findings enrolled! Gemini has registered \"$correctionText\" for Heat ${result.parameters.heatCode} to optimize future metallurgy evaluations."
                                    },
                                    color = CoolSteelText,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Rerun action
        item {
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MoltenSteelDark, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Edit Quality Formulation", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ---------------- HISTORICAL ARCHIVES SCREEN -----------------

@Composable
fun HistoryTabContent(
    history: List<CastingPrediction>,
    onDelete: (Int) -> Unit,
    onClearAll: () -> Unit,
    onSelect: (CastingPrediction) -> Unit
) {
    if (history.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = null,
                tint = CoolSteelText.copy(alpha = 0.3f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Saved Calculations Yet",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Run predictions on the form tab and they will be archived locally here for reference.",
                color = CoolSteelText,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Historical Predictions Log",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Past runs archived on this device.",
                            color = CoolSteelText,
                            fontSize = 11.sp
                        )
                    }

                    TextButton(
                        onClick = onClearAll,
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, size = 16.dp, tint = DangerRed)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All", color = DangerRed, fontSize = 12.sp)
                    }
                }
            }

            items(history) { record ->
                HistoryRecordCard(
                    record = record,
                    onDelete = { onDelete(record.id) },
                    onSelect = { onSelect(record) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// Extension to help use size constraint in icons easily
@Composable
private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp, tint: Color) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
        tint = tint
    )
}

@Composable
fun HistoryRecordCard(
    record: CastingPrediction,
    onDelete: () -> Unit,
    onSelect: () -> Unit
) {
    val dateStr = remember(record.timestamp) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        dateFormat.format(Date(record.timestamp))
    }

    val statusColor = when (record.status.uppercase()) {
        "GOOD" -> SafetyGreen
        "CAUTION" -> WarningYellow
        else -> DangerRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("history_record_${record.id}"),
        colors = CardDefaults.cardColors(containerColor = MoltenSteelDark),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = CoolSteelText,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp).testTag("delete_history_${record.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", size = 16.dp, tint = CoolSteelText)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular rating outline
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(100.dp))
                        .border(1.dp, statusColor.copy(alpha = 0.4f), shape = RoundedCornerShape(100.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${record.rejectionProbability.roundToInt()}%",
                        color = statusColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    val batchText = if (record.batchNumber.isNotBlank()) " | B: ${record.batchNumber}" else ""
                    val shiftText = if (record.shift.isNotBlank()) " | Shift: ${record.shift}" else ""
                    Text(
                        text = if (record.heatCode.isNotBlank()) "Heat: ${record.heatCode}$batchText$shiftText" else "Status: ${record.status.uppercase()}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "C: ${record.carbon}% | Si: ${record.silicon}% | Mg: ${record.magnesium}% | Temp: ${record.pouringTemp.roundToInt()}°C",
                        fontSize = 11.sp,
                        color = CoolSteelText
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Load formulation",
                    size = 18.dp,
                    tint = MoltenFlameAmber
                )
            }
        }
    }
}

@Composable
fun MetallurgicalSuggestionsPanel(
    params: CastingParameters,
    rejectionProbability: Double
) {
    // Only display when there is a risk of rejection (e.g. probability >= 30% or any parameter is out of range)
    val corrections = remember(params) {
        val list = mutableListOf<Pair<String, String>>() // Pair of (Action Tag, Description)
        
        if (params.carbon < 3.4) {
            list.add("Increase Carbon" to "Add synthetic graphite carburizer (approx 1.2 kg/ton for every 0.1% C increase) to treatment ladle to raise composition to target.")
        } else if (params.carbon > 4.0) {
            list.add("Dilute Melt" to "Carbon chemistry is high. Dilute melt back to target with low-carbon clean steel scrap or pig iron charge.")
        }
        
        if (params.silicon < 2.0) {
            list.add("Increase FeSi Inoculant" to "Add FeSi75 alloy to ladle stream or ladle inoculant system (add 1.5 kg per ton to boost Silicon to target 2.5% range).")
        } else if (params.silicon > 3.0) {
            list.add("Reduce Inoculation" to "Excess Silicon. Reduce ladle FeSi stream inoculant addition in subsequent heats to control graphite blockiness.")
        }
        
        if (params.magnesium < 0.035) {
            list.add("Increase Mg Treatment" to "Critical Magnesium level! Increase FeSiMg alloy or magnesium cored wire (approx 0.6 kg/ton) in treatment ladle to restore nodular/spheroid graphite formation.")
        } else if (params.magnesium > 0.055) {
            list.add("Dilute Magnesium" to "Mg content too high. Reduce FeSiMg treatment alloy addition to prevent dross, pinhole and micro-shrinkage defects.")
        }
        
        if (params.manganese > 0.5) {
            list.add("Restrict Manganese" to "Mn is high (carbide stabilizer). Limit high-manganese scrap alloy; dilute with high-purity pig iron to prevent chilled white iron spots.")
        }
        
        if (params.pouringTemp < 1380.0) {
            list.add("Raise Pouring Temp" to "Pouring temp too low! Hold/re-heat melt or increase ladle preheater temperature to run at 1410°C to prevent Cold Shut & Misrun.")
        } else if (params.pouringTemp > 1440.0) {
            list.add("Cool Down Melt" to "Pouring temp too high! Let the melt cool in ladle or add steel scrap to cool to target, avoiding sand burn-on and thermal expansion cavities.")
        }
        
        if (params.sandMoisture > 3.8) {
            list.add("Lower Sand Moisture" to "Add dry silica sand and mulling sand additives; extend mulling cycle to vent water content and prevent steam-driven blowholes.")
        } else if (params.sandMoisture < 2.8) {
            list.add("Increase Water/Additives" to "Moulding sand is too dry. Carefully add water-mulling additives or bentonite to prevent sand-wash, erosion, or crumble cavities.")
        }
        
        if (params.mouldHardness < 75.0) {
            list.add("Adjust Molten Compaction" to "Mould hardness low! Increase cylinder squeeze/compaction pressure during moulding line operation to avoid sand-swell or mold-wall expansions.")
        }
        
        if (list.isEmpty() && rejectionProbability >= 40.0) {
            list.add("Verify Formulation" to "Adjust chemistry and thermal process parameters. Ensure FeSi stream inoculation is fully active to avoid chill defects.")
        }
        
        list
    }

    if (corrections.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = MoltenFlameAmber,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AI Metallurgical Corrections (HIGH PRIORITY)",
                fontWeight = FontWeight.Bold,
                color = MoltenFlameAmber,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = MoltenSteelDark),
            border = BorderStroke(1.5.dp, DangerRed.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("metallurgical_corrections_panel")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Metallurgical warnings & direct foundry floor recipes calculated by AI for Rohit Chouhan:",
                    color = CoolSteelText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                corrections.forEach { (action, description) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(IndustrialCharcoal.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, MoltenFlameAmber.copy(alpha = 0.15f)), shape = RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(DangerRed.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = action.uppercase(),
                                    color = DangerRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            text = description,
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
