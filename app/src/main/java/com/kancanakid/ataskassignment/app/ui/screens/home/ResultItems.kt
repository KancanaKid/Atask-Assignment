package com.kancanakid.ataskassignment.app.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kancanakid.ataskassignment.app.data.model.Expression
import com.kancanakid.ataskassignment.app.ui.theme.AtaskAssignmentTheme
import com.kancanakid.ataskassignment.app.ui.theme.Gray200
import com.kancanakid.ataskassignment.app.ui.theme.Gray300

/**
 * @author basyi
 * Created 6/6/2023 at 12:32 PM
 */

@Composable
fun ResultItems(result: Expression) {
    Box(
        modifier = Modifier
            .fillMaxWidth().padding(top = 5.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(BorderStroke(width = 1.dp, color = Gray300))
            .background(color = Gray200)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Expression : ${result.expression}",
                modifier = Modifier
                    .padding(2.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold)

            Text(
                "Result : ${result.result}",
                modifier = Modifier
                    .padding(2.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
@Preview
fun PreviewListItem() {
    AtaskAssignmentTheme {
        ResultItems(result = Expression("1+1","2"))
    }
}