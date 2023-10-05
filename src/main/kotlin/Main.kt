import java.io.File

interface codeParser{
    fun parseCode(filename: String): List<Node>
}

//One regex per line and check each
class markdownParser : codeParser{

    override fun parseCode(filename: String): List<Node> {

        val markdownFile = File(filename)

        val regexHeading1 = Regex("(#)(\\s)(.*)")

        val regexHeading2 = Regex("(##)(\\s)(.*)")

        val regexHeading3 = Regex("(###)(\\s)(.*)")

        val regexRegular = Regex("(\\w.*)")

        val regexBold = Regex("\\*\\*(.*?)\\*\\*")

        val regexItal = Regex("^\\*(?!\\*)(.*)\\*$")

        val regexBlock = Regex("(>)(\\s)(.*)")

        val regexLine = Regex("(^---$)")

        val nodes = mutableListOf<Node>()

        markdownFile.forEachLine{line ->
            if (regexHeading1.matches(line)){
                // = find the line that matched and get text parsed from regex -- elvis operator to empty string
                // add text & type to node to determine html format
                val text = regexHeading1.find(line)?.groupValues?.get(3) ?: ""
                nodes.add(markdownNode(text, 1))
            }

            if (regexHeading2.matches(line)){
                val text = regexHeading2.find(line)?.groupValues?.get(3) ?: ""
                nodes.add(markdownNode(text, 2))
            }

            if (regexHeading3.matches(line)){
                val text = regexHeading3.find(line)?.groupValues?.get(3) ?: ""
                nodes.add(markdownNode(text, 3))
            }

            if (regexBold.matches(line)){
                val text = regexBold.find(line)?.groupValues?.get(1) ?: ""
                nodes.add(markdownNode(text, 4))
            }

            if (regexItal.matches(line)){
                val text = regexItal.find(line)?.groupValues?.get(1) ?: ""
                nodes.add(markdownNode(text, 5))
            }

            if (regexBlock.matches(line)){
                val text = regexBlock.find(line)?.groupValues?.get(3) ?: ""
                nodes.add(markdownNode(text, 6))
            }

            if (regexLine.matches(line)){
                val text = regexLine.find(line)?.groupValues?.get(1) ?: ""
                nodes.add(markdownNode(text, 7))
            }

            if (regexRegular.matches(line)){
                val text = regexRegular.find(line)?.groupValues?.get(1) ?: ""
                nodes.add(markdownNode(text,8))
            }
        }
        return nodes
    }
}

open class Node(val text: String) {

    open fun toHTML(): String {
        return text
    }
}


class markdownNode(text: String, val type : Int) : Node(text) {

    override fun toHTML(): String {

        return when (type) {
            1 -> "<h1>$text</h1>"
            2 -> "<h2>$text</h2>"
            3 -> "<h3>$text</h3>"
            4 -> "<b>$text</b>"
            5 -> "<i>$text</i>"
            6 -> "<blockquote>$text</blockquote>"
            7 -> "<hr />$text</hr />"
            8 -> "$text"
            else -> text
        }
    }
}

fun main(args: Array<String>) {
    val markdownParser = markdownParser()
    val testFile = "tester.md"
    val nodes = markdownParser.parseCode(testFile)

    //joins string & converts all the nodes to their representation in html
    val htmlConversion = nodes.joinToString("\n"){

        it.toHTML()
    }

    //create output file and write the html conversion to it
    val htmlFile = "out.html"
    File(htmlFile).writeText(htmlConversion)


}