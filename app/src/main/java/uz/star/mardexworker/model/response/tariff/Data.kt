package uz.star.mardexworker.model.response.tariff

data class Data(
    val nomi: String,
    val payments: List<PaymentsData>?
)