import java.io.File


const val TEXT_PATH = "res/tvende_ravne.txt"

val htmlPath = with(TEXT_PATH) {
    if (contains('.'))
        replaceAfterLast('.', "html")
    else
        plus(".html")
}


interface Text {
    fun toHTML()
}


class ShowSimpleText : Text {
    companion object {
        val file = File(htmlPath)
    }

    override fun toHTML() {
        file.createNewFile()
        file.writeText(
            """<html>
<body>
${File(TEXT_PATH).readText().replace("\n", "<br>")}
</body>
</html>"""
        )
    }
}


internal abstract class TextDecorator(private val textToBeDecorated: Text) : Text {
    override fun toHTML() {
        textToBeDecorated.toHTML()
    }
}


internal class Center(textToBeDecorated: Text) : TextDecorator(textToBeDecorated) {
    override fun toHTML() {
        super.toHTML()

        val file = ShowSimpleText.file
        val text = file.readText().replaceFirst("html", "html align=\"center\"")
        file.writeText(text)
    }
}


internal class AddTitle(textToBeDecorated: Text) : TextDecorator(textToBeDecorated) {
    override fun toHTML() {
        super.toHTML()

        val file = ShowSimpleText.file
        var lines = file.readLines()
        lines = lines.take(2) +
                "SVARTSOT<br>" +
                "<font size=6>TVENDE RAVNE</font><br>" +
                "FRA ALBUMET \"RAVNENES SAGA\"<br><br>" +
                lines.takeLast(lines.size - 2)
        file.writeText(lines.joinToString(separator = "\n"))
    }
}


internal class ChangeFont(
    textToBeDecorated: Text,
    private val font: String
) : TextDecorator(textToBeDecorated) {
    override fun toHTML() {
        super.toHTML()

        val file = ShowSimpleText.file
        val text = file.readText()
            .replaceFirst("<body>\n", "<body>\n<font face=\"$font\">")
        file.writeText(text)
    }
}


internal class AddAlbumCover(
    textToBeDecorated: Text,
    private val coverPath: String
) : TextDecorator(textToBeDecorated) {
    override fun toHTML() {
        super.toHTML()

        val file = ShowSimpleText.file
        var lines = file.readLines()
        lines = lines.take(2) +
                "<img src=\"$coverPath\" height=360 width=360><br><br>" +
                lines.takeLast(lines.size - 2)
        file.writeText(lines.joinToString(separator = "\n"))
    }
}


internal class SetBackgroundColor(
    textToBeDecorated: Text,
    private val color: String
) : TextDecorator(textToBeDecorated) {
    override fun toHTML() {
        super.toHTML()

        val file = ShowSimpleText.file
        val text = file.readText().replaceFirst("body", "body bgcolor=\"$color\"")
        file.writeText(text)
    }
}


fun main() {
    SetBackgroundColor(
        AddAlbumCover(
            ChangeFont(
                AddTitle(
                    Center(
                        ShowSimpleText()
                    )
                ),
                "Bookman Old Style"
            ),
            "ravnenes_saga.jfif"
        ),
        "#fff8dc"
    ).toHTML()
}
