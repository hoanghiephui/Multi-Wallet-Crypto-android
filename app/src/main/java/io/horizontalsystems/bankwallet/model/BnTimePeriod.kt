package io.horizontalsystems.bankwallet.model

enum class BnTimePeriod (val value: String) {
    Minutes1("1m"),
    Minutes5("5m"),
    Minutes15("15m"),
    Hour1("1h"),
    Hour4("4h"),
    Day1("1d"),
    Day3("3d"),
    Week1("1w"),
    Month1("1M");
}
