package gg.rsmod.plugins.api.ext

import com.google.common.primitives.Ints
import gg.rsmod.game.fs.def.ItemDef
import gg.rsmod.game.fs.def.VarbitDef
import gg.rsmod.game.message.impl.*
import gg.rsmod.game.model.Direction
import gg.rsmod.game.model.Tile
import gg.rsmod.game.model.World
import gg.rsmod.game.model.attr.*
import gg.rsmod.game.model.bits.BitStorage
import gg.rsmod.game.model.bits.StorageBits
import gg.rsmod.game.model.container.ContainerStackType
import gg.rsmod.game.model.container.ItemContainer
import gg.rsmod.game.model.entity.Player
import gg.rsmod.game.model.interf.DisplayMode
import gg.rsmod.game.model.item.Item
import gg.rsmod.game.model.quest.Quest
import gg.rsmod.game.model.shop.PurchasePolicy
import gg.rsmod.game.model.timer.SKULL_ICON_DURATION_TIMER
import gg.rsmod.game.sync.block.UpdateBlockType
import gg.rsmod.plugins.api.*
import gg.rsmod.plugins.api.cfg.Items
import gg.rsmod.plugins.content.combat.createProjectile
import gg.rsmod.plugins.content.combat.strategy.MagicCombatStrategy
import gg.rsmod.plugins.content.quests.QUEST_POINT_VARP
import gg.rsmod.plugins.content.skills.smithing.data.BarProducts
import gg.rsmod.plugins.content.skills.smithing.data.BarType
import gg.rsmod.plugins.content.skills.smithing.data.SmithingType
import gg.rsmod.util.BitManipulation
import gg.rsmod.util.Misc
import java.lang.ref.WeakReference
import kotlin.math.max

/**
 * The interface key used by inventory overlays
 */
const val INVENTORY_INTERFACE_KEY = 93

/**
 * The id of the script used to initialise the interface overlay options. The 'big' variant of this script
 * is used as it supports up to eight options rather than five.
 *
 * https://github.com/RuneStar/cs2-scripts/blob/master/scripts/[clientscript,interface_inv_init_big].cs2
 */
const val INTERFACE_INV_INIT_BIG = 150

const val MAKE_QUANTITY_VARBIT = 8095

const val MAKE_MAX_QUANTITY_VARBIT = 8094

fun Player.openShop(shop: String) {
    val s = world.getShop(shop)
    if (s != null) {
        attr[CURRENT_SHOP_ATTR] = s
        setVarp(118, 4) // main stock container id
        if(s.containsSamples) {
            setVarp(1496, 6) // free sample stock container id
        } else {
            setVarp(1496, -1)
        }
        sendTempVarbit(532, 995) // currency
        shopDirty = true
        setVarc(199, -1)
        openInterface(interfaceId = 621, dest = InterfaceDestination.TAB_AREA)
        openInterface(interfaceId = 620, dest = InterfaceDestination.MAIN_SCREEN)

        for (i in 0..40) {
            setVarc(946 + i, 0) // sets price amount on individual item container
        }
        setInterfaceEvents(
            interfaceId = 620,
            component = 25,
            from = 0,
            to = s.items.filterNotNull().size * 6,
            setting = 1150
        )
        if (s.sampleItems.filterNotNull().isNotEmpty()) {
            setInterfaceEvents(
                interfaceId = 620,
                component = 26,
                from = 0,
                to = s.sampleItems.filterNotNull().size * 4,
                setting = 1150
            )
        }
        setComponentText(interfaceId = 620, component = 20, text = s.name)
        if(s.purchasePolicy == PurchasePolicy.BUY_TRADEABLES) {
            setComponentHidden(interfaceId = 620, component = 19, hidden = false)
        }
    } else {
        World.logger.warn { "Player \"$username\" is unable to open shop \"$shop\" as it does not exist." }
    }
}

/**
 * Used primarily for firemaking, and finding the next available tile
 * for the player to walk to. Another use-case I found was for bird nests
 * from woodcutting
 */
