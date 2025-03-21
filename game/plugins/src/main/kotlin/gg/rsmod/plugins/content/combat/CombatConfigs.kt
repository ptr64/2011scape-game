package gg.rsmod.plugins.content.combat

import gg.rsmod.game.model.combat.AttackStyle
import gg.rsmod.game.model.combat.CombatClass
import gg.rsmod.game.model.combat.CombatStyle
import gg.rsmod.game.model.combat.XpMode
import gg.rsmod.game.model.entity.Npc
import gg.rsmod.game.model.entity.Pawn
import gg.rsmod.game.model.entity.Player
import gg.rsmod.plugins.api.EquipmentType
import gg.rsmod.plugins.api.WeaponType
import gg.rsmod.plugins.api.cfg.Items
import gg.rsmod.plugins.api.ext.getAttackStyle
import gg.rsmod.plugins.api.ext.getEquipment
import gg.rsmod.plugins.api.ext.hasEquipped
import gg.rsmod.plugins.api.ext.hasWeaponType
import gg.rsmod.plugins.content.combat.strategy.CombatStrategy
import gg.rsmod.plugins.content.combat.strategy.MagicCombatStrategy
import gg.rsmod.plugins.content.combat.strategy.MeleeCombatStrategy
import gg.rsmod.plugins.content.combat.strategy.RangedCombatStrategy

/**
 * @author Tom <rspsmods@gmail.com>
 */
object CombatConfigs {

    private const val PLAYER_DEFAULT_ATTACK_SPEED = 4

    private const val MIN_ATTACK_SPEED = 1

    private val DEFENDERS = intArrayOf(
            Items.BRONZE_DEFENDER, Items.IRON_DEFENDER, Items.STEEL_DEFENDER,
            Items.MITHRIL_DEFENDER, Items.BLACK_DEFENDER, Items.ADAMANT_DEFENDER,
            Items.RUNE_DEFENDER, Items.DRAGON_DEFENDER
    )

    private val BOOKS = intArrayOf(
            Items.HOLY_BOOK, Items.BOOK_OF_BALANCE, Items.UNHOLY_BOOK,
            Items.BOOK_OF_LAW, Items.BOOK_OF_WAR
    )

    private val BOXING_GLOVES = intArrayOf(
            Items.BOXING_GLOVES, Items.BOXING_GLOVES_7673
    )

    private val GODSWORDS = intArrayOf(
            Items.ARMADYL_GODSWORD,
            Items.BANDOS_GODSWORD,
            Items.SARADOMIN_GODSWORD,
            Items.ZAMORAK_GODSWORD
    )

    fun getCombatStrategy(pawn: Pawn): CombatStrategy = when (getCombatClass(pawn)) {
        CombatClass.MELEE -> MeleeCombatStrategy
        CombatClass.MAGIC -> MagicCombatStrategy
        CombatClass.RANGED -> RangedCombatStrategy
        else -> throw IllegalStateException("Invalid combat class: ${getCombatClass(pawn)} for $pawn")
    }

    fun getCombatClass(pawn: Pawn): CombatClass {
        if (pawn is Npc) {
            return if(pawn.combatDef.spell > -1) CombatClass.MAGIC else pawn.combatClass
        }

        if (pawn is Player) {
            return when {
                pawn.attr.has(Combat.CASTING_SPELL) -> CombatClass.MAGIC
                pawn.hasWeaponType(WeaponType.BOW, WeaponType.CHINCHOMPA, WeaponType.CROSSBOW, WeaponType.THROWN) -> CombatClass.RANGED
                else -> CombatClass.MELEE
            }
        }

        throw IllegalArgumentException("Invalid pawn type.")
    }

    fun getAttackDelay(pawn: Pawn): Int {
        if (pawn is Npc) {
            return pawn.combatDef.attackSpeed
        }

        if (pawn is Player) {
            val default = PLAYER_DEFAULT_ATTACK_SPEED
            val weapon = pawn.getEquipment(EquipmentType.WEAPON) ?: return default
            var speed = weapon.getDef(pawn.world.definitions).attackSpeed
            if(getCombatClass(pawn) == CombatClass.RANGED && getAttackStyle(pawn) == AttackStyle.RAPID) {
                speed -= 1
            }
            if(getCombatClass(pawn) == CombatClass.MAGIC) {
                speed = 5
            }
            return Math.max(MIN_ATTACK_SPEED, speed)
        }

        throw IllegalArgumentException("Invalid pawn type.")
    }

