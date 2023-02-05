package org.valkyrienskies.tournament.mixinduck;

import org.valkyrienskies.tournament.item.GrabGun;

public interface MixinPlayerDuck {

    void cw_setGravitronState(GrabGun.GravitronState state);

    GrabGun.GravitronState cw_getGravitronState();

}