fun Player.findWesternTile() : Tile {
    return listOf(
        Direction.WEST,
        Direction.EAST,
        Direction.SOUTH,
        Direction.NORTH
    ).firstNotNullOfOrNull { direction ->
        if (world.collision.isBlocked(tile, direction, false)) {
            null
        } else {
            tile.step(direction, 1).takeUnless(world.collision::isClipped)
        }
    } ?: tile
}

fun Player.message(message: String, type: ChatMessageType = ChatMessageType.GAME_MESSAGE, username: String? = null) {
    write(MessageGameMessage(type = type.id, message = message, username = username))
}

fun Player.filterableMessage(message: String) {
    write(MessageGameMessage(type = ChatMessageType.FILTERED.id, message = message, username = null))
}

fun Player.runClientScript(id: Int, vararg args: Any) {
    write(RunClientScriptMessage(id, *args))
}

fun Player.focusTab(tab: GameframeTab) {
    runClientScript(915, tab.id)
}

fun Player.setInterfaceUnderlay(color: Int, transparency: Int) {
    runClientScript(2524, color, transparency)
}

fun Player.setInterfaceEvents(interfaceId: Int, component: Int, range: IntRange, setting: Int) {
    write(IfSetEventsMessage(hash = ((interfaceId shl 16) or component), fromChild = range.first, toChild = range.last, setting = setting))
}

fun Player.setComponentText(interfaceId: Int, component: Int, text: String) {
    write(IfSetTextMessage(interfaceId, component, text))
}

fun Player.setVarcString(id: Int, text: String) {
    write(VarcStringMessage(id, text))
}

fun Player.setComponentHidden(interfaceId: Int, component: Int, hidden: Boolean) {
    write(IfSetHideMessage(hash = ((interfaceId shl 16) or component), hidden = hidden))
}

fun Player.setComponentSprite(interfaceId: Int, component: Int, sprite: Int) {
    write(IfSetSpriteMessage(hash = ((interfaceId shl 16) or component), sprite = sprite))
}

fun Player.setComponentScrollVertical(interfaceId: Int, component: Int, height: Int) {
    write(IfSetScrollVerticalMessage(hash = ((interfaceId shl 16) or component), height = height))
}

fun Player.setComponentItem(interfaceId: Int, component: Int, item: Int, amountOrZoom: Int) {
    write(IfSetObjectMessage(hash = ((interfaceId shl 16) or component), item = item, amount = amountOrZoom))
}

fun Player.setComponentNpcHead(interfaceId: Int, component: Int, npc: Int) {
    write(IfSetNpcHeadMessage(hash = ((interfaceId shl 16) or component), npc = npc))
}

fun Player.setComponentPlayerHead(interfaceId: Int, component: Int) {
    write(IfSetPlayerHeadMessage(hash = ((interfaceId shl 16) or component)))
}

fun Player.setComponentAnim(interfaceId: Int, component: Int, anim: Int) {
    write(IfSetAnimMessage(hash = ((interfaceId shl 16) or component), anim = anim))
}

/**
 * Use this method to open an interface id on top of an [InterfaceDestination]. This
 * method should always be preferred over
 *
 * ```
 * openInterface(parent: Int, child: Int, component: Int, type: Int, isMainComponent: Boolean)
 * ```
 *
 * as it holds logic that must be handled for certain [InterfaceDestination]s.
 */
fun Player.openInterface(interfaceId: Int, dest: InterfaceDestination, fullscreen: Boolean = false) {
    val displayMode = if (!fullscreen) interfaces.displayMode else DisplayMode.FULLSCREEN
    val child = getChildId(dest, displayMode)
    val parent = getDisplayComponentId(displayMode)
    if (displayMode == DisplayMode.FULLSCREEN) {
        openOverlayInterface(displayMode)
    }
    openInterface(parent, child, interfaceId, if (dest.clickThrough) 1 else 0, isModal = dest == InterfaceDestination.MAIN_SCREEN || dest == InterfaceDestination.MAIN_SCREEN_FULL)
}

/**
 * Use this method to open an interface id on top of an [InterfaceDestination]. This
 * method should always be preferred over
 *
 * ```
 * openInterface(parent: Int, child: Int, component: Int, type: Int, isMainComponent: Boolean)
 * ```
 *
 * as it holds logic that must be handled for certain [InterfaceDestination]s.
 */
