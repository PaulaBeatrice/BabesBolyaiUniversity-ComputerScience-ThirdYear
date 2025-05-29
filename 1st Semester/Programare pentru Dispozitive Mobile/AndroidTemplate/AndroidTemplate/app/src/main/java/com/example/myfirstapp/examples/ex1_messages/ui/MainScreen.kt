package com.example.myfirstapp.examples.ex1_messages.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.myfirstapp.base.SortKeyUtil
import com.example.myfirstapp.base.items.Group
import com.example.myfirstapp.base.items.ItemsListWithViewModelGroupBy
import com.example.myfirstapp.base.items.createItemsViewModel
import com.example.myfirstapp.base.ui.OnCloseFun
import com.example.myfirstapp.base.ui.components.ItemsListWithViewModel
import com.example.myfirstapp.base.ui.screens.GenericScreen
import com.example.myfirstapp.examples.ex1_messages.entity.Message
import java.util.Date

data class User(val name:String, val unread:Int=0): Group()

@Composable
fun MainScreen(onClose:OnCloseFun) {
    val itemsViewModel = createItemsViewModel<Message, Int>()

    GenericScreen(
        title="Messages"
    ){

        var selectedUser:String? by remember{ mutableStateOf(null) }

        if(selectedUser==null) {
            ItemsListWithViewModelGroupBy<Message, Int, User>(
                User::class,
                itemsViewModel,
                groupByKey = { User(name = it.sender) },
                groupAggregate = { user,message ->
                    val u = User(name=user.name, unread=user.unread+(if(message.read) 0 else 1))
                    u.count = user.count
                    u
                },
                sortKey = { SortKeyUtil.desc(it.unread) },
                onItemClick = { selectedUser = it.name },
                modifier = Modifier.padding(it)
            )
        }
        else{
            ItemsListWithViewModel<Message, Int>(Message::class,
                itemsViewModel,
                filter={ it.sender == selectedUser },
                sortKey = {SortKeyUtil.desc(it.created)},
                modifier=Modifier.padding(it),
                displayItem = { Text("${it.id}. ${it.text}\n${it.sender} ${Date(it.created)} ${it.read}") }
            )
        }
    }
}
