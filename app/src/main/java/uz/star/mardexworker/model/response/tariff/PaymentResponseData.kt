package uz.star.mardexworker.model.response.tariff

data class PaymentResponseData(
    val data: ArrayList<Data>?,
    val success: Boolean,
    val allPaidPayments: List<StatsPaymentsData>?
)