fun Player.openChatboxInterface(interfaceId: Int, child: Int, dest: InterfaceDestination, fullscreen: Boolean = false) {
    val displayMode = if (!fullscreen) interfaces.displayMode else DisplayMode.FULLSCREEN
    val parent = dest.interfaceId
    if (displayMode == DisplayMode.FULLSCREEN) {
        openOverlayInterface(displayMode)
    }
    openInterface(parent, child, interfaceId, if (dest.clickThrough) 1 else 0, isModal = dest == InterfaceDestination.MAIN_SCREEN)
}

/**
 * Use this method to "re-open" an [InterfaceDestination]. This method should always
 * be preferred over
 *
 * ```
 * openInterface(parent: Int, child: Int, interfaceId: Int, type: Int, mainInterface: Boolean)
 * ````
 *
 * as it holds logic that must be handled for certain [InterfaceDestination]s.
 */
fun Player.openInterface(dest: InterfaceDestination, autoClose: Boolean = false) {
    val displayMode = if (!autoClose) interfaces.displayMode else DisplayMode.FULLSCREEN
    val child = getChildId(dest, displayMode)
    val parent = getDisplayComponentId(displayMode)
    if (displayMode == DisplayMode.FULLSCREEN) {
        openOverlayInterface(displayMode)
    }
    openInterface(parent, child, dest.interfaceId, if (dest.clickThrough) 1 else 0, isModal = dest == InterfaceDestination.MAIN_SCREEN)
}

fun Player.openInterface(parent: Int, child: Int, interfaceId: Int, type: Int = 0, isModal: Boolean = false) {
    if (isModal) {
        interfaces.openModal(parent, child, interfaceId)
    } else {
        interfaces.open(parent, child, interfaceId)
    }
    write(IfOpenSubMessage(parent, child, interfaceId, type))
}

fun Player.closeInterface(interfaceId: Int) {
    if (interfaceId == interfaces.getModal()) {
        interfaces.setModal(-1)
    }
    val hash = interfaces.close(interfaceId)
    if (hash != -1) {
        write(IfCloseSubMessage(hash))
    }
}

fun Player.closeInterface(dest: InterfaceDestination) {
    val displayMode = interfaces.displayMode
    val child = getChildId(dest, displayMode)
    val parent = getDisplayComponentId(displayMode)
    val hash = interfaces.close(parent, child)
    if (hash != -1) {
        write(IfCloseSubMessage((parent shl 16) or child))
    }
}

fun Player.closeComponent(parent: Int, child: Int) {
    interfaces.close(parent, child)
    write(IfCloseSubMessage((parent shl 16) or child))
}

fun Player.closeInputDialog() {
    runClientScript(101)
}

fun Player.getInterfaceAt(dest: InterfaceDestination): Int {
    val displayMode = interfaces.displayMode
    val child = getChildId(dest, displayMode)
    val parent = getDisplayComponentId(displayMode)
    return interfaces.getInterfaceAt(parent, child)
}

fun Player.isInterfaceVisible(interfaceId: Int): Boolean = interfaces.isVisible(interfaceId)

fun Player.toggleDisplayInterface(newMode: DisplayMode) {
    if (interfaces.displayMode != newMode) {
        interfaces.displayMode = newMode

        openOverlayInterface(newMode)
        InterfaceDestination.values.filter { pane -> pane.interfaceId != -1 }.forEach { pane ->
            openInterface(pane.interfaceId, pane)
        }
    }
}

fun Player.openOverlayInterface(displayMode: DisplayMode) {
    if (displayMode != interfaces.displayMode) {
        interfaces.setVisible(parent = getDisplayComponentId(interfaces.displayMode), child = getChildId(InterfaceDestination.MAIN_SCREEN, interfaces.displayMode), visible = false)
    }
    val component = getDisplayComponentId(displayMode)
    interfaces.setVisible(parent = getDisplayComponentId(displayMode), child = 0, visible = true)
    write(IfOpenTopMessage(component, 1))
}

fun Player.sendItemContainer(key: Int, items: Array<Item?>) {
    write(UpdateInvFullMessage(containerKey = key, items = items, invother = false))
}

fun Player.sendItemContainer(key: Int, container: ItemContainer) = sendItemContainer(key, container.rawItems)

