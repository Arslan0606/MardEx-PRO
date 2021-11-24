package uz.star.mardexworker.model.response.regions

import com.google.gson.annotations.SerializedName

data class CityResponse(

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: Title? = null,

	@field:SerializedName("city_id")
	val cityId: CityId? = null
)

data class CityId(

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: Title? = null
)

data class Title(

	@field:SerializedName("ru")
	val ru: String? = null,

	@field:SerializedName("uz")
	val uz: String? = null,

	@field:SerializedName("uz_kr")
	val uzKr: String? = null,

	@field:SerializedName("en")
	val en: String? = null
)
