import java.io.File

private const val FOURS_PATH = "src/main/resources/fours"
private const val FIVES_PATH = "src/main/resources/fives"
//private const val SIXES_PATH = "src/main/resources/sixes"

fun getAllWords(): Set<String> = getWords(FOURS_PATH) + getWords(FIVES_PATH)// + getWords(SIXES_PATH)

fun getWords(path: String): Set<String> = File(path).readLines()
    .map { it.trim().lowercase() }
    .filter { it.isNotBlank() }
    .toSet()
