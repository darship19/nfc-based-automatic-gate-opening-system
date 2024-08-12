package com.example.gatesystem

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gatesystem.ui.theme.GateSystemTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var requestReference: DatabaseReference
    private lateinit var approvalReference: DatabaseReference
    private lateinit var tokensReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
            Log.d("Firebase", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("Firebase", "Firebase initialization failed", e)
        }

        // Initialize Firebase Database references
        val databaseUrl = ""
        val database = FirebaseDatabase.getInstance(databaseUrl)
        requestReference = database.getReference("Request")
        approvalReference = database.getReference("approval")
        tokensReference = database.getReference("device_tokens") // Reference for storing device tokens

        // Retrieve and store FCM token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "FCM Registration Token: $token")
                storeDeviceToken(token)
            } else {
                Log.e("FCM", "Fetching FCM registration token failed", task.exception)
            }
        }

        setContent {
            GateSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var requestValue by remember { mutableStateOf("No data") }
                    var approvalMessage by remember { mutableStateOf("") }
                    var showApprovalMessage by remember { mutableStateOf(false) }
                    var lastActionTime by remember { mutableStateOf<Long?>(null) }
                    val scope = rememberCoroutineScope()

                    // Read data from Firebase
                    LaunchedEffect(Unit) {
                        requestReference.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val value = snapshot.getValue(String::class.java) ?: "No data"
                                requestValue = value
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Data read failed: ${error.message}", error.toException())
                            }
                        })
                    }

                    // Update approval message based on Firebase
                    LaunchedEffect(Unit) {
                        approvalReference.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val value = snapshot.getValue(String::class.java) ?: ""
                                approvalMessage = value
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Data read failed: ${error.message}", error.toException())
                            }
                        })
                    }

                    LaunchedEffect(lastActionTime) {
                        if (lastActionTime != null) {
                            delay(5000) // 5 seconds delay
                            showApprovalMessage = false
                        }
                    }

                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        requestValue = requestValue,
                        approvalMessage = approvalMessage,
                        showApprovalMessage = showApprovalMessage,
                        onWriteData = { message ->
                            scope.launch {
                                updateRequestValue("No") {
                                    writeToFirebase(message) {
                                        // Update the approvalMessage after successfully writing to Firebase
                                        approvalMessage = message
                                        showApprovalMessage = true
                                        lastActionTime = System.currentTimeMillis()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun storeDeviceToken(token: String) {
        tokensReference.child(token).setValue(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Token stored successfully: $token")
                } else {
                    task.exception?.let {
                        Log.e("FCM", "Token storage failed: ${it.message}", it)
                    }
                }
            }
    }

    private fun writeToFirebase(message: String, onComplete: () -> Unit) {
        // Write the provided message to Firebase under the 'approval' node
        approvalReference.setValue(message).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Data written successfully: $message")
                onComplete() // Callback to update the approval message
            } else {
                task.exception?.let {
                    Log.e("Firebase", "Data write failed: ${it.message}", it)
                }
            }
        }
    }

    private fun updateRequestValue(newValue: String, onComplete: () -> Unit) {
        // Update the request value in Firebase
        requestReference.setValue(newValue).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Request value updated successfully: $newValue")
                onComplete()
            } else {
                task.exception?.let {
                    Log.e("Firebase", "Request update failed: ${it.message}", it)
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    requestValue: String,
    approvalMessage: String,
    showApprovalMessage: Boolean,
    onWriteData: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Request: $requestValue",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                Log.d("Firebase", "Yes button clicked")
                onWriteData("Yes")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open the Gate")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                Log.d("Firebase", "No button clicked")
                onWriteData("No")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reject the Request")
        }
        Spacer(modifier = Modifier.height(32.dp))
        // Show the approval message only if the showApprovalMessage flag is true
        if (showApprovalMessage) {
            val displayMessage = when (approvalMessage) {
                "Yes" -> "Opening Gate"
                "No" -> "Rejected Request"
                else -> ""
            }
            if (displayMessage.isNotEmpty()) {
                Text(
                    text = displayMessage,
                    style = TextStyle(fontSize = 24.sp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    GateSystemTheme {
        MainScreen(requestValue = "Sample Request", approvalMessage = "", showApprovalMessage = false, onWriteData = {})
    }
}
