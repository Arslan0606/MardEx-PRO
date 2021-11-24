package uz.star.mardexworker.model.response.tariff.statsrvmodel

import uz.star.mardexworker.data.models.tariff.statsrvmodel.Type

data class StatsInnerModel(
    val _id: String?,
    val amount: Long,
    val perform_time: Long?,
    val name: String,
    val type: Type
)