package com.example.tiarabakery.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.tiarabakery.AppUtil.getUserData
import com.example.tiarabakery.AppUtil.updateUserData
import com.example.tiarabakery.R
import com.example.tiarabakery.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


//ProfilePage tambah navController dan SignOut Button
@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController) {
    val currentUser = Firebase.auth.currentUser
    var userData by remember { mutableStateOf<UserModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    // State untuk data yang sedang diedit
    var editedName by remember { mutableStateOf("") }
    var editedPhone by remember { mutableStateOf("") }
    var editedAddress by remember { mutableStateOf("") }

    LaunchedEffect(key1 = currentUser?.uid) {
        if (currentUser != null) {
            getUserData(
                userId = currentUser.uid,
                onSuccess = { user ->
                    userData = user
                    editedName = user.name
                    editedPhone = user.phone
                    editedAddress = user.address
                    isLoading = false
                },
                onFailure = { exception ->
                    errorMessage = exception.message
                    isLoading = false
                }
            )
        } else {
            errorMessage = "User not authenticated"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.cream))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile",
                style = TextStyle(
                    fontSize = 25.sp,
                    fontFamily = FontFamily(Font(R.font.catamaran_medium)),
                    fontWeight = FontWeight.Bold
                )
            )

            if (!isEditing) {
                Button(
                    onClick = { isEditing = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.brown)
                    ),
                    modifier = Modifier.size(height = 36.dp, width = 80.dp)
                ) {
                    Text("Edit", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Error loading profile",
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            userData?.let { user ->
                if (isEditing) {
                    // Tampilan edit mode
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(  // <-- Gunakan ini untuk Material 3
                                focusedBorderColor = colorResource(id = R.color.brown),
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Email tidak bisa diedit
                        Text(
                            text = "Email: ${user.email}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.catamaran_medium))
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        OutlinedTextField(
                            value = editedPhone,
                            onValueChange = { editedPhone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(id = R.color.brown),
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editedAddress,
                            onValueChange = { editedAddress = it },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(id = R.color.brown),
                                unfocusedBorderColor = Color.Gray
                            ),
                            singleLine = false,
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    isEditing = false
                                    // Reset ke nilai semula
                                    editedName = user.name
                                    editedPhone = user.phone
                                    editedAddress = user.address
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.LightGray
                                )
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    if (currentUser != null) {
                                        val updatedData = mapOf(
                                            "name" to editedName,
                                            "phone" to editedPhone,
                                            "address" to editedAddress
                                        )

                                        updateUserData(
                                            userId = currentUser.uid,
                                            updatedData = updatedData,
                                            onSuccess = {
                                                userData = user.copy(
                                                    name = editedName,
                                                    phone = editedPhone,
                                                    address = editedAddress
                                                )
                                                isEditing = false
                                            },
                                            onFailure = { exception ->
                                                errorMessage = "Failed to update: ${exception.message}"
                                            }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.brown)
                                )
                            ) {
                                Text("Save")
                            }
                        }
                    }
                } else {
                    // Tampilan view mode
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        ProfileInfoItem(label = "Full Name", value = user.name)
                        ProfileInfoItem(label = "Email", value = user.email)
                        ProfileInfoItem(label = "Phone Number", value = user.phone)
                        ProfileInfoItem(label = "Address", value = user.address)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                Firebase.auth.signOut()
                navController.navigate("auth") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.brown)
            )
        ) {
            Text(text = "Log Out")
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
                fontFamily = FontFamily(Font(R.font.catamaran_medium))
            )
        )
        Text(
            text = value.ifEmpty { "Not set" },
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.catamaran_medium)),
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
        Divider(
            modifier = Modifier.padding(top = 8.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
    }
}