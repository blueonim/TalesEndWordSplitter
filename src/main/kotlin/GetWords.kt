import java.io.File

private const val THREES_PATH = "src/main/resources/threes"
private const val FOURS_PATH = "src/main/resources/fours"
private const val FIVES_PATH = "src/main/resources/fives"

fun getAllWords(): Set<String> = getWords(FOURS_PATH) + getWords(FIVES_PATH) + getWords(THREES_PATH)

fun getWords(path: String): Set<String> = File(path).readLines()
    .map { it.trim().lowercase() }
    .filter { it.isNotBlank() }
    .toSet()