    fun getAttackAnimation(pawn: Pawn): Int {
        if (pawn is Npc) {
            return pawn.combatDef.attackAnimation
        }

        if (pawn is Player) {
            val style = pawn.getAttackStyle()

            return when {
                pawn.hasEquipped(EquipmentType.WEAPON, *GODSWORDS) -> 7045
                pawn.hasWeaponType(WeaponType.AXE) -> if (style == 2) 401 else 395
                pawn.hasWeaponType(WeaponType.HAMMER) || pawn.hasWeaponType(WeaponType.HAMMER_EXTRA) -> 401
                pawn.hasWeaponType(WeaponType.SCYTHE) -> 8056
                pawn.hasWeaponType(WeaponType.BOW) -> 426
                pawn.hasWeaponType(WeaponType.CROSSBOW) -> 4230
                pawn.hasWeaponType(WeaponType.LONG_SWORD) -> if (style == 2) 390 else 386
                pawn.hasWeaponType(WeaponType.TWO_HANDED) -> if (style == 0) 406 else 407
                pawn.hasWeaponType(WeaponType.PICKAXE) -> if (style == 2) 400 else 401
                pawn.hasWeaponType(WeaponType.DAGGER) -> if (style == 2) 390 else 386
                pawn.hasWeaponType(WeaponType.STAFF) || pawn.hasWeaponType(WeaponType.SCEPTRE) -> 419
                pawn.hasWeaponType(WeaponType.MACE) -> if (style == 2) 400 else 401
                pawn.hasWeaponType(WeaponType.CHINCHOMPA) -> 7618
                pawn.hasWeaponType(WeaponType.THROWN)  || pawn.hasWeaponType(WeaponType.THROWN_EXTRA) -> if (pawn.hasEquipped(EquipmentType.WEAPON, Items.TOKTZXILUL)) 7558 else 929
                pawn.hasWeaponType(WeaponType.WHIP) -> if(style == 1) 11969 else 11968
                pawn.hasWeaponType(WeaponType.SPEAR) || pawn.hasWeaponType(WeaponType.HALBERD) -> if (style == 1) 440 else if (style == 2) 429 else 428
                pawn.hasWeaponType(WeaponType.CLAWS) -> 393
                else -> if (style == 1) 423 else 422
            }
        }

        throw IllegalArgumentException("Invalid pawn type.")
    }

    fun getBlockAnimation(pawn: Pawn): Int {
        if (pawn is Npc) {
            return pawn.combatDef.blockAnimation
        }

        if (pawn is Player) {
            return when {
                pawn.hasEquipped(EquipmentType.SHIELD, *BOOKS) -> 420
                pawn.hasEquipped(EquipmentType.WEAPON, Items.SLED_4084) -> 1466
                pawn.hasEquipped(EquipmentType.WEAPON, Items.BASKET_OF_EGGS) -> 1834
                pawn.hasEquipped(EquipmentType.SHIELD, *DEFENDERS) -> 4177
                pawn.getEquipment(EquipmentType.SHIELD) != null -> 1156 // If wearing any shield, this animation is used

                pawn.hasEquipped(EquipmentType.WEAPON, *BOXING_GLOVES) -> 3679
                pawn.hasEquipped(EquipmentType.WEAPON, *GODSWORDS) -> 7056
                pawn.hasEquipped(EquipmentType.WEAPON, Items.ZAMORAKIAN_SPEAR) -> 1709

                pawn.hasWeaponType(WeaponType.DAGGER) -> 378
                pawn.hasWeaponType(WeaponType.LONG_SWORD) -> 388
                pawn.hasWeaponType(WeaponType.PICKAXE, WeaponType.CLAWS) -> 397
                pawn.hasWeaponType(WeaponType.MACE) -> 403
                pawn.hasWeaponType(WeaponType.TWO_HANDED) -> 7050
                pawn.hasWeaponType(WeaponType.STAFF) || pawn.hasWeaponType(WeaponType.SCEPTRE) -> 420
                pawn.hasWeaponType(WeaponType.BOW) -> 424
                pawn.hasWeaponType(WeaponType.SPEAR, WeaponType.HALBERD) -> 430
                pawn.hasWeaponType(WeaponType.WHIP) -> 11974
                else -> 424
            }
        }

        throw IllegalArgumentException("Invalid pawn type.")
    }

