package gg.rsmod.plugins.content.cmd

import de.mkammerer.argon2.Argon2Factory
import gg.rsmod.game.message.impl.LogoutFullMessage
import gg.rsmod.game.model.attr.NO_CLIP_ATTR
import gg.rsmod.game.model.bits.INFINITE_VARS_STORAGE
import gg.rsmod.game.model.bits.InfiniteVarsType
import gg.rsmod.game.model.interf.DisplayMode
import gg.rsmod.game.model.priv.Privilege
import gg.rsmod.game.model.timer.ACTIVE_COMBAT_TIMER
import gg.rsmod.game.service.serializer.PlayerSerializerService
import gg.rsmod.game.sync.block.UpdateBlockType
import gg.rsmod.plugins.content.inter.bank.openBank
import gg.rsmod.plugins.content.magic.TeleportType
import gg.rsmod.plugins.content.magic.teleport
import gg.rsmod.util.Misc
import java.text.DecimalFormat

on_command("empty", Privilege.ADMIN_POWER) {
    player.inventory.removeAll()
}

on_command("male") {
    player.appearance = Appearance.DEFAULT
    player.addBlock(UpdateBlockType.APPEARANCE)
}

on_command("female") {
    player.appearance = Appearance.DEFAULT_FEMALE
    player.addBlock(UpdateBlockType.APPEARANCE)
}

on_command("players") {
    val count = world.players.count()
    if (!player.timers.has(ACTIVE_COMBAT_TIMER)) {
        player.openInterface(dest = InterfaceDestination.MAIN_SCREEN_FULL, interfaceId = 275)
        player.setComponentHidden(interfaceId = 275, component = 14, hidden = true)
        player.setComponentText(interfaceId = 275, component = 2, "Players Online: $count")
        for (i in 16..315) {
            player.setComponentText(interfaceId = 275, component = i, "")
        }

        val builder = StringBuilder("<br>")
        world.players.forEach { p ->
            builder.append("${Misc.formatForDisplay(p.username)}<br><br>")
        }
        player.setComponentText(interfaceId = 275, component = 16, builder.toString())
    }
    player.message("There is currently $count ${if (count > 1) "players" else "player"} online.")
}

on_command("yell") {
   player.message("To talk in the global chat, start your message in public chat with a period (.)", ChatMessageType.CONSOLE)
}

on_command("teleto", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(
        player,
        args,
        "Invalid format! Example of proper command <col=42C66C>::teleto alycia</col>"
    ) { values ->
        val p = world.getPlayerForName(values[0]) ?: return@tryWithUsage
        player.teleport(p.tile, TeleportType.DAEMONHEIM)
    }
}

on_command("reboot", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::reboot 500</col>") { values ->
        val cycles = values[0].toInt()
        world.rebootTimer = cycles
        world.sendRebootTimer()
    }
}

on_command("kick", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::kick alycia</col>") { values ->
        val p = world.getPlayerForName(values[0]) ?: return@tryWithUsage
        p.requestLogout()
        p.write(LogoutFullMessage())
        p.channelClose()

    }
}

on_command("rate") {
    val args = player.getCommandArgs()
    tryWithUsage(
        player,
        args,
        "Invalid format! Example of proper command <col=42C66C>::rate 0</col> or <col=42C66C>::rate attack</col>"
    ) { values ->
        var skill: Int
        try {
            skill = values[0].toInt()
        } catch (e: NumberFormatException) {
            var name = values[0].lowercase()
            when (name) {
                "con" -> name = "construction"
                "cons" -> name = "constitution"
                "craft" -> name = "crafting"
                "hunt" -> name = "hunter"
                "slay" -> name = "slayer"
                "pray" -> name = "prayer"
                "mage" -> name = "magic"
                "fish" -> name = "fishing"
                "herb" -> name = "herblore"
                "rc" -> name = "runecrafting"
                "fm" -> name = "firemaking"
            }
            skill = Skills.getSkillForName(world, player.getSkills().maxSkills, name)
        }
        if (skill != -1) {
            val rate = player.interpolate(1.0, 5.0, player.getSkills().getCurrentLevel(skill))
            player.message("Your experience rate for ${Skills.getSkillName(world, skill).lowercase()} is ${String.format("%.2f", rate)}x", type = ChatMessageType.CONSOLE)
        } else {
            player.message("Could not find skill with identifier: ${values[0]}", type = ChatMessageType.CONSOLE)
        }
    }
}

