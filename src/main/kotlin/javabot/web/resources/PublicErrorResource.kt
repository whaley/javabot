package javabot.web.resources

import io.dropwizard.views.View
import javabot.web.views.ErrorView

import java.util.Random

class PublicErrorResource {
    fun view403(): View {
        return ErrorView("/error/403.ftl", getRandomImage(IMAGE_403))
    }

    fun view500(): View {
        return ErrorView("/error/500.ftl", getRandomImage(IMAGE_500))
    }

    private fun getRandomImage(images: Array<String>): String {
        return images[Random().nextInt(images.size)]
    }

    companion object {
        private val IMAGE_403 = arrayOf("403.gif")
        private val IMAGE_500 = arrayOf("500.gif")
    }
}
