package uz.star.mardexworker.model.response.tariff.statsrvmodel

data class StatsOuterModel(
    var amount: Long = 0,
    var date: Long,
    val data: List<StatsInnerModel>
)