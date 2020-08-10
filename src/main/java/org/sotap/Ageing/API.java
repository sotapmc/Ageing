package org.sotap.Ageing;

import javax.validation.constraints.NotNull;
import org.sotap.Ageing.Exception.AgeingAPIException;
import org.sotap.Ageing.Utils.G;

public final class API {
    public Ageing plug;

    public API(Ageing plug) {
        this.plug = plug;
    }

    /**
     * 更新玩家的经验值
     * @param exp 经验值（非 0 整数）
     * @param playername 玩家名
     * @return boolean
     * @throws AgeingAPIException
     */
    public boolean updateExperience(@NotNull Integer exp, @NotNull String playername) throws AgeingAPIException {
        // 无需验证玩家是否在线：当该方法被执行时，玩家必定在线且存在
        Integer oldExp = G.getDataOf(plug, playername).getInt("exp");
        if (exp > 0 || exp < 0) {
            plug.controller.updateExperience(playername, oldExp + exp);
            return true;
        } else {
            throw new AgeingAPIException("Invalid experience value: avoided Zero");
        }
    }

    /**
     * 获取玩家的年龄 若玩家不在线或不存在将返回 -1
     * @param playername 玩家名
     * @return int
     */
    public Integer getAgeOf(@NotNull String playername) {
        Integer age = -1;
        try {
            age = G.getDataOf(plug, playername).getInt("age");
        } catch (Exception e) {
            // do nothing
        }
        return age;
    }

    /**
     * 获取玩家的经验值 若玩家不在线或不存在将返回 -1
     * @param playername 玩家名
     * @return int
     */
    public Integer getExpOf(@NotNull String playername) {
        Integer exp = -1;
        try {
            exp = G.getDataOf(plug, playername).getInt("exp");
        } catch (Exception e) {
            // do nothing
        }
        return exp;
    }
}
