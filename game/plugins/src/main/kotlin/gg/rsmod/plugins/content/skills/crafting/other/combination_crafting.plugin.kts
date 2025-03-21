package gg.rsmod.plugins.content.skills.crafting.other

val primaries = CombinationData.values.map { it.items[0] }.toIntArray()

primaries.forEach {
    val item = it
    val def = CombinationData.combinationDefinitions[item] ?: return@forEach
    if (def.tool != CombinationTool.NONE) {
        on_item_on_item(item1 = item, item2 = def.tool.item) {
            player.queue {
                CombinationAction.combine(this, def)
            }
        }
    } else {
        on_item_on_item(itemUsed = item, itemsList = def.items) {
            player.queue {
                CombinationAction.combine(this, def)
            }
        }
    }
}