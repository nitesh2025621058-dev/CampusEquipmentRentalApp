package com.example.campusequipmentrentalapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface UserDao {

    /** 중복 ID 검사와 저장을 하나의 트랜잭션으로 처리 */
    @Transaction
    suspend fun insertUserIfAbsent(userEntity: UserEntity): Boolean {
        if (getUserId(userEntity.userId) != null) return false
        insertUser(userEntity)
        return true
    }

    /** 사용자 추가 */
    @Insert
    suspend fun insertUser(userEntity: UserEntity): Long

    /** 모든 사용자 조회 */
    @Query("SELECT * FROM users ORDER BY id DESC")
    suspend fun getUsersAll(): List<UserEntity>

    /** 사용자 아이디로 사용자 조회 */
    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserId(userId: String): UserEntity?
}
