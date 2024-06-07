package com.example.littlelemon

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.littlelemon.ui.theme.LittleLemonColor
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun Home(
    navigateTo: (Destinations) -> Unit = {}
) {
    val context: Context = LocalContext.current
    val database by lazy {
        MenuDatabase.getDatabase(context)
    }


    val menuItems by database.menuDao().getAllMenuItems().observeAsState()

    var searchPhrase by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("")    }

    // Filtrar la lista de elementos del menú si la consulta de búsqueda no está en blanco
    val filteredMenuItems = if (searchPhrase.isNotBlank()) {
        menuItems?.filter { it.title.contains(searchPhrase, ignoreCase = true)}
    } else {
        menuItems
    }

     val categoryList: List<String>? = menuItems?.groupBy { it.category }?.keys?.toList()


    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                alignment = Alignment.TopStart,
                modifier = Modifier
                    .padding(16.dp)
                    .height(66.dp)
                    .width(300.dp)

            )
            IconButton(onClick = { navigateTo(Profile) }) {

                Image(
                    painter = painterResource(id = R.drawable.baseline_person_64),
                    contentDescription = "Profile",
                )
            }
        }

        Column(
            modifier = Modifier
                .background(Color(0xFF495E57))
                .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 16.dp)
        )
        {
            Text(
                text = stringResource(id = R.string.title),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF4CE14),
                fontFamily = FontFamily.Serif
            )
            Text(
                text = stringResource(id = R.string.location),
                fontSize = 24.sp,
                color = Color(0xFFEDEFEE)
            )
            Row(
                modifier = Modifier
                    .padding(top = 18.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.description),
                    color = Color(0xFFEDEFEE),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(bottom = 28.dp)
                        .fillMaxWidth(0.6f)
                )
                Image(
                    painter = painterResource(id = R.drawable.upperpanelimage),
                    contentDescription = "Upper Panel Image",
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                )
            }

            TextField(
                value = searchPhrase,
                onValueChange = { searchPhrase = it },
                placeholder = {
                    Text("Search phrase")
                },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.padding(16.dp),
                leadingIcon = { Icon( imageVector = Icons.Default.Search, contentDescription = "") }
            )

        }

        if(categoryList != null){
            CategoryFilter(categoryList = categoryList, onClick = {categoryType -> category = categoryType})
        }


        if(category.isEmpty()){
            filteredMenuItems?.let { MenuItemsList(it) }
        }
        else {
            filteredMenuItems?.filter { it.category == category }
                ?.let { menuItems ->
                    MenuItemsList(menuItems)
                }
        }

    }

}

@Composable
fun CategoryFilter(categoryList: List<String>,onClick: (String) -> Unit) {
    Column(
    ) {
        Text(
            text = "Order for delivery!",
            modifier = Modifier.padding(8.dp)
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            categoryList.forEach {
                Button(
                    onClick = { onClick(it) },
                    modifier = Modifier
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LittleLemonColor.cloud)

                ) {
                    Text(
                        text = it.capitalize(Locale.getDefault()),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

    }

}

@Composable
fun MenuItemsList(
    items: List<MenuItem>
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(items = items) { menuItem ->
            Column {
                Text(
                    text = menuItem.title,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.weight(2f),
                        text = menuItem.description,
                    )

                    AsyncImage(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp),
                        model = menuItem.image,
                        contentDescription = null,
                    )
                }
                Text(
                    text = menuItem.price.toString()
                )

                Spacer(modifier = Modifier.padding(bottom = 16.dp))
            }

        }

    }
}


@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ListPreview() {
    MenuItemsList(
        listOf(
            MenuItem("1", "Title", "description ", 7.99, "ddd", "category"),
            MenuItem("nnnnnnn", "the jfijfdsvfdfvdfvfd", " csdcscscsdcdc", 7.99, "ddd", "category")
        )
    )
}


@Preview
@Composable
private fun HomePreview() {
    Home()
}


