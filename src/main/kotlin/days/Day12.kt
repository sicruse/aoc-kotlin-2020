package days

import java.lang.IllegalArgumentException
import kotlin.Throws
import kotlin.math.absoluteValue

class Day12 : Day(12) {

    private val course: List<ManouveringInstruction> by lazy {
        inputList.map { code -> ManouveringInstruction.fromText(code) }
    }

    data class ManouveringInstruction(val maneuver: Maneuver, val amount: Int) {
        companion object {
            @Throws
            fun fromText(code: String): ManouveringInstruction {
                val maneuver = code.take(1)
                val amount = code.drop(1).toInt()
                return ManouveringInstruction(Maneuver.valueOf(maneuver), amount)
            }
        }
    }

    data class Position(val east: Int, val north: Int)
    data class PositionWithHeading(val position: Position, val heading: Int)

    enum class Maneuver {
        N {
            override fun maneuverShip(from: PositionWithHeading, amount: Int): PositionWithHeading =
                PositionWithHeading(Position(from.position.east, from.position.north + amount), from.heading )
            override fun maneuverShipByWaypoint(waypoint: Position, ship: Position, amount: Int) =
                Pair( Position(waypoint.east, waypoint.north + amount), ship )
        },
        E {
            override fun maneuverShip(from: PositionWithHeading, amount: Int): PositionWithHeading =
                PositionWithHeading(Position(from.position.east + amount, from.position.north), from.heading )
            override fun maneuverShipByWaypoint(waypoint: Position, ship: Position, amount: Int) =
                Pair( Position(waypoint.east + amount, waypoint.north), ship)
        },
        S {
            override fun maneuverShip(from: PositionWithHeading, amount: Int): PositionWithHeading =
                PositionWithHeading(Position(from.position.east, from.position.north - amount), from.heading )
            override fun maneuverShipByWaypoint(waypoint: Position, ship: Position, amount: Int) =
                Pair( Position(waypoint.east, waypoint.north - amount), ship)
        },
        W {
            override fun maneuverShip(from: PositionWithHeading, amount: Int): PositionWithHeading =
                PositionWithHeading(Position(from.position.east - amount, from.position.north), from.heading )
            override fun maneuverShipByWaypoint(waypoint: Position, ship: Position, amount: Int) =
                Pair( Position(waypoint.east - amount, waypoint.north), ship)
        },
        L {
            override fun maneuverShip(from: PositionWithHeading, amount: Int): PositionWithHeading =
                PositionWithHeading(Position(from.position.east, from.position.north), (360 + from.heading - amount) % 360 )
            override fun maneuverShipByWaypoint(waypoint: Position, ship: Position, amount: Int): Pair<Position, Position> {
                return Pair(simpleRotateLeft(waypoint, amount), ship)
            }
        },
        R {
            override fun maneuverShip(from: PositionWithHeading, amount: Int): PositionWithHeading =
                PositionWithHeading(Position(from.position.east, from.position.north), (from.heading + amount) % 360 )
            override fun maneuverShipByWaypoint(waypoint: Position, ship: Position, amount: Int): Pair<Position, Position> {
                return Pair(simpleRotateRight(waypoint, amount), ship)
            }
        },
        F {
            override fun maneuverShip(from: PositionWithHeading, amount: Int): PositionWithHeading {
                val northInc = amount * when(from.heading) {
                    0 -> 1
                    180 -> -1
                    else -> 0
                }
                val eastInc = amount * when(from.heading) {
                    90 -> 1
                    270 -> -1
                    else -> 0
                }
                return PositionWithHeading(Position(from.position.east + eastInc, from.position.north + northInc), from.heading )
            }
            override fun maneuverShipByWaypoint(waypoint: Position, ship: Position, amount: Int) =
                Pair( waypoint, Position(ship.east + waypoint.east * amount, ship.north + waypoint.north * amount))
        };

        @Throws
        protected fun simpleRotateLeft(input: Position, amount: Int): Position =
            when (amount) {
                90 -> Position(-input.north, input.east)
                180 -> Position(-input.east, -input.north)
                270 -> Position(input.north, -input.east)
                else -> throw IllegalArgumentException("Bad rotation angle...")
            }

        @Throws
        protected fun simpleRotateRight(input: Position, amount: Int): Position =
            when (amount) {
                90 -> Position(input.north, -input.east)
                180 -> Position(-input.east, -input.north)
                270 -> Position(-input.north, input.east)
                else -> throw IllegalArgumentException("Bad rotation angle...")
            }

        abstract fun maneuverShip(from: PositionWithHeading, amount: Int): PositionWithHeading
        abstract fun maneuverShipByWaypoint(waypoint: Position, ship: Position, amount: Int): Pair<Position, Position>
    }

    private fun navigate(course: List<ManouveringInstruction>): Sequence<PositionWithHeading> = sequence {
        var currentPosition = PositionWithHeading(Position(0, 0), 90)
        for (instruction in course) {
            val newPosition = instruction.maneuver.maneuverShip(currentPosition, instruction.amount)
            currentPosition = newPosition
            yield(newPosition)
        }
    }

    private fun navigateByWaypoints(course: List<ManouveringInstruction>): Sequence<Position> = sequence {
        var currentWaypointPosition = Position(10, 1)
        var currentShipPosition = Position(0, 0)
        for (instruction in course) {
            val (newWaypointPosition, newShipPosition) = instruction.maneuver.maneuverShipByWaypoint(currentWaypointPosition, currentShipPosition, instruction.amount)
            currentWaypointPosition = newWaypointPosition
            currentShipPosition = newShipPosition
            yield(newShipPosition)
        }
    }

    override fun partOne(): Any {
        val finalPosition = navigate(course).last()
        return finalPosition.position.north.absoluteValue + finalPosition.position.east.absoluteValue
    }

    override fun partTwo(): Any {
        val finalPosition = navigateByWaypoints(course).last()
        return finalPosition.north.absoluteValue + finalPosition.east.absoluteValue
    }
}
