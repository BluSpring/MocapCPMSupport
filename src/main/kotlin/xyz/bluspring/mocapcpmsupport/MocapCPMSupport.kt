package xyz.bluspring.mocapcpmsupport

import com.mt1006.mocap.mocap.actions.Action
import com.mt1006.mocap.mocap.actions.ComparableAction
import com.mt1006.mocap.mocap.files.RecordingFiles
import net.fabricmc.api.ModInitializer
import net.minecraft.world.entity.Entity
import org.slf4j.LoggerFactory
import xyz.bluspring.mocapcpmsupport.actions.SetCPMDataAction
import xyz.bluspring.mocapcpmsupport.mixin.ActionTypeAccessor
import java.util.function.Function

class MocapCPMSupport : ModInitializer {
    override fun onInitialize() {
    }

    companion object {
        val logger = LoggerFactory.getLogger(MocapCPMSupport::class.java)
        val SET_CPM_DATA_ACTION = createAction("SET_CPM_DATA", ::SetCPMDataAction, ::SetCPMDataAction)

        private fun createAction(name: String, fromReader: Function<RecordingFiles.Reader, Action>, fromEntity: Function<Entity, ComparableAction>): Action.Type {
            return EnumUtils.addEnumToClass(Action.Type::class.java, ActionTypeAccessor.getValues(), name, { size -> ActionTypeAccessor.createType(name, size, size, fromReader, fromEntity) }, { values -> ActionTypeAccessor.setValues(values.toTypedArray()) })
        }
    }
}
