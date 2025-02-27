package com.example.main.ui.screens


import androidx.compose.ui.graphics.vector.ImageVector
import com.example.main.R


// hier "Verwaltungsinfo" zu allen Bildschirmen listen
// ----------------------------------------------------------------

sealed class MyScreens(
    val route: String,
    val titleID: Int = R.string.emptyString,
    val labelID: Int = R.string.emptyString,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
    val showBackArrow: Boolean = false
) {

    // BottomNavScreens benötigen Title, Label, Icons
    // ----------------------------------------------------------------



    // FullScreens benötigen keine Icons und kein Label;
    // dafür aber showBackArrow = true für den Zurück Pfeil in der TopBar
    // ----------------------------------------------------------------

    object FullScreen1 : MyScreens(
        route = "fullscreen1",
        titleID = R.string.FullScreen1Title,

        )

    object FullScreen2 : MyScreens(
        route = "fullscreen2",
        titleID = R.string.FullScreen2Title,
        showBackArrow = true,
    )

    // Dialog Screens benötigen nur die route
    // ----------------------------------------------------------------


    object AlertDialog : MyScreens(
        route = "alert_dialog",
    )


    companion object {
        val allScreens =
            listOf(FullScreen1, FullScreen2, AlertDialog)

        val bottomBarScreens = emptyList<MyScreens>()

        val startDestination = FullScreen1.route

        fun fromRoute(route: String): MyScreens? =
            allScreens.firstOrNull { it.route == route }
    }

}



