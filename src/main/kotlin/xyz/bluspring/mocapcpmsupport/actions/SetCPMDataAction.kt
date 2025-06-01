package xyz.bluspring.mocapcpmsupport.actions

import com.tom.cpm.common.ServerHandler
import com.tom.cpm.shared.config.PlayerData
import com.tom.cpm.shared.network.NetH
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.mt1006.mocap.mocap.actions.Action
import net.mt1006.mocap.mocap.actions.ComparableAction
import net.mt1006.mocap.mocap.files.RecordingFiles
import net.mt1006.mocap.mocap.playing.playback.ActionContext
import xyz.bluspring.mocapcpmsupport.MocapCPMSupport
import xyz.bluspring.mocapcpmsupport.mixin.ActionContextAccessor

class SetCPMDataAction(val playerData: PlayerData?) : ComparableAction {
    constructor(reader: RecordingFiles.Reader) : this(readPlayerData(reader))
    constructor(entity: Entity) : this(getPlayerData(entity))

    val lastModelData = playerData?.data
    val lastGestureData = playerData?.gestureData

    override fun execute(ctx: ActionContext): Action.Result {
        if (ctx.entity !is ServerPlayer)
            return Action.Result.IGNORED

        try {
            ((ctx.entity as ServerPlayer).connection as NetH.ServerNetH).`cpm$setEncodedModelData`(playerData)
            for (player in (ctx as ActionContextAccessor).packetTargets.players) {
                if (!(player.connection as NetH.ServerNetH).`cpm$hasMod`())
                    continue

                ServerHandler.netHandler.sendPlayerData(ctx.entity as ServerPlayer, player)
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
            writer.addInt(playerData.data.size)
            for (b in playerData.data) {
                writer.addByte(b)
            }
            writer.addBoolean(playerData.forced)
            writer.addBoolean(playerData.save)

            writer.addInt(playerData.gestureData.size)
            for (b in playerData.gestureData) {
                writer.addByte(b)
            }
        }
    }

    companion object {
        fun readPlayerData(reader: RecordingFiles.Reader): PlayerData? {
            if (!reader.readBoolean())
                return null

            val size = reader.readInt()
            val byteArray = ByteArray(size)
            for (i in 0 until size) {
                byteArray[i] = reader.readByte()
            }

            val data = PlayerData()
            data.setModel(byteArray, reader.readBoolean(), reader.readBoolean())

            val gestureSize = reader.readInt()
            val gestureByteArray = ByteArray(gestureSize)
            for (i in 0 until gestureSize) {
                gestureByteArray[i] = reader.readByte()
            }
            data.gestureData = gestureByteArray

            return data
        }

        fun getPlayerData(entity: Entity): PlayerData? {
            if (entity !is ServerPlayer)
                return null

            return (entity.connection as NetH.ServerNetH).`cpm$getEncodedModelData`()
        }
    }
}