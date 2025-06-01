package xyz.bluspring.mocapcpmsupport.mixin;

import net.mt1006.mocap.mocap.actions.Action;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Action.Type.class, remap = false)
public interface ActionTypeAccessor {
    @Invoker("<init>")
    static Action.Type createType(String name, int enumId, int id, Action.FromReaderOnly fromReader, Action.FromEntity fromEntity) {
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
