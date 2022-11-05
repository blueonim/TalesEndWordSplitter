class WordFragment(val fragment: String) {
    private var leftCount: Int = 0
    private var rightCount: Int = 0

    fun length() = fragment.length
    fun incrementLeft() = leftCount ++
    fun incrementRight() = rightCount ++
    fun totalCount() = leftCount + rightCount

    fun print() = println("$fragment, $leftCount, $rightCount")
}
