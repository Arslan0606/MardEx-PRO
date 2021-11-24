package uz.star.mardexworker.model.response.regions

class SpinnerRegion(private val title: String, private val id: String) {
    override fun toString(): String {
        return title
    }

    fun getId(): String {
        return id
    }
}