    fun getAttackStyle(pawn: Pawn): AttackStyle {
        if (pawn.entityType.isNpc) {
            return (pawn as Npc).attackStyle
        }

        if (pawn is Player) {
            val style = pawn.getAttackStyle()

            return when {

                pawn.hasWeaponType(WeaponType.NONE) -> when (style) {
                    0 -> AttackStyle.ACCURATE
                    1 -> AttackStyle.AGGRESSIVE
                    3 -> AttackStyle.DEFENSIVE
                    else -> AttackStyle.NONE
                }

                pawn.hasWeaponType(WeaponType.BOW, WeaponType.CROSSBOW, WeaponType.THROWN, WeaponType.CHINCHOMPA) -> when (style) {
                    0 -> AttackStyle.ACCURATE
                    1 -> AttackStyle.RAPID
                    3 -> AttackStyle.LONG_RANGE
                    else -> AttackStyle.NONE
                }

                pawn.hasWeaponType(WeaponType.AXE, WeaponType.HAMMER, WeaponType.TWO_HANDED, WeaponType.PICKAXE,
                        WeaponType.DAGGER, WeaponType.LONG_SWORD, WeaponType.STAFF,
                        WeaponType.CLAWS) -> when (style) {
                    0 -> AttackStyle.ACCURATE
                    1 -> AttackStyle.AGGRESSIVE
                    2 -> AttackStyle.CONTROLLED
                    3 -> AttackStyle.DEFENSIVE
                    else -> AttackStyle.NONE
                }

                pawn.hasWeaponType(WeaponType.SPEAR) -> when (style) {
                    3 -> AttackStyle.DEFENSIVE
                    else -> AttackStyle.CONTROLLED
                }

                pawn.hasWeaponType(WeaponType.HALBERD) -> when (style) {
                    0 -> AttackStyle.CONTROLLED
                    1 -> AttackStyle.AGGRESSIVE
                    3 -> AttackStyle.DEFENSIVE
                    else -> AttackStyle.NONE
                }

                pawn.hasWeaponType(WeaponType.SCYTHE) -> when (style) {
                    0 -> AttackStyle.ACCURATE
                    1 -> AttackStyle.AGGRESSIVE
                    2 -> AttackStyle.AGGRESSIVE
                    3 -> AttackStyle.DEFENSIVE
                    else -> AttackStyle.NONE
                }

                pawn.hasWeaponType(WeaponType.WHIP) -> when (style) {
                    0 -> AttackStyle.ACCURATE
                    1 -> AttackStyle.CONTROLLED
                    3 -> AttackStyle.DEFENSIVE
                    else -> AttackStyle.NONE
                }

                else -> AttackStyle.NONE
            }
        }

        throw IllegalArgumentException("Invalid pawn type.")
    }

    fun getCombatStyle(pawn: Pawn): CombatStyle {
        if (pawn.entityType.isNpc) {
            return (pawn as Npc).combatStyle
        }

        if (pawn is Player) {
            val style = pawn.getAttackStyle()

            return when {

                pawn.attr.has(Combat.CASTING_SPELL) -> CombatStyle.MAGIC

                pawn.hasWeaponType(WeaponType.NONE) -> when (style) {
                    0 -> CombatStyle.CRUSH
                    1 -> CombatStyle.CRUSH
                    3 -> CombatStyle.CRUSH
                    else -> CombatStyle.NONE
                }

                pawn.hasWeaponType(WeaponType.BOW, WeaponType.CROSSBOW, WeaponType.THROWN, WeaponType.CHINCHOMPA, WeaponType.THROWN_EXTRA) -> CombatStyle.RANGED

                pawn.hasWeaponType(WeaponType.AXE) -> when (style) {
                    2 -> CombatStyle.CRUSH
                    else -> CombatStyle.SLASH
                }

                pawn.hasWeaponType(WeaponType.HAMMER) -> CombatStyle.CRUSH
                pawn.hasWeaponType(WeaponType.HAMMER_EXTRA) -> CombatStyle.CRUSH

                pawn.hasWeaponType(WeaponType.CLAWS) -> when (style) {
                    2 -> CombatStyle.STAB
                    else -> CombatStyle.SLASH
                }

                pawn.hasWeaponType(WeaponType.SALAMANDER) -> when (style) {
                    0 -> CombatStyle.SLASH
                    1 -> CombatStyle.RANGED
                    else -> CombatStyle.MAGIC
                }

                pawn.hasWeaponType(WeaponType.LONG_SWORD) -> when (style) {
                    2 -> CombatStyle.STAB
                    else -> CombatStyle.SLASH
                }

                pawn.hasWeaponType(WeaponType.TWO_HANDED) -> when (style) {
                    2 -> CombatStyle.CRUSH
                    else -> CombatStyle.SLASH
                }

                pawn.hasWeaponType(WeaponType.PICKAXE) -> when (style) {
                    2 -> CombatStyle.CRUSH
                    else -> CombatStyle.STAB
                }

                pawn.hasWeaponType(WeaponType.HALBERD) -> when (style) {
                    1 -> CombatStyle.SLASH
                    else -> CombatStyle.STAB
                }

                pawn.hasWeaponType(WeaponType.STAFF) -> CombatStyle.CRUSH
                pawn.hasWeaponType(WeaponType.SCEPTRE) -> CombatStyle.CRUSH

                pawn.hasWeaponType(WeaponType.SCYTHE) -> when (style) {
                    2 -> CombatStyle.CRUSH
                    else -> CombatStyle.SLASH
                }

                pawn.hasWeaponType(WeaponType.SPEAR) -> when (style) {
                    1 -> CombatStyle.SLASH
                    2 -> CombatStyle.CRUSH
                    else -> CombatStyle.STAB
                }

                pawn.hasWeaponType(WeaponType.MACE) -> when (style) {
                    2 -> CombatStyle.STAB
                    else -> CombatStyle.CRUSH
                }

                pawn.hasWeaponType(WeaponType.DAGGER) -> when (style) {
                    2 -> CombatStyle.SLASH
                    else -> CombatStyle.STAB
                }

                pawn.hasWeaponType(WeaponType.STAFF) -> CombatStyle.CRUSH

                pawn.hasWeaponType(WeaponType.WHIP) -> CombatStyle.SLASH

                else -> CombatStyle.NONE
            }
        }

        throw IllegalArgumentException("Invalid pawn type.")
    }

