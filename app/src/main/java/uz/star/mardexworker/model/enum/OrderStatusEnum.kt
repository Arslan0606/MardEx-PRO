package uz.star.mardexworker.model.enum

/**
 * Created by Botirali Kozimov on 29/05/2021
 */

enum class OrderStatusEnum(val text: String) {
    STABLE("stable"),
    SUCCESS("success"),
    CANCEL_CLIENT("cancel_client"),
    CANCEL_USER("cancel_user"),
    CANCEL_ALL("cancel_all");
}

fun String.getOrderStatusEnum(): OrderStatusEnum {
    return when (this) {
        "stable" -> OrderStatusEnum.STABLE
        "success" -> OrderStatusEnum.SUCCESS
        "cancel_client" -> OrderStatusEnum.CANCEL_CLIENT
        "cancel_user" -> OrderStatusEnum.CANCEL_USER
        else -> OrderStatusEnum.CANCEL_ALL
    }
}