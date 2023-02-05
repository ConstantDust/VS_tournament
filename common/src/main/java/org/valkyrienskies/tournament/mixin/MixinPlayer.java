package org.valkyrienskies.tournament.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.tournament.mixinduck.MixinPlayerDuck;
import org.valkyrienskies.tournament.item.GrabGun;

@Mixin(Player.class)
public class MixinPlayer implements MixinPlayerDuck {

    @Unique
    private GrabGun.GravitronState state;

    @Override
    public void cw_setGravitronState(GrabGun.GravitronState s) {
        this.state = s;
    }

    @Override
    public GrabGun.GravitronState cw_getGravitronState() {
        return state;
    }
}
