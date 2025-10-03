package net.fameless.forcebattle.util;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public record ItemData<P, C>(NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType, C data) {

}