fun Player.updateItemContainer(interfaceId: Int, component: Int, oldItems: Array<Item?>, newItems: Array<Item?>) {
    write(UpdateInvPartialMessage(interfaceId = interfaceId, component = component, oldItems = oldItems, newItems = newItems))
}

fun Player.updateItemContainer(interfaceId: Int, component: Int, key: Int, oldItems: Array<Item?>, newItems: Array<Item?>) {
    write(UpdateInvPartialMessage(interfaceId = interfaceId, component = component, containerKey = key, oldItems = oldItems, newItems = newItems))
}

fun Player.updateItemContainer(key: Int, oldItems: Array<Item?>, newItems: Array<Item?>) {
    write(UpdateInvPartialMessage(containerKey = key, oldItems = oldItems, newItems = newItems))
}

/**
 * Sends a container type referred to as 'invother' in CS2, which is used for displaying a second container with
 * the same container key. An example of this is the trade accept screen, where the list of items being traded is stored
 * in container 90 for both the player's container, and the partner's container. A container becomes 'invother' when it's
 * component hash is less than -70000, which internally translates the container key to (key + 32768). We can achieve this by either
 * sending a component hash of less than -70000, or by setting the key ourselves. I feel like the latter makes more sense.
 *
 * Special thanks to Polar for explaining this concept to me.
 *
 * https://github.com/RuneStar/cs2-scripts/blob/a144f1dceb84c3efa2f9e90648419a11ee48e7a2/scripts/script768.cs2
 *
 *
 * -- Note* Alycia
 * It appears that 667 has a new way of doing this, and that is by sending a byte in the packet
 * to flag if the inventory/container is an "invother". More research into this would provide
 * better documentation, but I assume this is pretty self-explanatory with regard to the above note.
 *
 */
fun Player.sendItemContainerOther(key: Int, container: ItemContainer) {
    write(UpdateInvFullMessage(containerKey = key, items = container.rawItems, invother = true))
}

fun Player.sendRunEnergy(energy: Int) {
    write(UpdateRunEnergyMessage(energy))
}

fun Player.playSound(id: Int, volume: Int = 1, delay: Int = 0) {
    write(SynthSoundMessage(sound = id, volume = volume, delay = delay))
}

fun Player.playSong(id: Int) {
    write(MidiSongMessage(0, id, 255))
}

fun Player.getVarp(id: Int): Int = varps.getState(id)

fun Player.setVarp(id: Int, value: Int) {
    varps.setState(id, value)
}

fun Player.toggleVarp(id: Int) {
    varps.setState(id, varps.getState(id) xor 1)
}

fun Player.syncVarp(id: Int) {
    setVarp(id, getVarp(id))
}

fun Player.getVarbit(id: Int): Int {
    val def = world.definitions.get(VarbitDef::class.java, id)
    return varps.getBit(def.varp, def.startBit, def.endBit)
}

fun Player.setVarbit(id: Int, value: Int) {
    val def = world.definitions.get(VarbitDef::class.java, id)
    varps.setBit(def.varp, def.startBit, def.endBit, value)
}

fun Player.setVarc(id: Int, value: Int) {
    val message = if (id in -Byte.MAX_VALUE..Byte.MAX_VALUE) VarcSmallMessage(id, value) else VarcLargeMessage(id, value)
    write(message)
    varcs[id] = value
}

fun Player.getVarc(id: Int) : Int {
    return varcs[id]
}

/**
 * Write a varbit message to the player's client without actually modifying
 * its varp value in [Player.varps].
 */
fun Player.sendTempVarbit(id: Int, value: Int) {
    val def = world.definitions.get(VarbitDef::class.java, id)
    val state = BitManipulation.setBit(varps.getState(def.varp), def.startBit, def.endBit, value)
    val message = if (state in -Byte.MAX_VALUE..Byte.MAX_VALUE) VarpSmallMessage(def.varp, state) else VarpLargeMessage(def.varp, state)
    write(message)
}


fun Player.toggleVarbit(id: Int) {
    val def = world.definitions.get(VarbitDef::class.java, id)
    varps.setBit(def.varp, def.startBit, def.endBit, getVarbit(id) xor 1)
}

