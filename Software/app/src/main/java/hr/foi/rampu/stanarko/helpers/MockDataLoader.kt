package hr.foi.rampu.stanarko.helpers

import hr.foi.rampu.stanarko.database.FlatsDAO
import hr.foi.rampu.stanarko.database.TenantsDAO
import hr.foi.rampu.stanarko.entities.Flat
import hr.foi.rampu.stanarko.entities.Tenant
import kotlinx.coroutines.tasks.await

object MockDataLoader {
    suspend fun getFirebaseFlats(): MutableList<Flat> {
        val flatsDAO = FlatsDAO()
        val flats = mutableListOf<Flat>()
        val result = flatsDAO.getAllFlats().await()
        flats.addAll(result.toObjects(Flat::class.java))
        return flats
    }
    suspend fun getFirebaseFlatsByOwner(mail: String): MutableList<Flat> {
        val flatsDAO = FlatsDAO()
        val flats = mutableListOf<Flat>()
        val result = flatsDAO.getFlatsByOwnerMail(mail).await()
        flats.addAll(result.toObjects(Flat::class.java))
        return flats
    }

    suspend fun getFirebaseTenants(id: Int): List<Tenant> {
        val tenantsDAO = TenantsDAO()
        val tenants = mutableListOf<Tenant>()
        val result = tenantsDAO.getTenantsByFlatId(id).await()
        tenants.addAll(result.toObjects(Tenant::class.java))
        return tenants
    }

    suspend fun getTenantByMail(userMail : String) : Tenant {
        val tenantsDAO = TenantsDAO()
        return tenantsDAO.getTenant(userMail)!!
    }

    suspend fun getFirebaseTenantsByAdress(adresa: String): List<Tenant> {
        val tenantsDAO = TenantsDAO()
        val tenants = mutableListOf<Tenant>()
        val result = tenantsDAO.getTenantsByFlatAddress(adresa).await()
        tenants.addAll(result.toObjects(Tenant::class.java))
        return tenants
    }
}