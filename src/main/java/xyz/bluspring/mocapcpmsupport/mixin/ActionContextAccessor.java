package xyz.bluspring.mocapcpmsupport.mixin;

import net.minecraft.server.players.PlayerList;
import net.mt1006.mocap.mocap.playing.playback.ActionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ActionContext.class)
public interface ActionContextAccessor {
    @Accessor
    PlayerList getPacketTargets();
}
