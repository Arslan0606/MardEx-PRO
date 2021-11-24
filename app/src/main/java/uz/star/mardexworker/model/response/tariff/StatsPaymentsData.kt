package uz.star.mardexworker.model.response.tariff

import uz.star.mardexworker.model.response.login.Payment

data class StatsPaymentsData(
    val _id: String,
    val payments: List<Payment>
)