on_command("home", Privilege.ADMIN_POWER) {
    val home = world.gameContext.home
    player.moveTo(home)
}

on_command("changepass") {
    val args = player.getCommandArgs()
    tryWithUsage(
        player,
        args,
        "Invalid format! Example of proper command <col=42C66C>::changepass newpassword</col>"
    ) { values ->
        val password = values[0]
        val client = player as Client
        client.passwordHash = Argon2Factory.create().hash(2, 65536, 1, password.toCharArray())
        player.world.getService(PlayerSerializerService::class.java, searchSubclasses = true)?.saveClientData(client)
        player.message(
            "<col=178000>You've successfully changed your password to $password",
            type = ChatMessageType.CONSOLE
        )
    }
}

on_command("noclip", Privilege.ADMIN_POWER) {
    val noClip = !(player.attr[NO_CLIP_ATTR] ?: false)
    player.attr[NO_CLIP_ATTR] = noClip
    player.message(
        "No-clip: ${if (noClip) "<col=178000>enabled</col>" else "<col=42C66C>disabled</col>"}",
        type = ChatMessageType.CONSOLE
    )
}

on_command("mypos") {
    val instancedMap = world.instanceAllocator.getMap(player.tile)
    val tile = player.tile
    if (instancedMap == null) {
        player.message(
            "Tile=[<col=42C66C>${tile.x}, ${tile.z}, ${tile.height}</col>], Region=${player.tile.regionId}",
            type = ChatMessageType.CONSOLE
        )
    } else {
        val delta = tile - instancedMap.area.bottomLeft
        player.message(
            "Tile=[<col=42C66C>${tile.x}, ${tile.z}, ${tile.height}</col>], Relative=[${delta.x}, ${delta.z}]",
            type = ChatMessageType.CONSOLE
        )
    }
}

on_command("tele", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    var x: Int
    var z: Int
    var height: Int
    tryWithUsage(
        player,
        args,
        "Invalid format! Example of proper command <col=42C66C>::tele 3200 3200</col>"
    ) { values ->
        if (values.size == 1 && values[0].contains(",")) {
            val split = values[0].split(",".toRegex())
            x = split[1].toInt() shl 6 or split[3].toInt()
            z = split[2].toInt() shl 6 or split[4].toInt()
            height = split[0].toInt()
        } else {
            x = values[0].toInt()
            z = values[1].toInt()
            height = if (values.size > 2) values[2].toInt() else 0
        }
        player.moveTo(x, z, height)
    }
}

on_command("teler", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::teler 12850</col>") { values ->
        val region = values[0].toInt()
        val tile = Tile.fromRegion(region)
        player.moveTo(tile)
    }
}

on_command("anim", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::anim 1</col>") { values ->
        val id = values[0].toInt()
        player.animate(id)
        player.message("Animate: $id", type = ChatMessageType.CONSOLE)
    }
}

on_command("gfx", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::gfx 1</col>") { values ->
        val id = values[0].toInt()
        val height = if (values.size >= 2) values[1].toInt() else 100
        player.graphic(id, height)
        player.message("Graphic: $id", type = ChatMessageType.CONSOLE)
    }
}

on_command("sound", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::sound 1</col>") { values ->
        val id = values[0].toInt()
        player.playSound(id)
        player.message("Sound: $id", type = ChatMessageType.CONSOLE)
    }
}

on_command("song", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::song 1</col>") { values ->
        val id = values[0].toInt()
        player.playSong(id)
        player.message("Song: $id", type = ChatMessageType.CONSOLE)
    }
}

