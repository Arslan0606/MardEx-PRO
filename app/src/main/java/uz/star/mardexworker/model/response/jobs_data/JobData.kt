package uz.star.mardexworker.model.response.jobs_data

import com.google.gson.annotations.Expose
import uz.star.mardexworker.model.response.jobs_data.TitleData
import java.io.Serializable

/**
 * Created by Botirali Kozimov on 08/3/2021
 **/

data class JobData(
    val _id: String,
    val title: TitleData,
    val category_job: String,
    val pic: String,
    val createdAt: String,
    val _v: Int,
    @Expose
    var isSelected: Boolean = false
) : Serializable