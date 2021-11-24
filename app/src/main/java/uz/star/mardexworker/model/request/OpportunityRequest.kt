package uz.star.mardexworker.model.request

import com.google.gson.annotations.SerializedName

data class OpportunityRequest(

	@field:SerializedName("type")
	val type: String? = null
)
