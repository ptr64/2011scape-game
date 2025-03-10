package gg.rsmod.plugins.content.items.potion

import gg.rsmod.plugins.api.cfg.Items

enum class Potion(
    val item: Int,
    val replacement: Int = -1,
    val potionType: PotionType,
) {
    STRENGTH_POTION4(item = Items.STRENGTH_POTION_4, replacement = Items.STRENGTH_POTION_3, potionType = PotionType.STRENGTH),
    STRENGTH_POTION3(item = Items.STRENGTH_POTION_3, replacement = Items.STRENGTH_POTION_2, potionType = PotionType.STRENGTH),
    STRENGTH_POTION2(item = Items.STRENGTH_POTION_2, replacement = Items.STRENGTH_POTION_1, potionType = PotionType.STRENGTH),
    STRENGTH_POTION1(item = Items.STRENGTH_POTION_1, potionType = PotionType.STRENGTH),
    SUPER_STRENGTH4(item = Items.SUPER_STRENGTH_4, replacement = Items.SUPER_STRENGTH_3, potionType = PotionType.SUPER_STRENGTH),
    SUPER_STRENGTH3(item = Items.SUPER_STRENGTH_3, replacement = Items.SUPER_STRENGTH_2, potionType = PotionType.SUPER_STRENGTH),
    SUPER_STRENGTH2(item = Items.SUPER_STRENGTH_2, replacement = Items.SUPER_STRENGTH_1, potionType = PotionType.SUPER_STRENGTH),
    SUPER_STRENGTH1(item = Items.SUPER_STRENGTH_1, replacement = Items.VIAL, potionType = PotionType.SUPER_STRENGTH),

    ATTACK4(item = Items.ATTACK_POTION_4, replacement = Items.ATTACK_POTION_3, potionType = PotionType.ATTACK),
    ATTACK3(item = Items.ATTACK_POTION_3, replacement = Items.ATTACK_POTION_2, potionType = PotionType.ATTACK),
    ATTACK2(item = Items.ATTACK_POTION_2, replacement = Items.ATTACK_POTION_1, potionType = PotionType.ATTACK),
    ATTACK1(item = Items.ATTACK_POTION_1, replacement = Items.VIAL, potionType = PotionType.ATTACK),
    SUPER_ATTACK4(item = Items.SUPER_ATTACK_4, replacement = Items.SUPER_ATTACK_3, potionType = PotionType.SUPER_ATTACK),
    SUPER_ATTACK3(item = Items.SUPER_ATTACK_3, replacement = Items.SUPER_ATTACK_2, potionType = PotionType.SUPER_ATTACK),
    SUPER_ATTACK2(item = Items.SUPER_ATTACK_2, replacement = Items.SUPER_ATTACK_1, potionType = PotionType.SUPER_ATTACK),
    SUPER_ATTACK1(item = Items.SUPER_ATTACK_1, replacement = Items.VIAL, potionType = PotionType.SUPER_ATTACK),

    DEFENCE_POTION4(item = Items.DEFENCE_POTION_4, replacement = Items.DEFENCE_POTION_3, potionType = PotionType.DEFENCE),
    DEFENCE_POTION3(item = Items.DEFENCE_POTION_3, replacement = Items.DEFENCE_POTION_2, potionType = PotionType.DEFENCE),
    DEFENCE_POTION2(item = Items.DEFENCE_POTION_2, replacement = Items.DEFENCE_POTION_1, potionType = PotionType.DEFENCE),
    DEFENCE_POTION1(item = Items.DEFENCE_POTION_1, replacement = Items.VIAL, potionType = PotionType.DEFENCE),
    SUPER_DEFENCE4(item = Items.SUPER_DEFENCE_4, replacement = Items.SUPER_DEFENCE_3, potionType = PotionType.SUPER_DEFENCE),
    SUPER_DEFENCE3(item = Items.SUPER_DEFENCE_3, replacement = Items.SUPER_DEFENCE_2, potionType = PotionType.SUPER_DEFENCE),
    SUPER_DEFENCE2(item = Items.SUPER_DEFENCE_2, replacement = Items.SUPER_DEFENCE_1, potionType = PotionType.SUPER_DEFENCE),
    SUPER_DEFENCE1(item = Items.SUPER_DEFENCE_1, replacement = Items.VIAL, potionType = PotionType.SUPER_DEFENCE),

    RANGING_POTION4(item = Items.RANGING_POTION_4, replacement = Items.RANGING_POTION_3, potionType = PotionType.RANGING),
    RANGING_POTION3(item = Items.RANGING_POTION_3, replacement = Items.RANGING_POTION_2, potionType = PotionType.RANGING),
    RANGING_POTION2(item = Items.RANGING_POTION_2, replacement = Items.RANGING_POTION_1, potionType = PotionType.RANGING),
    RANGING_POTION1(item = Items.RANGING_POTION_1, replacement = Items.VIAL, potionType = PotionType.RANGING),

    MAGIC_POTION4(item = Items.MAGIC_POTION_4, replacement = Items.MAGIC_POTION_3, potionType = PotionType.MAGIC),
    MAGIC_POTION3(item = Items.MAGIC_POTION_3, replacement = Items.MAGIC_POTION_2, potionType = PotionType.MAGIC),
    MAGIC_POTION2(item = Items.MAGIC_POTION_2, replacement = Items.MAGIC_POTION_1, potionType = PotionType.MAGIC),
    MAGIC_POTION1(item = Items.MAGIC_POTION_1, replacement = Items.VIAL, potionType = PotionType.MAGIC),

    FISHING4(item = Items.FISHING_POTION_4, replacement = Items.FISHING_POTION_3, potionType = PotionType.FISHING),
    FISHING3(item = Items.FISHING_POTION_3, replacement = Items.FISHING_POTION_2, potionType = PotionType.FISHING),
    FISHING2(item = Items.FISHING_POTION_2, replacement = Items.FISHING_POTION_1, potionType = PotionType.FISHING),
    FISHING1(item = Items.FISHING_POTION_1, replacement = Items.VIAL, potionType = PotionType.FISHING),
    AGILITY4(item = Items.AGILITY_POTION_4, replacement = Items.AGILITY_POTION_3, potionType = PotionType.AGILITY),
    AGILITY3(item = Items.AGILITY_POTION_3, replacement = Items.AGILITY_POTION_2, potionType = PotionType.AGILITY),
    AGILITY2(item = Items.AGILITY_POTION_2, replacement = Items.AGILITY_POTION_1, potionType = PotionType.AGILITY),
    AGILITY1(item = Items.AGILITY_POTION_1, replacement = Items.VIAL, potionType = PotionType.AGILITY),
    HUNTER4(item = Items.HUNTER_POTION_4, replacement = Items.HUNTER_POTION_3, potionType = PotionType.HUNTER),
    HUNTER3(item = Items.HUNTER_POTION_3, replacement = Items.HUNTER_POTION_2, potionType = PotionType.HUNTER),
    HUNTER2(item = Items.HUNTER_POTION_2, replacement = Items.HUNTER_POTION_1, potionType = PotionType.HUNTER),
    HUNTER1(item = Items.HUNTER_POTION_1, replacement = Items.VIAL, potionType = PotionType.HUNTER),
    CRAFTING4(item = Items.CRAFTING_POTION_4, replacement = Items.CRAFTING_POTION_3, potionType = PotionType.CRAFTING),
    CRAFTING3(item = Items.CRAFTING_POTION_3, replacement = Items.CRAFTING_POTION_2, potionType = PotionType.CRAFTING),
    CRAFTING2(item = Items.CRAFTING_POTION_2, replacement = Items.CRAFTING_POTION_1, potionType = PotionType.CRAFTING),
    CRAFTING1(item = Items.CRAFTING_POTION_1, replacement = Items.VIAL, potionType = PotionType.CRAFTING),
    FLETCHING_POTION4(item = Items.FLETCHING_POTION_4, replacement = Items.FLETCHING_POTION_3, potionType = PotionType.FLETCHING),
    FLETCHING_POTION3(item = Items.FLETCHING_POTION_3, replacement = Items.FLETCHING_POTION_2, potionType = PotionType.FLETCHING),
    FLETCHING_POTION2(item = Items.FLETCHING_POTION_2, replacement = Items.FLETCHING_POTION_1, potionType = PotionType.FLETCHING),
    FLETCHING_POTION1(item = Items.FLETCHING_POTION_1, potionType = PotionType.FLETCHING),
    COMBAT4(item = Items.COMBAT_POTION_4, replacement = Items.COMBAT_POTION_3, potionType = PotionType.COMBAT),
    COMBAT3(item = Items.COMBAT_POTION_3, replacement = Items.COMBAT_POTION_2, potionType = PotionType.COMBAT),
    COMBAT2(item = Items.COMBAT_POTION_2, replacement = Items.COMBAT_POTION_1, potionType = PotionType.COMBAT),
    COMBAT1(item = Items.COMBAT_POTION_1, replacement = Items.VIAL, potionType = PotionType.COMBAT),
    RESTORE4(item = Items.RESTORE_POTION_4, replacement = Items.RESTORE_POTION_3, potionType = PotionType.RESTORE),
    RESTORE3(item = Items.RESTORE_POTION_3, replacement = Items.RESTORE_POTION_2, potionType = PotionType.RESTORE),
    RESTORE2(item = Items.RESTORE_POTION_2, replacement = Items.RESTORE_POTION_1, potionType = PotionType.RESTORE),
    RESTORE1(item = Items.RESTORE_POTION_1, replacement = Items.VIAL, potionType = PotionType.RESTORE),
    SUPER_RESTORE4(item = Items.SUPER_RESTORE_4, replacement = Items.SUPER_RESTORE_3, potionType = PotionType.SUPER_RESTORE),
    SUPER_RESTORE3(item = Items.SUPER_RESTORE_3, replacement = Items.SUPER_RESTORE_2, potionType = PotionType.SUPER_RESTORE),
    SUPER_RESTORE2(item = Items.SUPER_RESTORE_2, replacement = Items.SUPER_RESTORE_1, potionType = PotionType.SUPER_RESTORE),
    SUPER_RESTORE1(item = Items.SUPER_RESTORE_1, replacement = Items.VIAL,potionType = PotionType.SUPER_RESTORE),
    PRAYER4(item = Items.PRAYER_POTION_4, replacement = Items.PRAYER_POTION_3, potionType = PotionType.PRAYER),
    PRAYER3(item = Items.PRAYER_POTION_3, replacement = Items.PRAYER_POTION_2, potionType = PotionType.PRAYER),
    PRAYER2(item = Items.PRAYER_POTION_2, replacement = Items.PRAYER_POTION_1, potionType = PotionType.PRAYER),
    PRAYER1(item = Items.PRAYER_POTION_1, replacement = Items.VIAL, potionType = PotionType.PRAYER),
    SUPER_PRAYER4(item = Items.SUPER_PRAYER_4, replacement = Items.SUPER_PRAYER_3, potionType = PotionType.SUPER_PRAYER),
    SUPER_PRAYER3(item = Items.SUPER_PRAYER_3, replacement = Items.SUPER_PRAYER_2, potionType = PotionType.SUPER_PRAYER),
    SUPER_PRAYER2(item = Items.SUPER_PRAYER_2, replacement = Items.SUPER_PRAYER_1, potionType = PotionType.SUPER_PRAYER),
    SUPER_PRAYER1(item = Items.SUPER_PRAYER_1, replacement = Items.VIAL, potionType = PotionType.SUPER_PRAYER),
    SARADOMIN_BREW4(item = Items.SARADOMIN_BREW_4, replacement = Items.SARADOMIN_BREW_3, potionType = PotionType.SARADOMIN_BREW),
    SARADOMIN_BREW3(item = Items.SARADOMIN_BREW_3, replacement = Items.SARADOMIN_BREW_2, potionType = PotionType.SARADOMIN_BREW),
    SARADOMIN_BREW2(item = Items.SARADOMIN_BREW_2, replacement = Items.SARADOMIN_BREW_1, potionType = PotionType.SARADOMIN_BREW),
    SARADOMIN_BREW1(item = Items.SARADOMIN_BREW_1, replacement = Items.VIAL, potionType = PotionType.SARADOMIN_BREW),

    // todo update formulas for the beers
    WIZARDS_MIND_BOMB(item = Items.WIZARDS_MIND_BOMB,replacement = Items.BEER_GLASS, potionType = PotionType.WIZARDS_MIND_BOMB),
    DWARVEN_STOUT(item = Items.DWARVEN_STOUT, replacement = Items.BEER_GLASS, potionType = PotionType.DWARVEN_STOUT),
    ASGARNIAN_ALE(item = Items.ASGARNIAN_ALE, replacement = Items.BEER_GLASS, potionType = PotionType.ASGARNIAN_ALE),

//    ZAMORAK_BREW4 (item = Items.ZAMORAK_BREW_4, replacement = Items.ZAMORAK_BREW_3, potionType = PotionType.ZAMORAK_BREW),
//    ZAMORAK_BREW3 (item = Items.ZAMORAK_BREW_3, replacement = Items.ZAMORAK_BREW_2, potionType = PotionType.ZAMORAK_BREW),
//    ZAMORAK_BREW2 (item = Items.ZAMORAK_BREW_2, replacement = Items.ZAMORAK_BREW_1, potionType = PotionType.ZAMORAK_BREW),
//    ZAMORAK_BREW1 (item = Items.ZAMORAK_BREW_1, potionType = PotionType.ZAMORAK_BREW),
//
//    SUMMONING_POTION4 (item = Items.SUMMONING_POTION_4, replacement = Items.SUMMONING_POTION_3, potionType = PotionType.SUMMONING_POTION),
//    SUMMONING_POTION3 (item = Items.SUMMONING_POTION_3, replacement = Items.SUMMONING_POTION_2, potionType = PotionType.SUMMONING_POTION),
//    SUMMONING_POTION2 (item = Items.SUMMONING_POTION_2, replacement = Items.SUMMONING_POTION_1, potionType = PotionType.SUMMONING_POTION),
//    SUMMONING_POTION1 (item = Items.SUMMONING_POTION_1, potionType = PotionType.SUMMONING_POTION),
//
//    ANTIPOISON4 (item = Items.ANTIPOISON_4, replacement = Items.ANTIPOISON_3, potionType = PotionType.ANTIPOISON),
//    ANTIPOISON3 (item = Items.ANTIPOISON_3, replacement = Items.ANTIPOISON_2, potionType = PotionType.ANTIPOISON),
//    ANTIPOISON2 (item = Items.ANTIPOISON_2, replacement = Items.ANTIPOISON_1, potionType = PotionType.ANTIPOISON),
//    ANTIPOISON1 (item = Items.ANTIPOISON_1, potionType = PotionType.ANTIPOISON),
//    SUPER_ANTIPOISON4 (item = Items.SUPER_ANTIPOISON_4, replacement = Items.SUPER_ANTIPOISON_3, potionType = PotionType.SUPER_ANTIPOISON),
//    SUPER_ANTIPOISON3 (item = Items.SUPER_ANTIPOISON_3, replacement = Items.SUPER_ANTIPOISON_2, potionType = PotionType.SUPER_ANTIPOISON),
//    SUPER_ANTIPOISON2 (item = Items.SUPER_ANTIPOISON_2, replacement = Items.SUPER_ANTIPOISON_1, potionType = PotionType.SUPER_ANTIPOISON),
//    SUPER_ANTIPOISON1 (item = Items.SUPER_ANTIPOISON_1, potionType = PotionType.SUPER_ANTIPOISON),
//
//    ENERGY4 (item = Items.ENERGY_4, replacement = Items.ENERGY_3, potionType = PotionType.ENERGY),
//    ENERGY3 (item = Items.ENERGY_3, replacement = Items.ENERGY_2, potionType = PotionType.ENERGY),
//    ENERGY2 (item = Items.ENERGY_2, replacement = Items.ENERGY_1, potionType = PotionType.ENERGY),
//    ENERGY1 (item = Items.ENERGY_1, potionType = PotionType.ENERGY),
//    SUPER_ENERGY4 (item = Items.SUPER_ENERGY_4, replacement = Items.SUPER_ENERGY_3, potionType = PotionType.SUPER_ENERGY),
//    SUPER_ENERGY3 (item = Items.SUPER_ENERGY_3, replacement = Items.SUPER_ENERGY_2, potionType = PotionType.SUPER_ENERGY),
//    SUPER_ENERGY2 (item = Items.SUPER_ENERGY_2, replacement = Items.SUPER_ENERGY_1, potionType = PotionType.SUPER_ENERGY),
//    SUPER_ENERGY1 (item = Items.SUPER_ENERGY_1, potionType = PotionType.SUPER_ENERGY),
//
//    SUPER_ANTIFIRE4 (item = Items.SUPER_ANTIFIRE_4, replacement = Items.SUPER_ANTIFIRE_3, potionType = PotionType.SUPER_ANTIFIRE),
//    SUPER_ANTIFIRE3 (item = Items.SUPER_ANTIFIRE_3, replacement = Items.SUPER_ANTIFIRE_2, potionType = PotionType.SUPER_ANTIFIRE),
//    SUPER_ANTIFIRE2 (item = Items.SUPER_ANTIFIRE_2, replacement = Items.SUPER_ANTIFIRE_1, potionType = PotionType.SUPER_ANTIFIRE),
//    SUPER_ANTIFIRE1 (item = Items.SUPER_ANTIFIRE_1, potionType = PotionType.SUPER_ANTIFIRE),

    ;
    companion object {
        val values = enumValues<Potion>()
    }
}