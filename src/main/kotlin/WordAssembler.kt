fun main() {
    /*
     * Get all the cards.
     * Compare each fragment on the card to the fragments on the remaining cards.
     * Create all combinations with the fragment on the front and back.
     * Consider partial combinations, since cards can cover up parts of fragments.
     * Compare the created combinations to the original word list.
     */
    val matchedWords = mutableSetOf<String>()
    val allWords = getAllWords()
    val cards = getCards()
    cards.forEachIndexed { index, fragments ->
        val allOtherFragments = cards.minus(setOf(fragments)).flatten().toSet()

        val potentialWords = mutableSetOf<String>()
        fragments.toSet().forEach { cardFragment ->
            allOtherFragments.forEach { otherFragment ->
                potentialWords.add(cardFragment + otherFragment)
                potentialWords.add(otherFragment + cardFragment)
                potentialWords.addAll(makeWordsByCovering(cardFragment, otherFragment))
                potentialWords.addAll(makeWordsByCovering(otherFragment, cardFragment))
            }
        }

        val cardName = "Card ${index + 1}"
        val matches = allWords.filter { word -> potentialWords.contains(word) }
        println("$cardName: ${matches.size}")
        matchedWords.addAll(matches)
    }

    println()
    matchedWords.forEach { println(it) }
    println(matchedWords.size)
    println()

    /*
     * Figure out what fragments are not contributing much.
     * Get all the fragments from all the cards.
     * Check how many times they are contained in the matched words list.
     * This is a naive strategy to get a rough estimate.
     */
    val fragmentContribution = mutableMapOf<String, Int>()
    cards.flatten().toSet().forEach { fragment ->
        fragmentContribution[fragment] = matchedWords.count { word -> word.contains(fragment) }
    }

    fragmentContribution.entries.sortedBy { it.value }.forEach {
        println("${it.key}: ${it.value}")
    }
}

/*
 * Put one fragment over the other, allowing all partial covers.
 * Don't worry about tucking under, since that will happen when the fragments are reversed.
 * Assume that the maximum size of a fragment is 3.
 */
fun makeWordsByCovering(topFragment: String, bottomFragment: String): Set<String> {
    val potentialWords = mutableSetOf<String>()

    if (bottomFragment.length > 2) {
        potentialWords.add(topFragment + bottomFragment.substring(1))
        potentialWords.add(bottomFragment.substring(0, 1) + topFragment)
    }

    if (bottomFragment.length > 1) {
        potentialWords.add(topFragment + bottomFragment.last())
        potentialWords.add(bottomFragment.first() + topFragment)
    }

    return potentialWords
}
