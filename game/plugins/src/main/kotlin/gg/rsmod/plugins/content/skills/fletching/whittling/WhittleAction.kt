package gg.rsmod.plugins.content.skills.fletching.whittling

import gg.rsmod.game.fs.DefinitionSet
import gg.rsmod.game.model.queue.QueueTask
import gg.rsmod.plugins.api.Skills
import gg.rsmod.plugins.api.cfg.Items
import gg.rsmod.plugins.api.ext.doubleItemMessageBox
import gg.rsmod.plugins.api.ext.player
import kotlin.math.min

class WhittleAction(val definitions: DefinitionSet) {
    suspend fun cut(task: QueueTask, raw: Int, whittleItem: WhittleItem, amount: Int) {

        val player = task.player
        val inventory = player.inventory

        val maxCount = min(amount, inventory.getItemCount(raw))

        repeat(maxCount) {
            if(!canCut(task, raw, whittleItem)) {
                player.animate(-1)
                return@repeat
            }
            player.animate(1248)
            task.wait(2)
            if (!inventory.remove(raw, assureFullRemoval = true).hasSucceeded()) {
                return@repeat
            }
            inventory.add(whittleItem.product, whittleItem.amount)
            player.addXp(Skills.FLETCHING, whittleItem.experience)
            task.wait(1)
        }

    }

    private suspend fun canCut(task: QueueTask, raw: Int, data: WhittleItem) : Boolean {
        val player = task.player
        val inventory = player.inventory

        if(!inventory.contains(Items.KNIFE)) {
            return false
        }

        if(!inventory.contains(raw)) {
            return false
        }

        if(player.getSkills().getCurrentLevel(Skills.FLETCHING) < data.levelRequirement) {
            task.doubleItemMessageBox("You need a Fletching level of at least ${data.levelRequirement} to make a ${data.itemName}.", item1 = raw, item2 = data.product)
            return false
        }

        return true
    }

}