on_command("infrun", Privilege.ADMIN_POWER) {
    player.toggleStorageBit(INFINITE_VARS_STORAGE, InfiniteVarsType.RUN)
    player.message(
        "Infinite run: ${
            if (!player.hasStorageBit(
                    INFINITE_VARS_STORAGE,
                    InfiniteVarsType.RUN
                )
            ) "<col=42C66C>disabled</col>" else "<col=178000>enabled</col>"
        }", type = ChatMessageType.CONSOLE
    )
}

on_command("infpray", Privilege.ADMIN_POWER) {
    player.toggleStorageBit(INFINITE_VARS_STORAGE, InfiniteVarsType.PRAY)
    player.message(
        "Infinite prayer: ${
            if (!player.hasStorageBit(
                    INFINITE_VARS_STORAGE,
                    InfiniteVarsType.PRAY
                )
            ) "<col=42C66C>disabled</col>" else "<col=178000>enabled</col>"
        }", type = ChatMessageType.CONSOLE
    )
}

on_command("infhp", Privilege.ADMIN_POWER) {
    player.toggleStorageBit(INFINITE_VARS_STORAGE, InfiniteVarsType.HP)
    player.message(
        "Infinite hp: ${
            if (!player.hasStorageBit(
                    INFINITE_VARS_STORAGE,
                    InfiniteVarsType.HP
                )
            ) "<col=42C66C>disabled</col>" else "<col=178000>enabled</col>"
        }", type = ChatMessageType.CONSOLE
    )
}

on_command("invisible", Privilege.ADMIN_POWER) {
    player.invisible = !player.invisible
    player.message(
        "Invisible: ${if (!player.invisible) "<col=42C66C>false</col>" else "<col=178000>true</col>"}",
        type = ChatMessageType.CONSOLE
    )
}

on_command("npc", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::npc 1</col>") { values ->
        val id = values[0].toInt()
        val npc = Npc(id, player.tile, world)
        npc.walkRadius = 10
        world.spawn(npc)
    }
}

on_command("obj", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::obj 1</col>") { values ->
        val id = values[0].toInt()
        val type = if (values.size > 1) values[1].toInt() else 10
        val rot = if (values.size > 2) values[2].toInt() else 0
        val obj = DynamicObject(id, type, rot, player.tile)
        world.spawn(obj)
    }
}

on_command("removeobj", Privilege.ADMIN_POWER) {
    val chunk = world.chunks.getOrCreate(player.tile)
    val obj =
        chunk.getEntities<GameObject>(player.tile, EntityType.STATIC_OBJECT, EntityType.DYNAMIC_OBJECT).firstOrNull()
    if (obj != null) {
        world.remove(obj)
    } else {
        player.message("No object found in tile.", type = ChatMessageType.CONSOLE)
    }
}

on_command("master", Privilege.ADMIN_POWER) {
    for (i in 0 until player.getSkills().maxSkills) {
        player.getSkills().setBaseLevel(i, 99)
    }
    player.calculateAndSetCombatLevel()
}
on_command("drainskills", Privilege.DEV_POWER) {
    for (i in 0 until player.getSkills().maxSkills) {
        player.getSkills().setCurrentLevel(i, 1)
    }
}

on_command("reset", Privilege.ADMIN_POWER) {
    for (i in 0 until player.getSkills().maxSkills) {
        player.getSkills().setBaseLevel(i, if (i == Skills.HITPOINTS) 10 else 1)
    }
    player.calculateAndSetCombatLevel()
}

