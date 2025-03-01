package xyz.bluspring.mocapcpmsupport.mixin;

import com.tom.cpm.shared.config.PlayerData;
import com.tom.cpm.shared.network.NetH;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "com.mt1006.mocap.utils.FakePlayer$FakePlayerNetHandler")
public class FakePlayerNetHandlerMixin implements NetH.ServerNetH {
    @Unique private PlayerData mocapCpm$cpmPlayerData;

    @Override
    public PlayerData cpm$getEncodedModelData() {
        return mocapCpm$cpmPlayerData;
    }

    @Override
    public void cpm$setEncodedModelData(PlayerData playerData) {
        mocapCpm$cpmPlayerData = playerData;
    }

    @Override
    public boolean cpm$hasMod() {
        return false;
    }

    @Override
    public void cpm$setHasMod(boolean b) {

    }
}