fun Player.setMapFlag(x: Int, z: Int) {
    write(SetMapFlagMessage(x, z))
}

fun Player.clearMapFlag() {
    setMapFlag(255, 255)
}

fun Player.sendOption(option: String, id: Int, leftClick: Boolean = false) {
    check(id in 1..options.size) { "Option id must range from [1-${options.size}]" }
    val index = id - 1
    options[index] = option
    write(SetOpPlayerMessage(option, index, leftClick, -1))
}

/**
 * Checks if the player has an option with the name [option] (case-sensitive).
 */
fun Player.hasOption(option: String, id: Int = -1): Boolean {
    check(id == -1 || id in 1..options.size) { "Option id must range from [1-${options.size}]" }
    return if (id != -1) options.any { it == option } else options[id - 1] == option
}

/**
 * Removes the option with [id] from this player.
 */
fun Player.removeOption(id: Int) {
    check(id in 1..options.size) { "Option id must range from [1-${options.size}]" }
    val index = id - 1
    write(SetOpPlayerMessage("null", index, false, -1))
    options[index] = null
}

fun Player.getStorageBit(storage: BitStorage, bits: StorageBits): Int = storage.get(this, bits)

fun Player.hasStorageBit(storage: BitStorage, bits: StorageBits): Boolean = storage.get(this, bits) != 0

fun Player.setStorageBit(storage: BitStorage, bits: StorageBits, value: Int) {
    storage.set(this, bits, value)
}

fun Player.toggleStorageBit(storage: BitStorage, bits: StorageBits) {
    storage.set(this, bits, storage.get(this, bits) xor 1)
}

fun Player.heal(amount: Int, capValue: Int = 0) {
    alterLifepoints(value = amount, capValue = capValue)
}

fun Player.hasSpellbook(book: Spellbook): Boolean = getVarbit(4070) == book.id

fun Player.getSpellbook(): Spellbook = Spellbook.values.first { getVarbit(4070) == it.id }

fun Player.setSpellbook(book: Spellbook) = setVarbit(4070, book.id)

fun Player.getWeaponType(): Int = attr[LAST_KNOWN_WEAPON_TYPE] ?: 0

fun Player.getMaximumMakeQuantity(): Int = getVarbit(MAKE_MAX_QUANTITY_VARBIT)

fun Player.getMakeQuantity(): Int = getVarbit(MAKE_QUANTITY_VARBIT)

fun Player.getAttackStyle(): Int = getVarp(43)

fun Player.hasWeaponType(type: WeaponType, vararg others: WeaponType): Boolean = getWeaponType() == type.id || others.isNotEmpty() && getWeaponType() in others.map { it.id }

fun Player.hasEquipped(slot: EquipmentType, vararg items: Int): Boolean {
    check(items.isNotEmpty()) { "Items shouldn't be empty." }
    return items.any { equipment.hasAt(slot.id, it) }
}

fun Player.hasEquipped(items: IntArray) = items.all { equipment.contains(it) }

fun Player.getEquipment(slot: EquipmentType): Item? = equipment[slot.id]

fun Player.setSkullIcon(icon: SkullIcon) {
    skullIcon = icon.id
    addBlock(UpdateBlockType.APPEARANCE)
}

fun Player.skull(icon: SkullIcon, durationCycles: Int) {
    check(icon != SkullIcon.NONE)
    setSkullIcon(icon)
    timers[SKULL_ICON_DURATION_TIMER] = durationCycles
}

fun Player.hasSkullIcon(icon: SkullIcon): Boolean = skullIcon == icon.id

fun Player.isClientResizable(): Boolean = interfaces.displayMode == DisplayMode.RESIZABLE_NORMAL || interfaces.displayMode == DisplayMode.RESIZABLE_LIST

fun Player.inWilderness(): Boolean = false

fun Player.sendWorldMapTile() {
    runClientScript(1749, tile.as30BitInteger)
}
fun Player.sendWeaponComponentInformation() {
    for(slot in 11..14) {
        setInterfaceEvents(interfaceId = 884, component = slot, from = -1, to = 0, setting = 2)
    }
    val weapon = getEquipment(EquipmentType.WEAPON)
    if (weapon != null) {
        val definition = world.definitions.get(ItemDef::class.java, weapon.id)
        attr[LAST_KNOWN_WEAPON_TYPE] = max(0, definition.weaponType)
    } else {
        attr[LAST_KNOWN_WEAPON_TYPE] = WeaponType.NONE.id
    }
}

