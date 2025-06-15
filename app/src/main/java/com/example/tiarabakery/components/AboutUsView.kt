package com.example.tiarabakery.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Instagram
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.Smartphone
import com.example.tiarabakery.R

@Composable
fun AboutUsView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text("About Us", fontFamily = FontFamily(Font(R.font.catamaran_medium)), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        // Address
        Column {
            Text("Address", fontFamily = FontFamily(Font(R.font.catamaran_medium)),fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Jl. Semeru Utara IV, Tegalharjo,",fontFamily = FontFamily(Font(R.font.catamaran_medium)),)
            Text("Jebres, Surakarta, Jawa Tengah, Indonesia", fontFamily = FontFamily(Font(R.font.catamaran_medium)),)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Lihat di Google Maps",
                fontFamily = FontFamily(Font(R.font.catamaran_medium)),
                color = colorResource(id = R.color.light_brown),
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.app.goo.gl/kbUwHkjKciEQMM5G6"))
                    context.startActivity(intent)
                }
            )
        }

        // Working Hours
        Column {
            Text("Working Hours", fontFamily = FontFamily(Font(R.font.catamaran_medium)),fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Lucide.Clock, contentDescription = "Clock", tint = colorResource(id = R.color.brown))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Weekday: 6 am - 8 pm",fontFamily = FontFamily(Font(R.font.catamaran_medium)),)
                    Text("Weekend: 6 am - 5 pm",fontFamily = FontFamily(Font(R.font.catamaran_medium)),)
                }
            }
        }

        // Contact Us
        Column {
            Text("Contact Us", fontFamily = FontFamily(Font(R.font.catamaran_medium)),fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            ContactItem("Instagram", "@tiarabakerysolo", "https://www.instagram.com/tiarabakerysolo", Lucide.Instagram)
            ContactItem("TikTok", "@tiarabakerysolo", "https://www.tiktok.com/@tiarabakerysolo", Lucide.Smartphone)
            ContactItem("Email", "tiarabakerysolo@gmail.com", "https://mail.google.com/mail/?view=cm&fs=1&to=tiarabakerysolo@gmail.com", Lucide.Mail)
            ContactItem("WhatsApp", "082241900657", "https://wa.me/6282241900657", Lucide.Phone)
        }
    }
}

@Composable
fun ContactItem(label: String, value: String, url: String, icon: ImageVector) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
            .padding(vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = colorResource(id = R.color.brown)
            )
            Text(
                text = label,
                fontFamily = FontFamily(Font(R.font.catamaran_medium)),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(id = R.color.brown)
            )
        }
        Text(
            text = value,
            fontFamily = FontFamily(Font(R.font.catamaran_medium)),
            color = colorResource(id = R.color.light_brown),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 32.dp)
        )
    }
}

