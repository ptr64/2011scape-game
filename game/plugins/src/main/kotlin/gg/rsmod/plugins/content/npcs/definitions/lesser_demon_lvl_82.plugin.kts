package gg.rsmod.plugins.content.npcs.definitions

import gg.rsmod.plugins.content.drops.DropTableFactory
import gg.rsmod.plugins.content.drops.global.Herbs

val ids = intArrayOf(Npcs.LESSER_DEMON)

val table = DropTableFactory
val lesserDemon = table.build {
    guaranteed {
        obj(Items.ACCURSED_ASHES)
    }

    main {
        total(1024)

        table(Herbs.minorHerbTable, slots = 8)

        obj(Items.STEEL_FULL_HELM, slots = 42)
        obj(Items.STEEL_HATCHET, slots = 42)
        obj(Items.STEEL_SCIMITAR, slots = 24)
        obj(Items.MITHRIL_SQ_SHIELD, slots = 8)
        obj(Items.MITHRIL_CHAINBODY, slots = 8)
        obj(Items.RUNE_MED_HELM, slots = 8)

        obj(Items.FIRE_RUNE, quantity = 60, slots = 64)
        obj(Items.CHAOS_RUNE, quantity = 12, slots = 46)
        obj(Items.DEATH_RUNE, quantity = 3, slots = 23)
        obj(Items.FIRE_RUNE, quantity = 30, slots = 8)

        obj(Items.COINS_995, quantity = 120, slots = 320)
        obj(Items.COINS_995, quantity = 40, slots = 231)
        obj(Items.COINS_995, quantity = 200, slots = 80)
        obj(Items.COINS_995, quantity = 10, slots = 56)
        obj(Items.COINS_995, quantity = 450, slots = 8)
        obj(Items.COINS_995, quantity = 10, slots = 8)


        obj(Items.JUG_OF_WINE, slots = 24)
        obj(Items.GOLD_ORE, slots = 16)

    }
}

table.register(lesserDemon, *ids)

on_npc_death(*ids) {
    table.getDrop(world, npc.damageMap.getMostDamage()!! as Player, npc.id, npc.tile)
}

ids.forEach {
    set_combat_def(it) {
        configs {
            attackSpeed = 4
            respawnDelay = 30
        }
        stats {
            hitpoints = 790
            attack = 68
            strength = 70
            defence = 71
            magic = 1
            ranged = 1
        }
        bonuses {
            attackStab = 0
            attackCrush = 0
            defenceStab = 0
            defenceSlash = 0
            defenceCrush = 0
            defenceMagic = -10
            defenceRanged = 0
        }
        anims {
            attack = 4630
            death = 67
            block = 65
        }
    }
}