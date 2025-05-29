package com.example.myfirstapp.base.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.Rezervari_Android.ui.NumberField
import com.example.myfirstapp.base.Reflection.Companion.getMutableMemberProperties
import com.example.myfirstapp.base.items.Item
import com.example.myfirstapp.base.items.ItemDefaults
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

@Composable
fun<T: Item> ItemEditorFields(
    itemClass: KClass<T>,
    item:T,
    visibleProps:Array<String>?=null,
    itemEdited: (T)->Unit = {}
) {
    val mutableMemberProperties = itemClass.getMutableMemberProperties()
    val props =
        visibleProps
            ?.map{ name->mutableMemberProperties.find{it.name==name}!!}
            ?: mutableMemberProperties

    Log.d("ItemEditorFields", props.map{it->it.name}.joinToString())

    val values: MutableMap<String, Any?> = remember{
        mutableStateMapOf(
            *mutableMemberProperties.map{it.name to it.getter.call(item)}.toTypedArray()
        )
    }

    fun buildItemFromMap():T {
        var new_item = ItemDefaults.createCopy(itemClass, item)
        for(prop in props)
            prop.setter.call(new_item, values[prop.name])
        return new_item
    }

    //for(prop in props)  Log.d("PROP!!", prop.returnType.toString())
    //for(kv in values) Log.d("ItemEditorFields_Map", "${kv.key}=${kv.value}")

    val defaultFieldModifier = Modifier.padding(vertical = 5.dp)
    Column(modifier = Modifier.padding(horizontal=24.dp,vertical=0.dp)){

        for(prop in props){
            if(visibleProps!=null && prop.name !in visibleProps) continue

            when(prop.returnType.classifier){
                Int::class -> {
                    NumberField(
                        value = values[prop.name] as Int,
                        onValueChanged = { values[prop.name] = it; itemEdited(buildItemFromMap()) },
                        modifier = defaultFieldModifier,
                        label = prop.name
                    )
                }
                String::class ->
                    TextField(
                        value = values[prop.name] as String,
                        onValueChange = { values[prop.name] = it; itemEdited(buildItemFromMap()) },
                        label = { Text(prop.name) },
                        modifier = defaultFieldModifier.fillMaxWidth(),
                    )
                Boolean::class ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = defaultFieldModifier) {
                        Checkbox(
                            checked = values[prop.name] as Boolean,
                            onCheckedChange = { values[prop.name] = it; itemEdited(buildItemFromMap()) },
                        )
                        Text(text=prop.name)
                    }
            }
        }
    }
}