package dev.tuhkanens.liteskins.fetch

enum class SkinAPI(val url: String) {
    MOJANG_PROFILE_API("https://api.mojang.com/users/profiles/minecraft/"),
    MOJANG_SKIN_API("https://sessionserver.mojang.com/session/minecraft/profile/"),
    MINESKIN_SKIN_API("https://api.mineskin.org/get/uuid/");
}