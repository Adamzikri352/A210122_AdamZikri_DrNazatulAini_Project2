package com.example.a210122_nazatul_lab1

import kotlinx.coroutines.flow.Flow

// ------------------------------------------------------------------
// Lab 5 — Repository
// ------------------------------------------------------------------


class ApplianceRepository(
    private val applianceDao: ApplianceDao,
    private val billGoalDao: BillGoalDao
) {

    // ---- Appliances ------------------------------------------------

    fun getAppliancesForUser(username: String): Flow<List<ApplianceEntity>> =
        applianceDao.getAppliancesForUser(username)


    suspend fun addAppliance(appliance: ApplianceEntity) =
        applianceDao.insert(appliance)


    suspend fun deleteAppliance(appliance: ApplianceEntity) =
        applianceDao.delete(appliance)

    // ---- Bill Goal -------------------------------------------------

    fun getGoalForUser(username: String): Flow<BillGoalEntity?> =
        billGoalDao.getGoalForUser(username)


    suspend fun setBillGoal(username: String, goalRm: Double) =
        billGoalDao.upsert(BillGoalEntity(username = username, goalRm = goalRm))
}