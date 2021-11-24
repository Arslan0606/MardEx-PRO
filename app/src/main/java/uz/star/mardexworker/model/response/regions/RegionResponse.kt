package uz.star.mardexworker.model.response.regions

import com.google.gson.annotations.SerializedName

data class RegionResponse(

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: Title? = null
)
