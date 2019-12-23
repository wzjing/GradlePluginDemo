@file:JvmName("JsonUtil")

package com.wzjing

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlin.reflect.KClass

/**
 * @Author:    wangzijing
 * @Date:      2019/12/23
 */

val jsonUtil = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create()

public fun Any.toJson() = jsonUtil.toJson(this)

public fun <T : Any> String.fromJson(type: KClass<T>) = jsonUtil.fromJson(this, type.java)