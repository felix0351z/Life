package de.felix0351.life.models

import org.bukkit.Location


fun Location.toStringCoordinate() =
    this.world.name + "-" + this.x + "-" + this.y + "-" + this.z
