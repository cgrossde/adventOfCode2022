import java.io.File

// Object-oriented solution
abstract class Resource(open var name: String, internal open var parent: Directory?) {
    abstract fun size(): Int
}

data class FsFile(val size: Int, override var name: String, override var parent: Directory?) : Resource(name, parent) {
    override fun toString(): String {
        return "<File> ${this.name} (size=${this.size})"
    }

    override fun size(): Int {
        return this.size
    }
}

data class Directory(
    override var name: String, override var parent: Directory?, val content: MutableList<Resource> = mutableListOf()
) : Resource(name, parent) {


    private var cachedSize: Int = -1

    override fun size(): Int {
        if (this.cachedSize == -1)
            this.cachedSize = content.sumOf { resource -> resource.size() }
        return this.cachedSize
    }

    override fun toString(): String {
        return "<Dir> ${this.name} (contents=${this.content.size})"
    }

    fun subDirs() : List<Directory> {
        val dirs = this.content.filterIsInstance<Directory>()
        return dirs.plus(dirs.flatMap { it.subDirs() })
    }
}

class Terminal {
    private val input: MutableList<String>
    private var cwd: Directory
    private val fs: Directory
    private val fsMaxSize: Int

    constructor(input: MutableList<String>, fsMaxSize: Int = 0, fsRoot: Directory = Directory("/", null, mutableListOf())) {
        this.input = input
        this.fs = fsRoot
        this.fsMaxSize = fsMaxSize
        this.cwd = fsRoot
    }

    fun simulate() {
        while (input.isNotEmpty()) {
            val parts = input.removeFirst().split(" ")
            execute(parts[1], parts.getOrElse(2) { "" })
        }
    }

    fun sizeOfDirsLessThan100000(): Int {
        return this.fs.subDirs().filter { it.size() < 100000 }.sumOf { it.size() }
    }

    fun findSmallestDirGiving(desiredFreeSpace: Int): Int  {
        val currentlyUsedSpace = this.fs.size()
        val minDirSizeToDelete = desiredFreeSpace - (this.fsMaxSize - currentlyUsedSpace)
        return this.fs.subDirs().sortedBy { it.size() }.filter { it.size() > minDirSizeToDelete }.first().size()
    }

    private fun execute(command: String, argument: String) {
        when (command) {
            "cd" -> this.changeDir(argument)
            "ls" -> this.readLsOutput()
        }
    }

    private fun readLsOutput() {
        while (input.isNotEmpty() && !input.first().startsWith("$"))
            readLsLine(input.removeFirst())
    }

    private fun readLsLine(line: String) {
        val lsOutputRegex = """(\w+) ([0-9a-zA-z\.]+)""".toRegex()
        val (typeSize, name) = lsOutputRegex
            .matchEntire(line)
            ?.destructured
            ?: throw IllegalArgumentException("Could not parse ls output $line")
        when (typeSize) {
            "dir" -> cwd.content.add(Directory(name = name, parent = cwd))
            else -> cwd.content.add(FsFile(typeSize.toInt(), name, cwd))
        }
    }

    private fun changeDir(dirName: String) {
        this.cwd = when (dirName) {
            "/" -> fs
            ".." -> this.cwd.parent ?: throw IllegalStateException("Tried to cd out of root '/'")
            else -> (cwd.content.find { it is Directory && it.name == dirName }
                ?: throw IllegalAccessException("Dir doesn't exist: $dirName")) as Directory
        }
    }
}

fun main() {
    val input = File("src", "Day07.txt").readLines()
    val terminal = Terminal(input.toMutableList(), 70_000_000)
    terminal.simulate()
    println("Part1: ${terminal.sizeOfDirsLessThan100000()}")
    val smallestDirFreeingEnoughSpace = terminal.findSmallestDirGiving(30_000_000)
    println("Part2: $smallestDirFreeingEnoughSpace")
}




