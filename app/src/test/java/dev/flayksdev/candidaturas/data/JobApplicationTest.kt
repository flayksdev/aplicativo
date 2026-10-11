package dev.flayksdev.candidaturas.data

import org.junit.Assert.assertEquals
import org.junit.Test

class JobApplicationTest {

    private fun app(id: Long, company: String, role: String, status: Status, at: Long) =
        JobApplication(id = id, company = company, role = role, status = status, createdAt = at)

    private val sample = listOf(
        app(1, "Acme", "Desenvolvedor React", Status.SENT, at = 100),
        app(2, "Beta", "Engenheiro Python", Status.INTERVIEW, at = 300),
        app(3, "Gama", "Desenvolvedor Node", Status.SENT, at = 200),
        app(4, "Delta", "Desenvolvedor Android", Status.REJECTED, at = 400),
    )

    @Test
    fun stats_countsTotalAndByStatus() {
        val stats = sample.stats()
        assertEquals(4, stats.total)
        assertEquals(2, stats.byStatus[Status.SENT])
        assertEquals(1, stats.byStatus[Status.INTERVIEW])
        assertEquals(0, stats.byStatus[Status.OFFER])
        assertEquals(1, stats.byStatus[Status.REJECTED])
    }

    @Test
    fun filtered_byStatus() {
        val result = sample.filtered(Status.SENT, "")
        assertEquals(listOf(3L, 1L), result.map { it.id })
    }

    @Test
    fun filtered_byQueryIgnoresCaseAndSpaces() {
        val result = sample.filtered(null, "  PYTHON ")
        assertEquals(listOf(2L), result.map { it.id })
    }

    @Test
    fun filtered_sortsNewestFirst() {
        val result = sample.filtered(null, "")
        assertEquals(listOf(4L, 2L, 3L, 1L), result.map { it.id })
    }

    @Test
    fun filtered_combinesStatusAndQuery() {
        val result = sample.filtered(Status.SENT, "node")
        assertEquals(listOf(3L), result.map { it.id })
    }
}