fun Player.calculateAndSetCombatLevel(): Boolean {
    val old = combatLevel

    val attack = getSkills().getMaxLevel(Skills.ATTACK)
    val defence = getSkills().getMaxLevel(Skills.DEFENCE)
    val strength = getSkills().getMaxLevel(Skills.STRENGTH)
    val hitpoints = getSkills().getMaxLevel(Skills.HITPOINTS)
    val prayer = getSkills().getMaxLevel(Skills.PRAYER)
    val ranged = getSkills().getMaxLevel(Skills.RANGED)
    val magic = getSkills().getMaxLevel(Skills.MAGIC)

    val base = Ints.max(strength + attack, magic * 2, ranged * 2)

    combatLevel = ((base * 1.3 + defence + hitpoints + prayer / 2) / 4).toInt()

    val changed = combatLevel != old
    if (changed) {
        addBlock(UpdateBlockType.APPEARANCE)
        return true
    }

    return false
}

fun Player.buildSmithingInterface(bar: BarType) {
    val type = BarProducts.getBars(bar)
    attr[BAR_TYPE] = bar.item
    val componentIds = arrayOf(266, 169, 65, 97, 81, 89, 209, 161, 169)
    // Hide all the "extra" components, dart tips, lanterns, claws, etc..
    componentIds.forEach { component ->
        setComponentHidden(interfaceId = 300, component = component, hidden = true)
    }

    // Unlocks pickaxe component
    setComponentHidden(interfaceId = 300, component = 266, hidden = false)

    type.filterNotNull().forEach {

        // Unlock "extra" components if the type exists
        when(it.smithingType) {
            SmithingType.TYPE_GRAPPLE_TIP -> setComponentHidden(interfaceId = 300, component = 169, hidden = false)
            SmithingType.TYPE_DART_TIPS -> setComponentHidden(interfaceId = 300, component = 65, hidden = false)
            SmithingType.TYPE_IRON_SPIT -> setComponentHidden(interfaceId = 300, component = 89, hidden = false)
            SmithingType.TYPE_WIRE -> setComponentHidden(interfaceId = 300, component = 81, hidden = false)
            SmithingType.TYPE_CLAWS ->  setComponentHidden(interfaceId = 300, component = 209, hidden = false)
            SmithingType.TYPE_OIL_LANTERN ->  setComponentHidden(interfaceId = 300, component = 161, hidden = false)
            SmithingType.TYPE_BULLSEYE ->  setComponentHidden(interfaceId = 300, component = 161, hidden = false)
            SmithingType.TYPE_STUDS -> setComponentHidden(interfaceId = 300, component = 97, hidden = false)
            else -> {}
        }

        var color = ""

        // If the level requirement matches or exceeds the players current level
        if(getSkills().getCurrentLevel(Skills.SMITHING) >= it.level) {
            color = "<col=FFFFFF>"
        }

        // Set the items name
        setComponentText(interfaceId = 300, component = it.smithingType.componentId, text = "$color${Misc.formatSentence(it.smithingType.name.replace("TYPE_", "").replace("_", " "))}")

        // If the players inventory contains the proper amount of bars
        if (inventory.getItemCount(it.barType.item) >= it.smithingType.barRequirement) {
            color = "<col=2DE120>"
            val str = "Bar"
            setComponentText(interfaceId = 300, component = it.smithingType.componentId + 1, text = "$color${it.smithingType.barRequirement} ${str.pluralSuffix(it.smithingType.barRequirement)}")
        }

        // Finally, send the item on the interface
        setComponentItem(interfaceId = 300, component = it.smithingType.componentId - 1, item = it.result, amountOrZoom = it.smithingType.producedAmount)
    }

    // Send the title
    setComponentText(interfaceId = 300, component = 14, text = bar.barName)

    // Open the main interface
    openInterface(dest = InterfaceDestination.MAIN_SCREEN, interfaceId = 300)
}
fun Player.calculateDeathContainers(): DeathContainers {
    /*var keepAmount = if (hasSkullIcon(SkullIcon.WHITE)) 0 else 3
    if (attr[PROTECT_ITEM_ATTR] == true) {
        keepAmount++
    }

    val keptContainer = ItemContainer(world.definitions, keepAmount, ContainerStackType.NO_STACK)
    val lostContainer = ItemContainer(world.definitions, inventory.capacity + equipment.capacity, ContainerStackType.NORMAL)

    var totalItems = inventory.rawItems.filterNotNull() + equipment.rawItems.filterNotNull()
    val valueService = world.getService(ItemMarketValueService::class.java)

    totalItems = if (valueService != null) {
        totalItems.sortedBy { it.id }.sortedWith(compareByDescending { valueService.get(it.id) })
    } else {
        totalItems.sortedBy { it.id }.sortedWith(compareByDescending { world.definitions.get(ItemDef::class.java, it.id).cost })
    }

    totalItems.forEach { item ->
        if (keepAmount > 0 && !keptContainer.isFull) {
            val add = keptContainer.add(item, assureFullInsertion = false)
            keepAmount -= add.completed
            if (add.getLeftOver() > 0) {
                lostContainer.add(item.id, add.getLeftOver())
            }
        } else {
            lostContainer.add(item)
        }
    }
     */
    return DeathContainers(kept = ItemContainer(world.definitions, 3, ContainerStackType.NO_STACK), lost = ItemContainer(world.definitions, inventory.capacity + equipment.capacity, ContainerStackType.NORMAL))
}

