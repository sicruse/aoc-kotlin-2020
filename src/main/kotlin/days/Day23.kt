package days

class Day23 : Day(23) {

    val initalCupLabels = inputString.map { c -> c.toString().toInt() }

    private class CupGame(sample: List<Int>, size: Int = sample.size): Items(size) {
        init {
            currentItem = items[sample.first()]
            val itemsInOrder = sample + (sample.size + 1 .. size)
            val lastItem = items[sample.last()]
            // apply item links
            itemsInOrder
                .map { id -> items[id] }
                .fold( lastItem ) { previousItem, cup ->
                    cup.also { previousItem.next = cup }
                }
            // link last item to first item
            items[itemsInOrder.last()].next = items[itemsInOrder.first()]
        }

        fun play(times: Int): Item {
            repeat(times) {
                val three: List<Item> = currentItem.next(3)
                moveItemsTo(insertionPoint(three.map { it.value }.toSet()), three)
                currentItem = currentItem.next
            }
            return items[1]
        }

        private fun insertionPoint(exempt: Set<Int>): Item {
            var candidate = currentItem.value - 1
            while(candidate in exempt || candidate == 0) {
                candidate = if(candidate == 0) items.size - 1 else candidate - 1
            }
            return items[candidate]
        }
    }

    private open class Items(size: Int) {
        val items: List<Item> = List(size + 1) { Item(it) }
        lateinit var currentItem: Item

        fun moveItemsTo(insertionPoint: Item, itemsToInsert: List<Item>) {
            val previousItem = insertionPoint.next
            currentItem.next = itemsToInsert.last().next
            insertionPoint.next = itemsToInsert.first()
            itemsToInsert.last().next = previousItem
        }
    }

    private class Item(val value: Int) {
        lateinit var next: Item

        fun next(n: Int): List<Item> =
            (1 .. n).runningFold(this) { cur, _ -> cur.next }.drop(1)

        fun values(): String = buildString {
            var current = this@Item.next
            while(current != this@Item) {
                append(current.value.toString())
                current = current.next
            }
        }
    }

    override fun partOne(): Any {
        return CupGame(initalCupLabels)
            .play(100)
            .values()
    }

    override fun partTwo(): Any {
        return CupGame(initalCupLabels, 1000000)
            .play(10000000)
            .next(2)
            .fold(1L) { acc, cup -> acc * cup.value }
    }
}
