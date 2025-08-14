package cz.kudladev.vehicletracking.core.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.model.Role
import cz.kudladev.vehicletracking.model.User
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UserCard(
    modifier: Modifier = Modifier,
    user: User,
    onClick: () -> Unit
){
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserProfile(
                    modifier = Modifier
                        .size(64.dp),
                    user = user,
                    onClick = onClick
                )
                Column {
                    Text(
                        text = stringResource(user.role.displayName).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.titleMedium
                    )
//                    Text(
//                        text = user.email,
//                        style = MaterialTheme.typography.labelMedium,
//                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                    )
                }
            }
            UserDistanceProgress(
                modifier = Modifier.padding(top = 8.dp),
                distance = user.overallDistance,
                maximumDistance = user.maximumDistance
            )
        }
    }
}

internal val testUser = User(
    id = "12345678-1234-5678-1234-567812345678",
    firstName = "John",
    lastName = "Doe",
    fullName = "John Doe",
    email = "john.doe@gmail.com",
    phoneNumber = "1234567890",
    role = Role.USER,
    maximumDistance = 2000,
    overallDistance = 500
)

@Preview
@Composable
fun UserCardPreview() {
    AppTheme {
        UserCard(
            user = testUser,
            onClick = {}
        )
    }
}