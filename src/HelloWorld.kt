import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


fun main() {
    // JSON ExchangeCode.ContractDate. CET Zone is important
    val deliveryDate = ZonedDateTime.of(2022, 3, 31, 0, 0, 0, 0, ZoneId.of("CET"))
    val startDateTime = deliveryDate.withDayOfMonth(1)
    val endDateTime = startDateTime.plusMonths(1)
    val hours = ChronoUnit.HOURS.between(startDateTime, endDateTime)
    println("Days between $startDateTime and $endDateTime: $hours")
}
