package com.example.tiarabakery.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tiarabakery.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType

@Composable
fun BannerView(modifier: Modifier = Modifier) {

    var bannerList by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    LaunchedEffect(Unit){
        Firebase.firestore.collection("data")
            .document("banner")
            .get().addOnCompleteListener( ){
                bannerList = it.result.get("urls") as List<String>
            }
    }

    Column(
        modifier = modifier
    ) {

        val pagerState = rememberPagerState (){
            bannerList.size
        }

        HorizontalPager(
            state = pagerState,
            pageSpacing = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            Box (
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                AsyncImage(
                    model = bannerList.get(it),
                    contentDescription = "banner",
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        DotsIndicator(
            dotCount = bannerList.size,
            type = ShiftIndicatorType(
                DotGraphic(
                    color = colorResource(id = R.color.brown),
                    size = 8.dp
                )
            ),
            pagerState = pagerState,
        )
    }
}