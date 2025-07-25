package cz.kudladev.vehicletracking.core.ui.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.model.Role
import cz.kudladev.vehicletracking.model.User
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UserCard(
    modifier: Modifier = Modifier,
    user: User,
    onClick: () -> Unit
){
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = user.role.value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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
    maximumDistance = 0,
    overallDistance = 0
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