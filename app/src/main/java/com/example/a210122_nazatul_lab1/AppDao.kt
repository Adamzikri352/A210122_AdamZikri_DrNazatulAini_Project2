package com.example.a210122_nazatul_lab1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow



@Dao
interface ApplianceDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appliance: ApplianceEntity)


    @Delete
    suspend fun delete(appliance: ApplianceEntity)


    @Query("SELECT * FROM appliances WHERE username = :username ORDER BY id ASC")
    fun getAppliancesForUser(username: String): Flow<List<ApplianceEntity>>
}


@Dao
interface BillGoalDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(goal: BillGoalEntity)


    @Query("SELECT * FROM bill_goals WHERE username = :username LIMIT 1")
    fun getGoalForUser(username: String): Flow<BillGoalEntity?>
}