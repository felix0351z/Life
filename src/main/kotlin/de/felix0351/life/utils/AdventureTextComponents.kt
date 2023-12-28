package de.felix0351.life.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor


fun text(
    text: String,
    color: NamedTextColor = NamedTextColor.GRAY
): TextComponent {
    return Component.text(text)
        .color(color)
}

operator fun Component.plus(toAppend: Component): Component {
    return this.append(toAppend)
}


class TextFormatException(val text: String) : RuntimeException(text)


fun<T: Any> textArg(
    text: String,
    vararg arguments: T,
    defaultColor: NamedTextColor = NamedTextColor.GRAY,
    argumentColor: NamedTextColor = NamedTextColor.GOLD
): TextComponent {
    val argumentsInText = text.count { it == ';' }
    if (arguments.size != argumentsInText) throw TextFormatException("The number of your arguments is invalid!")

    val textComponents = text.split(";").map {
        Component.text(it).color(defaultColor)
    }.toMutableList()

    val argumentComponents = arguments.map {
        val arg = it.toString()
        Component.text(arg).color(argumentColor)
    }

    val final = Component.text()
    for (i in 0 until maxOf(textComponents.size, argumentComponents.size)) {
        if (i < textComponents.size) final.append(textComponents[i])
        if (i < argumentComponents.size) final.append(argumentComponents[i])
    }

    return final.build()
}


