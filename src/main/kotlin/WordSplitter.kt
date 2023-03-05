import java.io.File
import java.lang.System.currentTimeMillis

private const val MIN_FRAGMENT_SIZE = 1
private const val MAX_FRAGMENT_SIZE = 3
private const val MIN_WORD_SIZE = 4
private const val MAX_WORD_SIZE = 6
private const val FRAGMENT_OUT_PREFIX = "src/main/output/fragments-"
private const val FRAGMENT_OUT_POSTFIX = ".csv"

// How many of each word fragment size is allowed.
private val SIZE_TO_LIMIT: Map<Int, Int> = mapOf(
    1 to 26,
    2 to 130,
    3 to 70
)
//TODO add blocklist

fun main() {
    val allWords = getAllWords()

    /*
     * Create all valid fragments from the word list.
     * Separate them by size.
     * Sort each set of fragments by how frequently they appear in the word list.
     * Keep the most frequent fragments, up to the specified limit.
     * Print the fragment along with the stats.
     */
    val fragments = mutableListOf<WordFragment>()
    mapWordFragmentsToSize(makeWordFragments(allWords)).entries
        .sortedBy { entry -> entry.key }
        .forEach { entry -> entry.value
            .sortedByDescending { it.totalCount() }
            .take(SIZE_TO_LIMIT.getOrDefault(entry.key, 0))
            .forEach {
                fragments.add(it)
                it.print()
            }
        }
    println()

    // Output fragment csv file
    writeFile(fragmentFilename(), fragments)

    // Combine the fragments back into words and compare against the original word list.
    val madeWords = makeWordsFromFragments("", fragments.map { it.fragment }.toSet(), allWords)
    val wordOccurrence = mutableMapOf<String, Int>()
    madeWords.forEach {
        val count = wordOccurrence.getOrDefault(it, 0).inc()
        wordOccurrence[it] = count
    }
    wordOccurrence.entries.sortedBy { it.key }.forEach { println("${it.key}, ${it.value}") }
    println()
    println("Word Count: ${wordOccurrence.size}")

    //TODO next steps
    // determine which fragments should go on the same card - don't appear in many words together
    // make a set of cards (front and back, and with orientation being left or right)
    // determine how many words can be made with the cards
}

fun fragmentFilename(): String = "$FRAGMENT_OUT_PREFIX${currentTimeMillis()}$FRAGMENT_OUT_POSTFIX"

fun writeFile(filename: String, fragments: List<WordFragment>) =
    File(filename).bufferedWriter().use { out ->
        out.write("Fragment, Left, Right")
        out.newLine()
        fragments.forEach {
            out.write(it.string())
            out.newLine()
        }
    }

//TODO keep track of which fragments appear frequently (or infrequently) together
fun makeWordsFromFragments(word: String, fragments: Set<String>, allWords: Set<String>): MutableList<String> {
    val foundWords = mutableListOf<String>()
    fragments.forEach {
        val newWord = word + it
        if (newWord.length < MIN_WORD_SIZE)  {
            foundWords.addAll(makeWordsFromFragments(newWord, fragments, allWords))
        } else if (newWord.length < MAX_WORD_SIZE) {
            if (allWords.contains(newWord)) foundWords.add(newWord)
            foundWords.addAll(makeWordsFromFragments(newWord, fragments, allWords))
        } else {
            if (allWords.contains(newWord)) foundWords.add(newWord)
        }
    }
    return foundWords
}

fun makeWordFragments(words: Set<String>): List<WordFragment> {
    val fragmentMap: MutableMap<String, WordFragment> = mutableMapOf()

    words.forEach words@{ word ->
        if (word.length < MIN_FRAGMENT_SIZE) return@words

        for (i in MIN_FRAGMENT_SIZE until word.length) {
            val pair = word.splitAtIndex(i)

            pair.first.let { if (it.validFragment()) fragmentMap.getOrPut(it) { WordFragment(it) }.incrementLeft() }
            pair.second.let { if (it.validFragment()) fragmentMap.getOrPut(it) { WordFragment(it) }.incrementRight() }
        }
    }

    return fragmentMap.values.toList()
}

fun mapWordFragmentsToSize(wordFragments: List<WordFragment>): Map<Int, List<WordFragment>> {
    val map: MutableMap<Int, MutableList<WordFragment>> = mutableMapOf()
    wordFragments.forEach { map.getOrPut(it.length()) { mutableListOf() }.add(it) }
    return map
}

fun String.validFragment() = length in MIN_FRAGMENT_SIZE..MAX_FRAGMENT_SIZE

fun String.splitAtIndex(index : Int) = take(index) to substring(index)
