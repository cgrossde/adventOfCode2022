import java.io.File


fun <T> Iterable<T>.takeWhileInclusive(predicate: (T) -> Boolean): List<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

data class Tree(
    val size: Int,
    val row: Int,
    val col: Int,
    var top: Tree? = null,
    var right: Tree? = null,
    var bottom: Tree? = null,
    var left: Tree? = null
) {

    fun invisible(): Boolean {
        return top().any { it.size >= this.size }
                && right().any { it.size >= this.size }
                && bottom().any { it.size >= this.size }
                && left().any { it.size >= this.size }
    }

    fun scenicScore(): Int {
        return top().takeWhileInclusive { it.size < this.size }
            .count() * right().takeWhileInclusive { it.size < this.size }
            .count() * bottom().takeWhileInclusive { it.size < this.size }
            .count() * left().takeWhileInclusive { it.size < this.size }.count()
    }

    override fun toString(): String {
        return "Tree[${this.row},${this.col}] (size=${this.size})"
    }

    private fun top(): List<Tree> {
        return if (this.top != null) listOf(this.top!!) + this.top!!.top() else emptyList()
    }

    private fun right(): List<Tree> {
        return if (this.right != null) listOf(this.right!!) + this.right!!.right() else emptyList()
    }

    private fun bottom(): List<Tree> {
        return if (this.bottom != null) listOf(this.bottom!!) + this.bottom!!.bottom() else emptyList()
    }

    private fun left(): List<Tree> {
        return if (this.left != null) listOf(this.left!!) + this.left!!.left() else emptyList()
    }
}

fun main() {
    val input = File("src", "Day08.txt").readLines()
    val height = input.size
    val width = input.first().length
    // Fill grid with trees
    val grid: Array<Array<Tree>> = Array(height) { row ->
        Array(width) { col ->
            Tree(input[row][col].digitToInt(), row, col)
        }
    }
    // Associate trees
    grid.forEachIndexed { row, trees ->
        trees.forEachIndexed { col, tree ->
            if (row > 0) tree.top = grid[row - 1][col]
            if (row < height - 1) tree.bottom = grid[row + 1][col]
            if (col > 0) tree.left = grid[row][col - 1]
            if (col < width - 1) tree.right = grid[row][col + 1]
        }
    }

    val trees = grid.flatMap { it.asList() }
    val visibleTrees = trees.filter { !it.invisible() }
    val maxScenicScore = trees.maxOf { it.scenicScore() }

    println("Part 1: ${visibleTrees.size}")
    println("Part 2: $maxScenicScore")
}



