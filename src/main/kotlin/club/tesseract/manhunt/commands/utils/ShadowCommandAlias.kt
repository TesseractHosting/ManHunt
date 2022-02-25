package club.tesseract.manhunt.commands.utils

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ShadowCommandAlias(val alias: Array<String>)