on_command("setlvl", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(
        player,
        args,
        "Invalid format! Example of proper command <col=42C66C>::setlvl 0 99</col> or <col=42C66C>::setlvl attack 99</col>"
    ) { values ->
        var skill: Int
        try {
            skill = values[0].toInt()
        } catch (e: NumberFormatException) {
            var name = values[0].lowercase()
            when (name) {
                "con" -> name = "construction"
                "hp" -> name = "hitpoints"
                "craft" -> name = "crafting"
                "hunt" -> name = "hunter"
                "slay" -> name = "slayer"
                "pray" -> name = "prayer"
                "mage" -> name = "magic"
                "fish" -> name = "fishing"
                "herb" -> name = "herblore"
                "rc" -> name = "runecrafting"
                "fm" -> name = "firemaking"
            }
            skill = Skills.getSkillForName(world, player.getSkills().maxSkills, name)
        }
        if (skill != -1) {
            val level = values[1].toInt()
            player.getSkills().setBaseLevel(skill, level)
        } else {
            player.message("Could not find skill with identifier: ${values[0]}", type = ChatMessageType.CONSOLE)
        }
    }
}

on_command("item", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(
        player,
        args,
        "Invalid format! Example of proper command <col=42C66C>::item 4151 1</col> or <col=42C66C>::item 4151</col>"
    ) { values ->
        val item = values[0].toInt()
        val amount = if (values.size > 1) Math.min(Int.MAX_VALUE.toLong(), values[1].parseAmount()).toInt() else 1
        if (item < world.definitions.getCount(ItemDef::class.java)) {
            val def = world.definitions.get(ItemDef::class.java, Item(item).toUnnoted(world.definitions).id)
            val result = player.inventory.add(item = item, amount = amount, assureFullInsertion = false)
            player.message(
                "You have spawned <col=42C66C>${DecimalFormat().format(result.completed)} x ${def.name}</col></col> ($item).",
                type = ChatMessageType.CONSOLE
            )
        } else {
            player.message("Item $item does not exist in cache.", type = ChatMessageType.CONSOLE)
        }
    }
}

on_command("food", Privilege.ADMIN_POWER) {
    player.inventory.add(item = Items.MANTA_RAY, amount = player.inventory.freeSlotCount)
}

on_command("varp", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::varp 173 1</col>") { values ->
        val varp = values[0].toInt()
        val state = values[1].toInt()
        val oldState = player.getVarp(varp)
        player.setVarp(varp, state)
        player.message(
            "Set varp (<col=42C66C>$varp</col>) from <col=42C66C>$oldState</col> to <col=42C66C>${
                player.getVarp(
                    varp
                )
            }</col>", type = ChatMessageType.CONSOLE
        )
    }
}

on_command("varc", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::varc 173 1</col>") { values ->
        val varc = values[0].toInt()
        val state = values[1].toInt()
        player.setVarc(varc, state)
        player.message(
            "Set varc (<col=42C66C>$varc</col>) to <col=42C66C>${state}</col>",
            type = ChatMessageType.CONSOLE
        )
    }
}

on_command("varbit", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(
        player,
        args,
        "Invalid format! Example of proper command <col=42C66C>::varbit 5451 1</col>"
    ) { values ->
        val varbit = values[0].toInt()
        val state = values[1].toInt()
        val oldState = player.getVarbit(varbit)
        player.setVarbit(varbit, state)
        player.message(
            "Set varbit (<col=42C66C>$varbit</col>) from <col=42C66C>$oldState</col> to <col=42C66C>${
                player.getVarbit(
                    varbit
                )
            }</col>", type = ChatMessageType.CONSOLE
        )
    }
}

on_command("getvarbit", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(
        player,
        args,
        "Invalid format! Example of proper command <col=42C66C>::getvarbit 5451</col>"
    ) { values ->
        val varbit = values[0].toInt()
        val state = player.getVarbit(varbit)
        player.message(
            "Get varbit (<col=42C66C>$varbit</col>): <col=42C66C>$state</col>",
            type = ChatMessageType.CONSOLE
        )
    }
}

