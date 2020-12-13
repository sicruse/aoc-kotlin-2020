package days

class Day13 : Day(13) {
    data class Bus(val departure: Int, val frequency: Long)

    private val earliestPossibleDeparture: Long by lazy {
        inputList[0].toLong()
    }

    private val busses: List<Bus> by lazy {
        inputList[1].split(",").mapIndexedNotNull {
                departure, frequency -> if (frequency == "x") null else Bus(departure, frequency.toLong())
        }
    }

    private fun nextBusDepartureAfter(interval: Long): Long {
        val priorDeparture = interval * (earliestPossibleDeparture / interval)
        return if (priorDeparture == earliestPossibleDeparture) priorDeparture else priorDeparture + interval
    }

    override fun partOne(): Any {
        val departures = busses.map { nextBusDepartureAfter(it.frequency) }
        val best = departures.withIndex().minByOrNull { (_ , f) -> f }?.index!!
        return busses[best].frequency * (departures[best] - earliestPossibleDeparture)
    }

    override fun partTwo(): Any {
        var stepSize = busses.first().frequency
        var time = 0L
        busses.drop(1).forEach { (departure, frequency) ->
            while ((time + departure) % frequency != 0L) time += stepSize
            stepSize *= frequency
        }
        return time
    }
}
