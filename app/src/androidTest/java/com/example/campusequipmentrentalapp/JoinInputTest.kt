package com.example.campusequipmentrentalapp

import android.view.LayoutInflater
import android.view.ContextThemeWrapper
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JoinInputTest {
    @Test
    fun nameAndDepartmentAcceptKoreanComposition() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.Theme_CampusEquipmentRentalApp)
            val view = LayoutInflater.from(context).inflate(R.layout.fragment_join, null)
            listOf(R.id.et_name to "김민수", R.id.et_department to "컴퓨터소프트웨어학과").forEach { (id, value) ->
                val field = view.findViewById<EditText>(id)
                val connection = field.onCreateInputConnection(EditorInfo())!!
                connection.setComposingText(value.take(1), 1)
                connection.setComposingText(value, 1)
                connection.finishComposingText()
                assertEquals(value, field.text.toString())
            }
        }
    }
}
