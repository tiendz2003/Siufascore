package com.jerry.ronaldo.siufascore.presentation.matches.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.presentation.ui.DetailTopAppBar
import com.jerry.ronaldo.siufascore.presentation.ui.Purple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMatchScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,

    ) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Purple,
        topBar = {
            DetailTopAppBar(
                onBackClick = onBackClick,
                match = TODO(),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                MatchHeaderCard()
            }
        }
    }
}

@Composable
fun MatchHeaderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //Hiển thị thười gian đếm ngược
            CountdownTimer(
                days = 50,
                hours = 10,
                minutes = 6,
                seconds = 46
            )
            Spacer(modifier = Modifier.height(24.dp))
            MatchSection()
            MatchInfo()
        }
    }
}

@Composable
fun MatchInfo() {
    TODO("Not yet implemented")
}

@Composable
fun MatchSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

    ) {

    }
}

@Preview
@Composable
fun MatchSectionPreview() {
    DetailTeamSection(
        isHome = true,
        teamName = "Liverpool",
        homeTeamColor = listOf(Color.Red, Color.Blue),
        awayTeamColor = listOf(Color.White, Color.Black),
        teamLogo = "dưqd"
    )
}

@Composable
fun DetailTeamSection(
    modifier:Modifier = Modifier,
    isHome: Boolean,
    teamName: String,
    homeTeamColor: List<Color>,
    awayTeamColor: List<Color>,
    teamLogo: String
) {
    Box(
        modifier.background(
            brush = Brush.horizontalGradient(
                colors = if (isHome) {
                    homeTeamColor
                } else {
                    awayTeamColor
                },
                startX = if (isHome) 0f else Float.POSITIVE_INFINITY,
                endX = if (isHome) Float.POSITIVE_INFINITY else 0f
            ) ,
        )
    ){
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if(isHome) Arrangement.Start else Arrangement.End
        ){
            if(isHome){
                AsyncImage(
                    model = teamLogo,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = teamName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            }else{
                AsyncImage(
                    model = teamLogo,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = teamName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun CountdownTimer(days: Int, hours: Int, minutes: Int, seconds: Int) {

}
