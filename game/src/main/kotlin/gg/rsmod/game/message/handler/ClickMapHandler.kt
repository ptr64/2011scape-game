package gg.rsmod.game.message.handler

import gg.rsmod.game.message.MessageHandler
import gg.rsmod.game.message.impl.MoveGameClickMessage
import gg.rsmod.game.message.impl.SetMapFlagMessage
import gg.rsmod.game.model.MovementQueue
import gg.rsmod.game.model.World
import gg.rsmod.game.model.attr.LAST_KNOWN_RUN_STATE
import gg.rsmod.game.model.attr.NO_CLIP_ATTR
import gg.rsmod.game.model.entity.Client
import gg.rsmod.game.model.entity.Entity
import gg.rsmod.game.model.priv.Privilege
import gg.rsmod.game.model.queue.TaskPriority
import gg.rsmod.game.model.timer.STUN_TIMER
import gg.rsmod.game.sync.block.UpdateBlockType

/**
 * @author Tom <rspsmods@gmail.com>
 */
class ClickMapHandler : MessageHandler<MoveGameClickMessage> {

    override fun handle(client: Client, world: World, message: MoveGameClickMessage) {
        if (!client.lock.canMove()) {
            return
        }

        if (client.timers.has(STUN_TIMER)) {
            client.write(SetMapFlagMessage(255, 255))
            client.writeMessage(Entity.YOURE_STUNNED)
            return
        }

        log(client, "Click map: x=%d, z=%d, type=%d", message.x, message.z, message.movementType)

        /**
         * Handles resting
         */
        if(client.isResting()) {
            val standUpAnimation = 11788
            client.queue(TaskPriority.STRONG) {
                client.lock()
                client.animate(standUpAnimation)
                wait(3)
                client.varps.setState(173, client.attr[LAST_KNOWN_RUN_STATE]!!.toInt())
                client.unlock()
                val stepType = if (message.movementType == 1) MovementQueue.StepType.FORCED_RUN else MovementQueue.StepType.NORMAL
                val noClip = client.attr[NO_CLIP_ATTR] ?: false
                client.addBlock(UpdateBlockType.MOVEMENT_TYPE)
                client.walkTo(message.x, message.z, stepType, detectCollision = !noClip)
            }
            return
        }

        client.closeInterfaceModal()
        client.interruptQueues()
        client.resetInteractions()

        /**
         * Normal movement
         */
        if (message.movementType == 2 && world.privileges.isEligible(client.privilege, Privilege.ADMIN_POWER)) {
            client.moveTo(message.x, message.z, client.tile.height)
        } else {
            val stepType = if (message.movementType == 1) MovementQueue.StepType.FORCED_RUN else MovementQueue.StepType.NORMAL
            val noClip = client.attr[NO_CLIP_ATTR] ?: false
            client.addBlock(UpdateBlockType.MOVEMENT_TYPE)
            client.walkTo(message.x, message.z, stepType, detectCollision = !noClip)
        }
    }
}