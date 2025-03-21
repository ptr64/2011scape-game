package gg.rsmod.game.message.handler

import gg.rsmod.game.message.MessageHandler
import gg.rsmod.game.message.impl.OpHeldUMessage
import gg.rsmod.game.model.World
import gg.rsmod.game.model.attr.*
import gg.rsmod.game.model.entity.Client
import gg.rsmod.game.model.entity.Entity
import java.lang.ref.WeakReference

/**
 * @author Tom <rspsmods@gmail.com>
 */
class OpHeldUHandler : MessageHandler<OpHeldUMessage> {

    override fun handle(client: Client, world: World, message: OpHeldUMessage) {
        val fromComponentHash = message.fromComponentHash
        val fromInterfaceId = fromComponentHash shr 16
        val fromComponent = fromComponentHash and 0xFFFF
        val fromSlot = message.fromSlot
        val fromItemId = message.fromItem

        val toComponentHash = message.toComponentHash
        val toInterfaceId = toComponentHash shr 16
        val toComponent = toComponentHash and 0xFFFF
        val toSlot = message.toSlot
        val toItemId = message.toItem

        val fromItem = client.inventory[fromSlot] ?: return
        val toItem = client.inventory[toSlot] ?: return

        if (fromItem.id != fromItemId || toItem.id != toItemId) {
            return
        }

        if (!client.lock.canItemInteract()) {
            return
        }

        log(
            client,
            "Item on item: from_component=[%d,%d], to_component=[%d,%d], from_item=%d, from_slot=%d, to_item=%d, to_slot=%d",
            fromInterfaceId,
            fromComponent,
            toInterfaceId,
            toComponent,
            fromItem.id,
            fromSlot,
            toItem.id,
            toSlot
        )

        client.attr[INTERACTING_ITEM] = WeakReference(fromItem)
        client.attr[INTERACTING_ITEM_ID] = fromItem.id
        client.attr[INTERACTING_ITEM_SLOT] = fromSlot

        client.attr[OTHER_ITEM_ATTR] = WeakReference(toItem)
        client.attr[OTHER_ITEM_ID_ATTR] = toItem.id
        client.attr[OTHER_ITEM_SLOT_ATTR] = toSlot

        client.interruptQueues()
        client.resetInteractions()

        val handled = world.plugins.executeItemOnItem(client, fromItem.id, toItem.id)

        if (!handled) {
            client.writeFilterableMessage(Entity.NOTHING_INTERESTING_HAPPENS)
            if (world.devContext.debugItemActions) {
                client.writeConsoleMessage(
                    "Unhandled item on item: [from_item=${fromItem.id}, to_item=${toItem.id}, from_slot=$fromSlot, to_slot=$toSlot, " +
                            "from_component=[$fromInterfaceId:$fromComponent], to_component=[$toInterfaceId:$toComponent]]"
                )
            }
        }

    }
}