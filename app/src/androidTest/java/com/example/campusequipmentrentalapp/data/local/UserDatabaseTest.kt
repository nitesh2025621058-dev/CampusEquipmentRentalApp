package com.example.campusequipmentrentalapp.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.campusequipmentrentalapp.data.UserRepository
import com.example.campusequipmentrentalapp.model.UserRole
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDatabaseTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun seedAndJoinAndLookup() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addCallback(AppDatabase.seedUsers).build()
        try {
            val dao = database.userDao()
            assertEquals(4, dao.getUsersAll().size)
            UserRepository().userList.forEach { expected ->
                val actual = dao.getUserId(expected.userId)!!
                assertEquals(expected.name, actual.name)
                assertEquals(expected.department, actual.department)
                assertEquals(expected.role, actual.role)
            }
            assertNull(dao.getUserId("unknown"))
            for (role in UserRole.values()) {
                val user = UserEntity(userId = "new_${role.name}", name = "신규 회원", department = "테스트학과", role = role)
                assertTrue(dao.insertUserIfAbsent(user))
                assertFalse(dao.insertUserIfAbsent(user.copy(name = "중복 회원")))
                val stored = dao.getUserId(user.userId)!!
                assertTrue(stored.id > 0)
                assertEquals(user, stored.copy(id = 0))
            }
            assertEquals(7, dao.getUsersAll().size)
        } finally {
            database.close()
        }
    }

    @Test
    fun migrationPreservesRentalsAndReopeningDoesNotDuplicateUsers() = runBlocking {
        val name = "user-migration-${System.nanoTime()}.db"
        try {
            context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null).use { old ->
                old.execSQL("CREATE TABLE rentals (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, equipmentId INTEGER NOT NULL, userId TEXT NOT NULL, rentalDays INTEGER NOT NULL, purpose TEXT NOT NULL, status TEXT NOT NULL)")
                old.execSQL("INSERT INTO rentals VALUES (1, 2, '20260010', 3, '수업', '대여중')")
                old.version = 1
            }
            fun open() = Room.databaseBuilder(context, AppDatabase::class.java, name)
                .addMigrations(AppDatabase.migration1To2)
                .addCallback(AppDatabase.seedUsers).build()
            open().let { db ->
                try {
                    assertEquals(4, db.userDao().getUsersAll().size)
                    assertEquals("수업", db.rentalDao().getAll().single().purpose)
                    assertTrue(db.userDao().insertUserIfAbsent(UserEntity(userId = "joined", name = "가입자", department = "학과", role = UserRole.STUDENT)))
                } finally {
                    db.close()
                }
            }
            open().let { db ->
                try {
                    assertEquals(5, db.userDao().getUsersAll().size)
                    assertNotNull(db.userDao().getUserId("joined"))
                    assertEquals(1, db.rentalDao().getAll().size)
                } finally {
                    db.close()
                }
            }
        } finally {
            context.deleteDatabase(name)
        }
    }
}