fun openTanningInterface(player: Player) {
    player.openInterface(dest = InterfaceDestination.MAIN_SCREEN, interfaceId = 324)
}

fun essenceTeleport(player: Player, dialogue: String = "Senventior disthine molenko!", targetTile: Tile) {
    val npc = player.getInteractingNpc()
    npc.attr[INTERACTING_PLAYER_ATTR] = WeakReference(player)
    val p = npc.getInteractingPlayer()
    npc.queue {
        npc.facePawn(npc.getInteractingPlayer())
        npc.forceChat(dialogue)
        npc.graphic(108)
        val projectile = npc.createProjectile(p, 109, ProjectileType.MAGIC)
        p.world.spawn(projectile)
        p.playSound(127)
        wait(MagicCombatStrategy.getHitDelay(npc.tile, p.tile) + 1)
        p.attr[ESSENCE_MINE_INTERACTED_WITH] = npc.id
        p.moveTo(targetTile)
        wait(1)
        p.graphic(110)
        p.playSound(126)
    }
}

// Note: this does not take ground items, that may belong to the player, into
// account.
fun Player.hasItem(item: Int, amount: Int = 1): Boolean = containers.values.firstOrNull { container -> container.getItemCount(item) >= amount } != null

fun Player.isPrivilegeEligible(to: String): Boolean = world.privileges.isEligible(privilege, to)

fun Player.getSummoningBonus(): Int = equipmentBonuses[10]
fun Player.getStrengthBonus(): Int = equipmentBonuses[11]

fun Player.getRangedStrengthBonus(): Int = equipmentBonuses[12]

fun Player.getMagicDamageBonus(): Int = equipmentBonuses[14]

fun Player.getPrayerBonus(): Int = equipmentBonuses[13]

fun Player.completedAllQuests() : Boolean {
    return getVarp(QUEST_POINT_VARP) >= Quest.quests.sumOf { it.pointReward }
}
fun Player.checkEquipment() {
    equipment.filterNotNull().forEach { item ->
        if(item.id == Items.QUEST_POINT_HOOD || item.id == Items.QUEST_POINT_CAPE) {
            if (!completedAllQuests()) {
                disableEquipment(item.id)
            }
        }
    }
}

fun Player.disableEquipment(itemId: Int) {
    val itemName = world.definitions.get(ItemDef::class.java, itemId).name
    equipment.remove(itemId)
    if (inventory.hasSpace) {
        inventory.add(itemId)
        message("Your $itemName was removed from your equipment and added to your inventory.")
    } else {
        bank.add(itemId)
        message("Your $itemName was removed from your equipment and added to your bank.")
    }
}