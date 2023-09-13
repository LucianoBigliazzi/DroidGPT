package com.droidgpt.ui.common

import android.graphics.drawable.PictureDrawable
import android.icu.number.Precision
import android.icu.number.Scale
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

//@Composable
//fun DynamicSVGImage(
//    modifier: Modifier = Modifier,
//    svgImageString: String,
//    contentDescription: String,
//) {
//    val useDarkTheme = LocalDarkTheme.current.isDarkTheme()
//    val tonalPalettes = LocalTonalPalettes.current
//    var size by remember { mutableStateOf(IntSize.Zero) }
//    val pic by remember(useDarkTheme, tonalPalettes, size) {
//        mutableStateOf(
//            PictureDrawable(
//                SVG.getFromString(svgImageString.parseDynamicColor(tonalPalettes, useDarkTheme))
//                    .renderToPicture(size.width, size.height)
//            )
//        )
//    }
//
//    Row(
//        modifier = modifier
//            .aspectRatio(1.38f)
//            .onGloballyPositioned {
//                if (it.size != IntSize.Zero) {
//                    size = it.size
//                }
//            },
//    ) {
//        Crossfade(targetState = pic) {
//            RYAsyncImage(
//                contentDescription = contentDescription,
//                data = it,
//                placeholder = null,
//                error = null,
//            )
//        }
//    }
//}