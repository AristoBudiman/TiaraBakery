package com.example.tiarabakery.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tiarabakery.R
import com.example.tiarabakery.pages.CartPage
import com.example.tiarabakery.pages.OrderPage
import com.example.tiarabakery.pages.HomePage
import com.example.tiarabakery.pages.ProfilePage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {

    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Cart", Icons.Default.ShoppingCart),
        NavItem("Favorite", Icons.Default.Favorite),
        NavItem("Profile", Icons.Default.Person),
    )

    var selectedIndex by rememberSaveable {
        mutableStateOf(0)
    }

    Scaffold (
        bottomBar = {
            NavigationBar (
                containerColor = colorResource(id = R.color.white)
            ){
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = index==selectedIndex,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.label,
                                tint = if (index==selectedIndex) colorResource(id = R.color.light_brown) else colorResource(id = R.color.brown)
                            )

                        },

                        label = {Text(
                            text = navItem.label,
                            color = if (index == selectedIndex) colorResource(id = R.color.light_brown) else colorResource(id = R.color.brown),
                            fontFamily = FontFamily(Font(R.font.catamaran_medium)),
                        )},
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                    )
                }
            }
        }
    ){
        ContentScreen(modifier = modifier.padding(it), selectedIndex, navController)
    }
}

//ContentScreen tambah navController
@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex : Int, navController: NavController) {

    when (selectedIndex){
        0 -> HomePage(modifier)
        1 -> CartPage(modifier)
        2 -> OrderPage(modifier)
        3 -> ProfilePage(modifier, navController)

    }
}

data class NavItem (
    val label : String,
    val icon : ImageVector
)