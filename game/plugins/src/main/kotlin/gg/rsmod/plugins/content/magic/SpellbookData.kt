package gg.rsmod.plugins.content.magic

import gg.rsmod.game.model.item.Item
import gg.rsmod.plugins.api.cfg.Items

enum class SpellbookData(
    val interfaceId: Int,
    val component: Int,
    val uniqueId: Int,
    val spellType: SpellType,
    val spellName: String,
    val level: Int,
    val runes: List<Item> = emptyList()
)  {
    HOME_TELEPORT(
        interfaceId = 192,
        component = 24,
        uniqueId = 356,
        spellType = SpellType.TELEPORT_SPELL_TYPE,
        spellName = "Lumbridge Home Teleport",
        level = 1,
    ),
    WIND_RUSH(
        interfaceId = 192,
        component = 98,
        uniqueId = 3759,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Wind Rush",
        level = 1,
        runes = listOf(Item(Items.AIR_RUNE, 2))
    ),
    WIND_STRIKE(
        interfaceId = 192,
        component = 25,
        uniqueId = 15,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Wind Strike",
        level = 1,
        runes = listOf(Item(Items.AIR_RUNE, 1), Item(Items.MIND_RUNE, 1))
    ),
    WATER_STRIKE(
        interfaceId = 192,
        component = 28,
        uniqueId = 17,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Water Strike",
        level = 5,
        runes = listOf(Item(Items.AIR_RUNE, 1), Item(Items.MIND_RUNE, 1), Item(Items.WATER_RUNE, 1))
    ),
    EARTH_STRIKE(
        interfaceId = 192,
        component = 30,
        uniqueId = 19,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Earth Strike",
        level = 9,
        runes = listOf(Item(Items.AIR_RUNE, 1), Item(Items.MIND_RUNE, 1), Item(Items.EARTH_RUNE, 2))
    ),
    MOBILISING_ARMIES(
        interfaceId = 192,
        component = 37,
        uniqueId = 1564,
        spellType = SpellType.TELEPORT_SPELL_TYPE,
        spellName = "Mobilising Armies Teleport",
        level = 10,
        runes = listOf(Item(Items.WATER_RUNE), Item(Items.AIR_RUNE, 3), Item(Items.LAW_RUNE))
    ),
    FIRE_STRIKE(
        interfaceId = 192,
        component = 32,
        uniqueId = 71,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Fire Strike",
        level = 13,
        runes = listOf(Item(Items.AIR_RUNE, 1), Item(Items.MIND_RUNE, 1), Item(Items.FIRE_RUNE, 3))
    ),
    WIND_BOLT(
        interfaceId = 192,
        component = 34,
        uniqueId = 73,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Wind Bolt",
        level = 17,
        runes = listOf(Item(Items.AIR_RUNE, 2), Item(Items.CHAOS_RUNE, 1))
    ),
    WATER_BOLT(
        interfaceId = 192,
        component = 39,
        uniqueId = 76,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Water Bolt",
        level = 23,
        runes = listOf(Item(Items.AIR_RUNE, 2), Item(Items.WATER_RUNE, 2), Item(Items.CHAOS_RUNE, 1))
    ),
    VARROCK_TELEPORT(
        interfaceId = 192,
        component = 40,
        uniqueId = 77,
        spellType = SpellType.TELEPORT_SPELL_TYPE,
        spellName = "Varrock Teleport",
        level = 25,
        runes = listOf(Item(Items.FIRE_RUNE), Item(Items.AIR_RUNE, 3), Item(Items.LAW_RUNE))
    ),
    EARTH_BOLT(
        interfaceId = 192,
        component = 42,
        uniqueId = 79,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Earth Bolt",
        level = 29,
        runes = listOf(Item(Items.AIR_RUNE, 2), Item(Items.EARTH_RUNE, 3), Item(Items.CHAOS_RUNE, 1))
    ),
    LUMBRIDGE_TELEPORT(
        interfaceId = 192,
        component = 43,
        uniqueId = 80,
        spellType = SpellType.TELEPORT_SPELL_TYPE,
        spellName = "Lumbridge Teleport",
        level = 31,
        runes = listOf(Item(Items.EARTH_RUNE), Item(Items.AIR_RUNE, 3), Item(Items.LAW_RUNE))
    ),
    FIRE_BOLT(
        interfaceId = 192,
        component = 45,
        uniqueId = 82,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Fire Bolt",
        level = 35,
        runes = listOf(Item(Items.AIR_RUNE, 3), Item(Items.FIRE_RUNE, 4), Item(Items.CHAOS_RUNE, 1))
    ),
    FALADOR_TELEPORT(
        interfaceId = 192,
        component = 46,
        uniqueId = 83,
        spellType = SpellType.TELEPORT_SPELL_TYPE,
        spellName = "Falador Teleport",
        level = 37,
        runes = listOf(Item(Items.WATER_RUNE), Item(Items.AIR_RUNE, 3), Item(Items.LAW_RUNE))
    ),
    WIND_BLAST(
        interfaceId = 192,
        component = 49,
        uniqueId = 85,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Wind Blast",
        level = 41,
        runes = listOf(Item(Items.AIR_RUNE, 3), Item(Items.DEATH_RUNE, 1))
    ),
    CAMELOT_TELEPORT(
        interfaceId = 192,
        component = 51,
        uniqueId = 87,
        spellType = SpellType.TELEPORT_SPELL_TYPE,
        spellName = "Camelot Teleport",
        level = 45,
        runes = listOf(Item(Items.AIR_RUNE, 5), Item(Items.LAW_RUNE))
    ),
    WATER_BLAST(
        interfaceId = 192,
        component = 52,
        uniqueId = 88,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Water Blast",
        level = 47,
        runes = listOf(Item(Items.AIR_RUNE, 3), Item(Items.WATER_RUNE, 3), Item(Items.DEATH_RUNE, 1))
    ),
    ARDOUGNE_TELEPORT(
        interfaceId = 192,
        component = 57,
        uniqueId = 104,
        spellType = SpellType.TELEPORT_SPELL_TYPE,
        spellName = "Ardougne Teleport",
        level = 51,
        runes = listOf(Item(Items.WATER_RUNE, 2), Item(Items.LAW_RUNE, 2))
    ),
    EARTH_BLAST(
        interfaceId = 192,
        component = 58,
        uniqueId = 90,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Earth Blast",
        level = 53,
        runes = listOf(Item(Items.AIR_RUNE, 3), Item(Items.EARTH_RUNE, 4), Item(Items.DEATH_RUNE, 1))
    ),
    WATCHTOWER_TELEPORT(
        interfaceId = 192,
        component = 62,
        uniqueId = 105,
        spellType = SpellType.TELEPORT_SPELL_TYPE,
        spellName = "Watchtower Teleport",
        level = 58,
        runes = listOf(Item(Items.EARTH_RUNE, 2), Item(Items.LAW_RUNE, 2))
    ),
    FIRE_BLAST(
        interfaceId = 192,
        component = 63,
        uniqueId = 95,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Fire Blast",
        level = 59,
        runes = listOf(Item(Items.AIR_RUNE, 4), Item(Items.FIRE_RUNE, 5), Item(Items.DEATH_RUNE, 1))
    ),
    TROLLHEIM_TELEPORT(
        interfaceId = 192,
        component = 69,
        uniqueId = 373,
        spellType = SpellType.TELEPORT_SPELL_TYPE,
        spellName = "Trollheim Teleport",
        level = 61,
        runes = listOf(Item(Items.FIRE_RUNE, 2), Item(Items.LAW_RUNE, 2))
    ),
    WIND_WAVE(
        interfaceId = 192,
        component = 70,
        uniqueId = 96,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Wind Wave",
        level = 62,
        runes = listOf(Item(Items.AIR_RUNE, 5), Item(Items.BLOOD_RUNE, 1))
    ),
    APE_ATOLL_TELEPORT(
        interfaceId = 192,
        component = 72,
        uniqueId = 407,
        spellType = SpellType.TELEPORT_SPELL_TYPE,
        spellName = "Teleport to Ape Atoll",
        level = 64,
        runes = listOf(Item(Items.FIRE_RUNE, 2), Item(Items.WATER_RUNE, 2), Item(Items.LAW_RUNE, 2), Item(Items.BANANA))
    ),
    WATER_WAVE(
        interfaceId = 192,
        component = 73,
        uniqueId = 98,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Water Wave",
        level = 65,
        runes = listOf(Item(Items.AIR_RUNE, 5), Item(Items.WATER_RUNE, 7), Item(Items.BLOOD_RUNE, 1))
    ),
    EARTH_WAVE(
        interfaceId = 192,
        component = 77,
        uniqueId = 101,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Earth Wave",
        level = 70,
        runes = listOf(Item(Items.AIR_RUNE, 5), Item(Items.EARTH_RUNE, 7), Item(Items.BLOOD_RUNE, 1))
    ),
    FIRE_WAVE(
        interfaceId = 192,
        component = 80,
        uniqueId = 102,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Fire Wave",
        level = 75,
        runes = listOf(Item(Items.AIR_RUNE, 5), Item(Items.EARTH_RUNE, 7), Item(Items.BLOOD_RUNE, 1))
    ),
    WIND_SURGE(
        interfaceId = 192,
        component = 84,
        uniqueId = 815,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Wind Surge",
        level = 81,
        runes = listOf(Item(Items.AIR_RUNE, 7), Item(Items.DEATH_RUNE, 1), Item(Items.BLOOD_RUNE, 1))
    ),
    WATER_SURGE(
        interfaceId = 192,
        component = 87,
        uniqueId = 816,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Water Surge",
        level = 85,
        runes = listOf(Item(Items.AIR_RUNE, 7), Item(Items.WATER_RUNE, 10), Item(Items.DEATH_RUNE, 1), Item(Items.BLOOD_RUNE, 1))
    ),
    EARTH_SURGE(
        interfaceId = 192,
        component = 89,
        uniqueId = 817,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Earth Surge",
        level = 90,
        runes = listOf(Item(Items.AIR_RUNE, 7), Item(Items.EARTH_RUNE, 10), Item(Items.DEATH_RUNE, 1), Item(Items.BLOOD_RUNE, 1))
    ),
    FIRE_SURGE(
        interfaceId = 192,
        component = 91,
        uniqueId = 818,
        spellType = SpellType.COMBAT_SPELL_TYPE,
        spellName = "Fire Surge",
        level = 95,
        runes = listOf(Item(Items.AIR_RUNE, 7), Item(Items.FIRE_RUNE, 10), Item(Items.DEATH_RUNE, 1), Item(Items.BLOOD_RUNE, 1))
    ),
}