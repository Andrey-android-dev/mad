package ru.skillbranch.skillarticles.ui

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Type description here....
 *
 * Created by Andrey on 25.04.2021
 */
class AttrValue(@AttrRes val res: Int): ReadOnlyProperty<Context, Int> {

    private var value : Int? = null

    override fun getValue(thisRef: Context, property: KProperty<*>): Int {
        if (value == null) {
            val tv = TypedValue()
            if(thisRef.theme.resolveAttribute(res, tv, true))
                value = tv.data
            else
                throw Resources.NotFoundException("Resources with id $res not found")
        }
        return value!!
    }


}