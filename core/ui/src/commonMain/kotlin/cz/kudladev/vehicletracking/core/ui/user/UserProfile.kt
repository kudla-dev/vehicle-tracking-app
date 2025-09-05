package cz.kudladev.vehicletracking.core.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.Image
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.model.User
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UserProfile(
    modifier: Modifier = Modifier,
    user: User,
    onClick: () -> Unit
){
    Box(
        modifier = modifier
            .clip(CircleShape)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape
            )
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
    ){
        when(user.profilePicture){
            null -> {
                Icon(
                    modifier = Modifier
                        .fillMaxSize(0.5f),
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            else -> {
                Image(
                    imageUrl = user.profilePicture!!.url ?: "",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview
@Composable
private fun UserProfilePreview() {
    AppTheme {
        UserProfile(
            modifier = Modifier.size(256.dp),
            user = testUser,
            onClick = {}
        )
    }
}