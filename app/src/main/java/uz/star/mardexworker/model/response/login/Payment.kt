package uz.star.mardexworker.model.response.login

import uz.star.mardexworker.model.response.title.Title

data class Payment(
    var _id: String? = null,
    var price: Long = 0,
    var top: Int = 0,
    var call_count: Int = 0,
    var post: Int = 0,
    var deadline: Int = 0,
    var service_id: String? = null,
    var createdAt: Long? = null,
    var service: Title? = null
)