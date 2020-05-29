import java.io.File


// Путь к исходному файлу с текстом
const val TEXT_PATH = "res/tvende_ravne.txt"

// Путь к итоговому HTML-файлу
val htmlPath = with(TEXT_PATH) {
    if (contains('.'))
        replaceAfterLast('.', "html")
    else
        plus(".html")
}


/**
 * Интерфейс, который должны реализовать как декоратор, так и оборачиваемый объект.
 */
interface Text {
    fun apply()
}


/**
 * Класс, отвечающий за показ простого текста.
 */
class ShowSimpleText : Text {
    companion object {
        val file = File(htmlPath)
    }

    override fun apply() {
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


/**
 * Абстрактный класс-обёртка, от которого наследуются все декораторы.
 */
internal abstract class TextDecorator(private val textToBeDecorated: Text) : Text {
    override fun apply() {
        textToBeDecorated.apply()
    }
}


/**
 * Декоратор, отвечающий за центрирование текста.
 */
internal class Center(textToBeDecorated: Text) : TextDecorator(textToBeDecorated) {
    override fun apply() {
        super.apply()

        val file = ShowSimpleText.file
        val text = file.readText().replaceFirst("html", "html align=\"center\"")
        file.writeText(text)
    }
}


/**
 * Декоратор, отвечающий за добавление заголовка.
 */
internal class AddTitle(textToBeDecorated: Text) : TextDecorator(textToBeDecorated) {
    override fun apply() {
        super.apply()

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


/**
 * Декоратор, отвечающий за изменение шрифта.
 */
internal class ChangeFont(
    textToBeDecorated: Text,
    private val font: String
) : TextDecorator(textToBeDecorated) {
    override fun apply() {
        super.apply()

        val file = ShowSimpleText.file
        var lines = file.readLines()
        lines = lines.take(2) +
                "<font face=\"$font\">" +
                lines.takeLast(lines.size - 2)
        file.writeText(lines.joinToString(separator = "\n"))
    }
}


/**
 * Декоратор, отвечающий за добавление обложки альбома.
 */
internal class AddAlbumCover(
    textToBeDecorated: Text,
    private val coverPath: String
) : TextDecorator(textToBeDecorated) {
    override fun apply() {
        super.apply()

        val file = ShowSimpleText.file
        var lines = file.readLines()
        lines = lines.take(2) +
                "<img src=\"$coverPath\" height=360 width=360><br><br>" +
                lines.takeLast(lines.size - 2)
        file.writeText(lines.joinToString(separator = "\n"))
    }
}


/**
 * Декоратор, отвечающий за изменение цвета фона.
 */
internal class SetBackgroundColor(
    textToBeDecorated: Text,
    private val color: String
) : TextDecorator(textToBeDecorated) {
    override fun apply() {
        super.apply()

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
    ).apply()
}
