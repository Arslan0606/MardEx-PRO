package uz.star.mardexworker.model.response.jobs_data

import java.io.Serializable

/**
 * Created by Kurganbayev Jasurbek on 12/1/2020
 **/

data class TitleData(
    val uz: String? = null,
    val uz_kr: String? = null,
    val en: String? = null,
    val ru: String? = null
) : Serializable