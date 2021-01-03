package days

class Day22 : Day(22) {

    private val firstPlayersCards: List<Int> by lazy {
        inputString
            .split("\\n\\n".toRegex())
            .first()
            .split(":\\n".toRegex())
            .last()
            .split("\\n".toRegex())
            .map { card -> card.toInt() }
    }

    private val secondPlayersCards: List<Int> by lazy {
        inputString
            .split("\\n\\n".toRegex())
            .last()
            .split(":\\n".toRegex())
            .last()
            .split("\\n".toRegex())
            .map { card -> card.toInt() }
    }

    /*
    * Before either player deals a card, if there was a previous round in this game that had exactly the same cards in the same order in the same players' decks,
    * the game instantly ends in a win for player 1. Previous rounds from other games are not considered. (This prevents infinite games of Recursive Combat,
    * which everyone agrees is a bad idea.)
    *
    * Otherwise, this round's cards must be in a new configuration; the players begin the round by each drawing the top card of their deck as normal.
    *
    * If both players have at least as many cards remaining in their deck as the value of the card they just drew, the winner of the round is determined by playing
    * a new game of Recursive Combat (see below).
    *
    * Otherwise, at least one player must not have enough cards left in their deck to recurse; the winner of the round is the player with the higher-value card.
    */

    enum class Winner { P1, P2 }

    fun play(deck1: ArrayDeque<Int>, deck2: ArrayDeque<Int>): Winner {
        val playHistory1 = mutableSetOf<Int>()
        val playHistory2 = mutableSetOf<Int>()

        while (deck1.isNotEmpty() && deck2.isNotEmpty() && !playHistory1.contains(deck1.hashCode()) && !playHistory2.contains(deck2.hashCode())) {
            playHistory1.add(deck1.hashCode())
            playHistory2.add(deck2.hashCode())

            val card1 = deck1.removeFirst()
            val card2 = deck2.removeFirst()

            when {
                card1 <= deck1.size && card2 <= deck2.size -> {
                    when (play(ArrayDeque(deck1.take(card1)), ArrayDeque(deck2.take(card2)))) {
                        Winner.P1 -> {
                            deck1.addLast(card1)
                            deck1.addLast(card2)
                        }
                        Winner.P2 -> {
                            deck2.addLast(card2)
                            deck2.addLast(card1)
                        }
                    }
                }
                card1 > card2 -> {
                    deck1.addLast(card1)
                    deck1.addLast(card2)
                }
                else -> {
                    deck2.addLast(card2)
                    deck2.addLast(card1)
                }
            }
        }

        return when {
            playHistory1.contains(deck1.hashCode()) || playHistory2.contains(deck2.hashCode()) -> Winner.P1
            deck1.isNotEmpty() -> Winner.P1
            else -> Winner.P2
        }
    }

    fun calculateResult(deck: ArrayDeque<Int>): Long {
        val hash = deck.reversed().foldIndexed(0L) { i, acc, v -> acc+(i+1)  * v }
        return hash
    }

    override fun partOne(): Any {
        val deck1 = ArrayDeque(firstPlayersCards)
        val deck2 = ArrayDeque(secondPlayersCards)

        while (deck1.isNotEmpty() && deck2.isNotEmpty()) {
            val card1 = deck1.removeFirst()
            val card2 = deck2.removeFirst()

            if (card1 > card2) {
                deck1.addLast(card1)
                deck1.addLast(card2)
            } else {
                deck2.addLast(card2)
                deck2.addLast(card1)
            }
        }

        val winningDeck = if (deck1.size > 0) deck1 else deck2
        return calculateResult(winningDeck)
    }

    override fun partTwo(): Any {
        val deck1 = ArrayDeque(firstPlayersCards)
        val deck2 = ArrayDeque(secondPlayersCards)

        val winningDeck = if (play(deck1, deck2) == Winner.P1) deck1 else deck2
        return calculateResult(winningDeck)
    }
}
