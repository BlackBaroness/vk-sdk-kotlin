import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolute

fun main() {
    val inputDir = Path("./src/main/resources/schema".replace('/', File.separatorChar)).absolute()
    val outputDir = Path("../vk-sdk-kotlin-library/src/generated/kotlin".replace('/', File.separatorChar)).absolute()

    // todo
}