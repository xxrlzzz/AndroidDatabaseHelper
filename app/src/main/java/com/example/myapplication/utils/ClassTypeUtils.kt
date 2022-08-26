package com.example.myapplication.utils

import java.lang.reflect.ParameterizedType

object ClassTypeUtils {

    /**
     * 获取目标类范型的真实类型
     * 比如ClassA<ClassB> 返回 ClassB
     *
     * @param transformClass 泛型类型
     * @param offset 第几个参数
     * @return 泛型参数
     */
    fun getGenericClass(transformClass: Class<*>, offset:Int = 0): Class<*> {
        val parameterized: ParameterizedType =
            getParameterizedType(transformClass)
        return parameterized.actualTypeArguments[offset] as Class<*>
    }

    private fun getParameterizedType(transformClass: Class<*>): ParameterizedType {
        val parameterized: ParameterizedType =
            if (transformClass.superclass != Any::class.java) {
                val type = transformClass.genericSuperclass
                type as ParameterizedType
            } else {
                val types = transformClass.genericInterfaces
                types[0] as ParameterizedType
            }
        return parameterized
    }
}