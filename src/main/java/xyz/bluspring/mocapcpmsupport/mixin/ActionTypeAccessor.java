package xyz.bluspring.mocapcpmsupport.mixin;

import com.mt1006.mocap.mocap.actions.Action;
import com.mt1006.mocap.mocap.actions.ComparableAction;
import com.mt1006.mocap.mocap.files.RecordingFiles;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Function;

@Mixin(value = Action.Type.class, remap = false)
public interface ActionTypeAccessor {
    @Invoker("<init>")
    static Action.Type createType(String name, int enumId, int id, Function<RecordingFiles.Reader, Action> fromReader, Function<Entity, ComparableAction> fromEntity) {
        throw new IllegalStateException();
    }

    @Accessor("$VALUES")
    static Action.Type[] getValues() {
        throw new IllegalStateException();
    }

    @Accessor("$VALUES")
    @Mutable
    static void setValues(Action.Type[] values) {
        throw new IllegalStateException();
    }
}
