import java.io.File

private const val CARDS_PATH = "src/main/resources/epiloguefragments0302.csv"

fun getCards(): List<List<String>> = File(CARDS_PATH).readLines()
    .map { it.trim().lowercase() }
    .filter { it.isNotBlank() }
    .map {
        it.split(",")
            .map { fragment -> fragment.trim().lowercase() }
            .filter { fragment -> fragment.isNotBlank() }
    }
    .toList()
