package xyz.bluspring.mocapcpmsupport.actions

import com.tom.cpl.nbt.NBTTagCompound
import com.tom.cpm.common.ServerHandler
import com.tom.cpm.shared.config.PlayerData
import com.tom.cpm.shared.io.IOHelper
import com.tom.cpm.shared.network.ModelEventType
import com.tom.cpm.shared.network.NetH
import com.tom.cpm.shared.network.packet.ReceiveEventS2C
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.mt1006.mocap.mocap.actions.Action
import net.mt1006.mocap.mocap.actions.ComparableAction
import net.mt1006.mocap.mocap.files.RecordingFiles
import net.mt1006.mocap.mocap.playing.playback.ActionContext
import xyz.bluspring.mocapcpmsupport.*
import xyz.bluspring.mocapcpmsupport.mixin.ActionContextAccessor
import java.io.ByteArrayOutputStream

class SetCPMDataAction(val playerData: PlayerData?) : ComparableAction {
    constructor(reader: RecordingFiles.Reader) : this(readPlayerData(reader))
    constructor(entity: Entity) : this(getPlayerData(entity))

    val lastModelData = playerData?.data?.copyOf()
    val lastGestureData = playerData?.gestureData?.copyOf()

    override fun execute(ctx: ActionContext): Action.Result {
        if (ctx.entity !is ServerPlayer)
            return Action.Result.IGNORED

        try {
            ((ctx.entity as ServerPlayer).connection as NetH.ServerNetH).`cpm$setEncodedModelData`(playerData)
            for (player in (ctx as ActionContextAccessor).packetTargets.players) {
                if (!(player.connection as NetH.ServerNetH).`cpm$hasMod`())
                    continue

                ServerHandler.netHandler.sendPlayerData(ctx.entity as ServerPlayer, player)

                if (lastGestureData != null) {
                    val evt = NBTTagCompound()
                    evt.setByteArray("gesture", lastGestureData)

                    ServerHandler.netHandler.sendPacketToTracking(ctx.entity as ServerPlayer,
                        ReceiveEventS2C(ServerHandler.netHandler.getPlayerId(ctx.entity as ServerPlayer), evt)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Action.Result.OK
    }

    override fun differs(other: ComparableAction?): Boolean {
        return other == null || (this.playerData != (other as SetCPMDataAction).playerData || !this.lastGestureData.contentEquals(other.lastGestureData) || !this.lastModelData.contentEquals(other.lastModelData))
    }

    override fun write(
        writer: RecordingFiles.Writer
    ) {
        writer.addByte(MocapCPMSupport.SET_CPM_DATA_ACTION.id)
        writer.addBoolean(playerData != null)

        if (playerData != null) {
            writer.writeByteArray(lastModelData!!)

            writer.addBoolean(playerData.forced)
            writer.addBoolean(playerData.save)

            writer.writeByteArray(lastGestureData!!)

            writer.writeEnumSet(playerData.eventSubs, ModelEventType::class.java)
            writer.writeEnumSet(playerData.selfSubs, ModelEventType::class.java)

            writer.addBoolean(playerData.state.updated)
            writer.addFloat(playerData.state.falling)
            writer.addBoolean(playerData.state.creativeFlying)
            writer.addBoolean(playerData.state.inMenu)
            writer.addFloat(playerData.state.health)
            writer.addFloat(playerData.state.hunger)
            writer.addFloat(playerData.state.air)

            writer.addInt(playerData.pluginStates.size)
            for ((key, state) in playerData.pluginStates) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                state.write(IOHelper(byteArrayOutputStream))

                writer.addString(key)
                writer.writeByteArray(byteArrayOutputStream.toByteArray())
            }
        }
    }

    companion object {
        fun readPlayerData(reader: RecordingFiles.Reader): PlayerData? {
            if (!reader.readBoolean())
                return null

            val data = PlayerData()
            data.setModel(reader.readByteArray(), reader.readBoolean(), reader.readBoolean())

            data.gestureData = reader.readByteArray()

            data.eventSubs.addAll(reader.readEnumSet(ModelEventType::class.java))
            data.selfSubs.addAll(reader.readEnumSet(ModelEventType::class.java))

            data.state.updated = reader.readBoolean()
            data.state.falling = reader.readFloat()
            data.state.creativeFlying = reader.readBoolean()
            data.state.inMenu = reader.readBoolean()
            data.state.health = reader.readFloat()
            data.state.hunger = reader.readFloat()
            data.state.air = reader.readFloat()

            val mapSize = reader.readInt()
            for (i in 0 until mapSize) {
                val key = reader.readString()
                val byteArray = reader.readByteArray()
                val nbt = NBTTagCompound()
                nbt.read(IOHelper(byteArray))

                data.pluginStates[key] = nbt
            }

            return data
        }

        fun getPlayerData(entity: Entity): PlayerData? {
            if (entity !is ServerPlayer)
                return null

            return (entity.connection as NetH.ServerNetH).`cpm$getEncodedModelData`()
        }
    }
}