    fun getXpMode(player: Player): XpMode {
        val style = player.getAttackStyle()

        return when {

            player.hasWeaponType(WeaponType.NONE) -> {
                when (style) {
                    1 -> XpMode.STRENGTH
                    3 -> XpMode.DEFENCE
                    else -> XpMode.ATTACK
                }
            }

            player.hasWeaponType(WeaponType.STAFF, WeaponType.SCEPTRE) -> {
                when (style) {
                    1 -> XpMode.STRENGTH
                    2 -> XpMode.DEFENCE
                    else -> XpMode.ATTACK
                }
            }

            player.hasWeaponType(WeaponType.AXE, WeaponType.HAMMER, WeaponType.TWO_HANDED,
                    WeaponType.PICKAXE, WeaponType.DAGGER, WeaponType.HAMMER_EXTRA) -> {
                when (style) {
                    1 -> XpMode.STRENGTH
                    2 -> XpMode.STRENGTH
                    3 -> XpMode.DEFENCE
                    else -> XpMode.ATTACK
                }
            }

            player.hasWeaponType(WeaponType.LONG_SWORD, WeaponType.MACE, WeaponType.CLAWS) -> {
                when (style) {
                    1 -> XpMode.STRENGTH
                    2 -> XpMode.SHARED
                    3 -> XpMode.DEFENCE
                    else -> XpMode.ATTACK
                }
            }

            player.hasWeaponType(WeaponType.WHIP) -> {
                when (style) {
                    1 -> XpMode.SHARED
                    3 -> XpMode.DEFENCE
                    else -> XpMode.ATTACK
                }
            }

            player.hasWeaponType(WeaponType.SPEAR) -> {
                when (style) {
                    3 -> XpMode.DEFENCE
                    else -> XpMode.SHARED
                }
            }

            player.hasWeaponType(WeaponType.SCYTHE) -> {
                when (style) {
                    0 -> XpMode.ATTACK
                    1 -> XpMode.STRENGTH
                    2 -> XpMode.STRENGTH
                    else -> XpMode.DEFENCE
                }
            }

            player.hasWeaponType(WeaponType.HALBERD) -> {
                when (style) {
                    0 -> XpMode.SHARED
                    1 -> XpMode.STRENGTH
                    else -> XpMode.DEFENCE
                }
            }

            player.hasWeaponType(WeaponType.BOW, WeaponType.CROSSBOW, WeaponType.THROWN, WeaponType.CHINCHOMPA, WeaponType.THROWN_EXTRA) -> {
                when (style) {
                    3 -> XpMode.SHARED
                    else -> XpMode.RANGED
                }
            }

            player.hasWeaponType(WeaponType.SALAMANDER) -> {
                when (style) {
                    0 -> XpMode.STRENGTH
                    1 -> XpMode.RANGED
                    else -> XpMode.MAGIC
                }
            }

            else -> XpMode.ATTACK
        }
    }
}