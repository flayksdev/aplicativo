package dev.flayksdev.candidaturas.data

enum class Status(val label: String) {
    SENT("Enviada"),
    INTERVIEW("Entrevista"),
    OFFER("Oferta"),
    REJECTED("Recusada"),
}

data class JobApplication(
    val id: Long,
    val company: String,
    val role: String,
    val link: String = "",
    val status: Status = Status.SENT,
    val createdAt: Long = System.currentTimeMillis(),
)

data class Stats(val total: Int, val byStatus: Map<Status, Int>)

/** Contagem total e por status, usada nos filtros e no resumo da tela. */
fun List<JobApplication>.stats(): Stats =
    Stats(size, Status.entries.associateWith { s -> count { it.status == s } })

/** Filtra por status (null = todos) e por texto na empresa ou na vaga; mais recentes primeiro. */
fun List<JobApplication>.filtered(status: Status?, query: String): List<JobApplication> {
    val q = query.trim().lowercase()
    return filter { app ->
        (status == null || app.status == status) &&
            (q.isEmpty() ||
                app.company.lowercase().contains(q) ||
                app.role.lowercase().contains(q))
    }.sortedByDescending { it.createdAt }
}
