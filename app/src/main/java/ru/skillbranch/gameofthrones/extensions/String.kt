package ru.skillbranch.gameofthrones.extensions

fun String.getLastPathFromUrl(): String {
    return this.split("/").last()
}