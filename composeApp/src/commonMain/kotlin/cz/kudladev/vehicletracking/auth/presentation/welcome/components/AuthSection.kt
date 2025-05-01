package cz.kudladev.vehicletracking.auth.presentation.welcome.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AuthSection(
    onRegister: () -> Unit,
    onLogin: () -> Unit
){
    Box(
        modifier = Modifier
            .widthIn(500.dp,700.dp)
            .heightIn(max = 350.dp)
            .clip(shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(top = 16.dp)
    ){
        Text(
            "Welcome",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 350.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AuthButton(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                content = {
                    Text(
                        text = "Register",
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                contentColor = MaterialTheme.colorScheme.primaryContainer,
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = onRegister
            )
            Spacer(modifier = Modifier.padding(8.dp))
            AuthButton(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                content = {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.inversePrimary,
                onClick = onLogin
            )
        }
    }
}


@Composable
fun AuthButton(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    contentColor: Color,
    containerColor: Color,
    onClick: () -> Unit
){
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = contentColor,
            containerColor = containerColor
        )
    ) {
        content()
    }
}