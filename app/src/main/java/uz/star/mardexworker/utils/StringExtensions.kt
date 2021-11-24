package uz.star.mardexworker.utils

import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * Created by Botirali Kozimov on 28/05/2021
 */

fun Long.toCurrency(): String {
    val formatter: NumberFormat = DecimalFormat("#,###")
    val formattedNumber: String = formatter.format(this)
    return "$formattedNumber UZS"
}