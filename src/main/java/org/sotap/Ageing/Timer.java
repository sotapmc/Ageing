package org.sotap.Ageing;

import java.util.Date;
import java.util.UUID;

public class Timer {
    public Ageing plug;

    public Timer(Ageing plug) {
        this.plug = plug;
    }

    public void startTimerFor(UUID uuid) {
        Date date = new Date();
        long time = date.getTime();
        this.plug.timerData.set(uuid.toString() + ".lastJoinTime", time);
    }

    public void stopTimerFor(UUID uuid) {
        Date date = new Date();
        long time = date.getTime();
        this.plug.timerData.set(uuid.toString() + ".lastQuitTime", time);
    }

    public long getTimeDeltaOf(UUID uuid) {
        long lastJoin = this.plug.timerData.getLong(uuid.toString() + ".lastJoinTime");
        long lastQuit = this.plug.timerData.getLong(uuid.toString() + ".lastQuitTime");
        long delta = lastQuit - lastJoin;
        return delta;
    }
}