on_command("getvarbits", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(
        player,
        args,
        "Invalid format! Example of proper command <col=42C66C>::getvarbits 83</col>"
    ) { values ->
        val varp = values[0].toInt()
        val varbits = mutableListOf<VarbitDef>()
        val totalVarbits = world.definitions.getCount(VarbitDef::class.java)
        for (i in 0 until totalVarbits) {
            val varbit = world.definitions.getNullable(VarbitDef::class.java, i)
            if (varbit?.varp == varp) {
                varbits.add(varbit)
            }
        }
        player.message("Varbits for varp <col=42C66C>$varp</col>:")
        varbits.forEach { varbit ->
            player.message(
                "  ${varbit.id} [bits ${varbit.startBit}-${varbit.endBit}] [current ${player.getVarbit(varbit.id)}]",
                type = ChatMessageType.CONSOLE
            )
        }
    }
}

on_command("getvarp", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(
        player,
        args,
        "Invalid format! Example of proper command <col=42C66C>::getvarp 83</col>"
    ) { values ->
        val varp = values[0].toInt()
        val state = player.getVarp(varp)
        player.message(
            "Get varp (<col=42C66C>$varp</col>): <col=42C66C>$state</col>",
            type = ChatMessageType.CONSOLE
        )
    }
}

on_command("interface", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(
        player,
        args,
        "Invalid format! Example of proper command <col=42C66C>::interface 214</col>"
    ) { values ->
        val component = values[0].toInt()
        player.openInterface(component, InterfaceDestination.MAIN_SCREEN)
        player.message("Opening interface <col=42C66C>$component</col>", type = ChatMessageType.CONSOLE)
    }
}

on_command("clip", Privilege.ADMIN_POWER) {
    val chunk = world.chunks.getOrCreate(player.tile)
    val matrix = chunk.getMatrix(player.tile.height)
    val lx = player.tile.x % 8
    val lz = player.tile.z % 8
    player.message("Tile flags: ${chunk.getMatrix(player.tile.height).get(lx, lz)}")
    Direction.RS_ORDER.forEach { dir ->
        val walkBlocked = matrix.isBlocked(lx, lz, dir, projectile = false)
        val projectileBlocked = matrix.isBlocked(lx, lz, dir, projectile = true)
        val walkable = if (walkBlocked) "<col=42C66C>blocked</col>" else "<col=178000>walkable</col>"
        val projectile = if (projectileBlocked) "<col=42C66C>projectiles blocked" else "<col=178000>projectiles allowed"
        player.message("$dir: $walkable - $projectile", type = ChatMessageType.CONSOLE)
    }
}

on_command("equ", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::equ 3 19709</col>") { values ->
        val id = values[1].toInt()
        val slot = values[0].toInt()
        if (id < world.definitions.getCount(ItemDef::class.java)) {
            val def = world.definitions.get(ItemDef::class.java, Item(id).toUnnoted(world.definitions).id)
            player.equipment[slot] = Item(id)
            player.message(
                "You have equipped <col=42C66C>${def.name}</col> ($id) to equipment slot <col=42C66C>($slot)</col>.",
                type = ChatMessageType.CONSOLE
            )
        } else {
            player.message("Item $id does not exist in cache.", type = ChatMessageType.CONSOLE)
        }
    }
}

on_command("hit", Privilege.ADMIN_POWER) {
    val args = player.getCommandArgs()
    tryWithUsage(player, args, "Invalid format! Example of proper command <col=42C66C>::hit 10</col>") { values ->
        val damage = values[0].toInt()
        player.hit(damage = damage, type = HitType.MAGIC)
    }
}

on_command("bank", Privilege.ADMIN_POWER) {
    player.openBank()
}

on_command("shop", Privilege.ADMIN_POWER) {
    player.openShop("Edgeville General Store")
}

fun tryWithUsage(player: Player, args: Array<String>, failMessage: String, tryUnit: Function1<Array<String>, Unit>) {
    try {
        tryUnit.invoke(args)
    } catch (e: Exception) {
        player.message(failMessage, type = ChatMessageType.CONSOLE)
        e.printStackTrace()
    }
}

fun getArgumentLine(args: Array<String>, offset: Int, length: Int): String? {
    val sb = StringBuilder()
    for (i in offset until length) {
        if (i != offset) {
            sb.append(" ")
        }
        sb.append(args[i])
    }
    return sb.toString()
}