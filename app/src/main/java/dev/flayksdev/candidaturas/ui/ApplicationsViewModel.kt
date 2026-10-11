package dev.flayksdev.candidaturas.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dev.flayksdev.candidaturas.data.ApplicationRepository
import dev.flayksdev.candidaturas.data.JobApplication
import dev.flayksdev.candidaturas.data.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ApplicationsViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = ApplicationRepository(app)

    private val _items = MutableStateFlow(repository.load())
    val items: StateFlow<List<JobApplication>> = _items.asStateFlow()

    var statusFilter by mutableStateOf<Status?>(null)
        private set
    var query by mutableStateOf("")
        private set

    fun setStatusFilter(status: Status?) {
        statusFilter = status
    }

    fun setQuery(text: String) {
        query = text
    }

    fun add(company: String, role: String, link: String) {
        val item = JobApplication(
            id = System.currentTimeMillis(),
            company = company.trim(),
            role = role.trim(),
            link = link.trim(),
        )
        change { it + item }
    }

    fun updateStatus(id: Long, status: Status) =
        change { list -> list.map { if (it.id == id) it.copy(status = status) else it } }

    fun delete(id: Long) = change { list -> list.filterNot { it.id == id } }

    private fun change(transform: (List<JobApplication>) -> List<JobApplication>) {
        _items.update(transform)
        repository.save(_items.value)
    }
}
