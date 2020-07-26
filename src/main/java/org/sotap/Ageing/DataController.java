package org.sotap.Ageing;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class DataController {
    public Ageing plug;

    public DataController(Ageing plug) {
        this.plug = plug;
    }

    // 获取当前年龄所在的区间（从 0 开始）
    // 注：包含端点。例如当 rangeLength 为 6 时，0 ~ 5 属于第 0 区间，6 ~ 11 属于第 1 区间，以此类推
    private Integer getAgeRangeAt(FileConfiguration config, Integer age) {
        Integer rangeLength = config.getInt("growth_range_length");
        if (age < rangeLength && age >= 0) {
            return 0;
        }
        return age % rangeLength != 0 ? (age - (age % rangeLength)) / rangeLength
                : age / rangeLength;
    }

    // 获取当前区间内长一岁所需要的经验数
    private Integer getGrowthCostAtRange(FileConfiguration config, Integer range) {
        Integer stepValue = config.getInt("growth_step_value");
        return config.getInt("growth_base_value") + (stepValue * range);
    }

    // 获取当前年龄所在的区间内长一岁所需要的经验数
    public Integer getGrowthCost(Integer age) {
        FileConfiguration config = this.plug.getConfig();
        Integer stepValue = config.getInt("growth_step_value");
        return config.getInt("growth_base_value") + (stepValue * this.getAgeRangeAt(config, age));
    }

    // 获取长至指定年龄所需要的总经验数
    public Integer getGrowthCostTo(Integer age) {
        FileConfiguration config = this.plug.getConfig();
        Integer rangeLength = config.getInt("growth_range_length");
        Integer rangeAt = this.getAgeRangeAt(config, age);
        Integer result = 0;
        if (rangeAt == 0) {
            result = age * config.getInt("growth_base_value");
        } else {
            for (int i = 0; i < rangeAt; i += 1) {
                result += rangeLength * this.getGrowthCostAtRange(config, i);
            }
            result += (age - (rangeLength * rangeAt - 1)) * this.getGrowthCostAtRange(config, rangeAt);
            /**
             * 从第 0 区间向第 1 区间跳跃时，中间会多出一个 b，因此在这里减去来平衡
             * 例如，若 r=5, b=200, s=150，当 n=4 时 exp=800
             * 当 n=5 时，可知 i=1，则 exp=b*r+i(b+s)=200*5+200+150=1350
             * 那么 n=5 和 n=4 的 exp 值就相差了 550 多出了 200，也就是多出了一个 b 的值
             * 这当然能在上面的 for 循环中改掉，仅需对 rangeLength 做手脚，
             * 但很明显会用到 if 且逻辑并不简单，因而会降低效率，因此不如直接在这里减去 b
             * 为了避免后续理解上的问题，故写下这些解释
             */
            result -= config.getInt("growth_base_value");
        }
        return result;
    }

    public void updateAge(String playername, Integer newAge) {
        String uuid = Bukkit.getPlayer(playername).getUniqueId().toString();
        this.plug.ageData.set(uuid + ".age", newAge);
        this.plug.ageData.set(uuid + ".exp", this.getGrowthCostTo(newAge));
    }

    public void updateExperience(String playername, Integer newExperience) {
        String uuid = Bukkit.getPlayer(playername).getUniqueId().toString();
        Integer oldAge = this.plug.ageData.getInt(uuid + ".age");
        Integer newAge = oldAge;
        Integer oldExperience = this.plug.ageData.getInt(uuid + ".exp");
        Integer addExperience = Math.abs(newExperience - oldExperience);
        Integer ageGrowthExp = getGrowthCostTo(oldAge + 1) - oldExperience;
        Integer ageDecayExp = oldExperience - getGrowthCostTo(oldAge);

        if (newExperience == oldExperience) {
            return;
        } else if (newExperience > oldExperience) {
            while (addExperience >= ageGrowthExp) {
                newAge++;
                addExperience -= ageGrowthExp;
                oldExperience += ageGrowthExp;
                ageGrowthExp = getGrowthCostTo(newAge + 1) - oldExperience;
            }
        } else if (newExperience < oldExperience) {
            while (addExperience >= ageDecayExp) {
                newAge--;
                addExperience -= ageDecayExp;
                oldExperience -= ageDecayExp;
                ageDecayExp = oldExperience - getGrowthCostTo(newAge);
            }
        }

        this.plug.ageData.set(uuid + ".age", newAge);
        this.plug.ageData.set(uuid + ".exp", newExperience);
    }
}
