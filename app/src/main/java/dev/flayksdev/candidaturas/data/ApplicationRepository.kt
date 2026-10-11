package dev.flayksdev.candidaturas.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/** Guarda as candidaturas no aparelho (SharedPreferences, em JSON). Funciona offline. */
class ApplicationRepository(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences("candidaturas", Context.MODE_PRIVATE)

    fun load(): List<JobApplication> {
        val raw = prefs.getString(KEY, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                JobApplication(
                    id = o.getLong("id"),
                    company = o.getString("company"),
                    role = o.getString("role"),
                    link = o.optString("link", ""),
                    status = runCatching { Status.valueOf(o.getString("status")) }
                        .getOrDefault(Status.SENT),
                    createdAt = o.optLong("createdAt", 0L),
                )
            }
        } catch (e: JSONException) {
            emptyList()
        }
    }

    fun save(items: List<JobApplication>) {
        val array = JSONArray()
        items.forEach { a ->
            array.put(
                JSONObject()
                    .put("id", a.id)
                    .put("company", a.company)
                    .put("role", a.role)
                    .put("link", a.link)
                    .put("status", a.status.name)
                    .put("createdAt", a.createdAt),
            )
        }
        prefs.edit().putString(KEY, array.toString()).apply()
    }

    private companion object {
        const val KEY = "items"
    }
}
