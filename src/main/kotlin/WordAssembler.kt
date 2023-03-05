fun main() {
    /*
     * Get all the cards.
     * Compare each fragment on the card to the fragments on the remaining cards.
     * Create all combinations with the fragment on the front and back.
     * Consider partial combinations, since cards can cover up parts of fragments.
     * Compare the created combinations to the original word list.
     */
    val cards = getCards()

    val potentialWords = mutableSetOf<String>()
    cards.forEach {
        val allOtherFragments = cards.minus(setOf(it)).flatten().toSet()

        it.toSet().forEach { cardFragment ->
            allOtherFragments.forEach { otherFragment ->
                /*
                 * Put the fragment over the other, allowing all partial covers.
                 * Don't worry about tucking under, since that will happen naturally when the check is reversed.
                 * Assume that the maximum size of a fragment is 3.
                 */
                potentialWords.add(cardFragment + otherFragment)
                potentialWords.add(otherFragment + cardFragment)

                if (otherFragment.length > 2) {
                    potentialWords.add(cardFragment + otherFragment.substring(1))
                    potentialWords.add(otherFragment.substring(0, 1) + cardFragment)
                }

                if (otherFragment.length > 1) {
                    potentialWords.add(cardFragment + otherFragment.last())
                    potentialWords.add(otherFragment.first() + cardFragment)
                }
            }
        }
    }

    val matchedWords = getAllWords().filter { potentialWords.contains(it) }
    matchedWords.forEach { println(it) }
    println(matchedWords.size)
    println()

    /*
     * Figure out what fragments are not contributing much.
     * Get all the fragments from all the cards.
     * Check how many times they are contained in the matched words list.
     */
    val fragmentContribution = mutableMapOf<String, Int>()
    cards.flatten().toSet().forEach { fragment ->
        fragmentContribution[fragment] = matchedWords.count { word -> word.contains(fragment) }
    }

    fragmentContribution.entries.sortedBy { it.value }.forEach {
        println("${it.key}: ${it.value}")
    }
}
