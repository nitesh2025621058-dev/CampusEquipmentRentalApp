package com.example.campusequipmentrentalapp.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.campusequipmentrentalapp.data.UserRepository


@Database(
    entities = [RentalEntity::class, UserEntity::class, EquipmentEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase()
{
    /**
     * RentalDao 연결 처리
     * */
    abstract fun rentalDao(): RentalDao

    /**
     * UserDao 연결 처리
     * */
    abstract fun userDao(): UserDao

    /**
     * EquipmentDao 연결 처리
     * */
    abstract fun equipmentDao(): EquipmentDao


    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        internal val migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, name TEXT NOT NULL, department TEXT NOT NULL, role TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS equipments (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, category TEXT NOT NULL, icon TEXT NOT NULL, status TEXT NOT NULL, maxRentalDays INTEGER NOT NULL, location TEXT NOT NULL, description TEXT NOT NULL)")
            }
        }

        internal val seedUsers = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.beginTransaction()
                try {
                    UserRepository().userList.forEach { user ->
                        db.execSQL(
                            "INSERT INTO users (userId, name, department, role) SELECT ?, ?, ?, ? WHERE NOT EXISTS (SELECT 1 FROM users WHERE userId = ?)",
                            arrayOf<Any>(user.userId, user.name, user.department, user.role.name, user.userId)
                        )
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            }
        }

        /** 앱 전체에서 같은 DB 를 사용 */
        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance?.let { return@synchronized it }
                val _instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "campus_rental_database"
                ).addMigrations(migration1To2)
                    .addCallback(seedUsers)
                    .build()
                instance = _instance
                _instance
            }
         }
        }
}
