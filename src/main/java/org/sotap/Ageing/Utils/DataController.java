package org.sotap.Ageing.Utils;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.sotap.Ageing.Ageing;

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
        return config.getInt("growth_base_value") + (config.getInt("growth_step_value") * range);
    }

    // 获取长至指定年龄所需要的总经验数
    public Integer getGrowthCostTo(FileConfiguration config, Integer age) {
        Integer rangeLength = config.getInt("growth_range_length");
        Integer rangeAt = getAgeRangeAt(config, age);
        Integer result = 0;
        if (rangeAt == 0) {
            result = age * config.getInt("growth_base_value");
        } else {
            for (int i = 0; i < rangeAt; i += 1) {
                result += rangeLength * getGrowthCostAtRange(config, i);
            }
            result += (age - (rangeLength * rangeAt - 1)) * getGrowthCostAtRange(config, rangeAt);
            /**
             * 从第 0 区间向第 1 区间跳跃时，中间会多出一个 b，因此在这里减去来平衡 例如，若 r=5, b=200, s=150，当 n=4 时 exp=800 当 n=5
             * 时，可知 i=1，则 exp=b*r+i(b+s)=200*5+200+150=1350 那么 n=5 和 n=4 的 exp 值就相差了 550 多出了
             * 200，也就是多出了一个 b 的值 这当然能在上面的 for 循环中改掉，仅需对 rangeLength 做手脚， 但很明显会用到 if
             * 且逻辑并不简单，因而会降低效率，因此不如直接在这里减去 b 为了避免后续理解上的问题，故写下这些解释
             */
            result -= config.getInt("growth_base_value");
        }
        return result;
    }

    private Boolean checkAll(String playername, Integer newValue, FileConfiguration config) {
        Integer baseValue = config.getInt("growth_base_value");
        Integer stepValue = config.getInt("growth_step_value");
        Integer rangeLength = config.getInt("growth_range_length");
        try {
            @SuppressWarnings("unused")
            String uuid = Bukkit.getPlayer(playername).getUniqueId().toString();
        } catch (Exception e) {
            return false;
        }
        return baseValue > 0 && stepValue > 0 && rangeLength > 0 && newValue >= 0;
    }

    // 获取达到该年龄后可获得的奖励
    // 该函数只能在自然成长过程中生效，因为它只会判断当前年龄是否有存在的奖励设定
    public List<String> getAgeAwardsAt(FileConfiguration config, Integer age) {
        ConfigurationSection awards = config.getConfigurationSection("age_commands");
        if (awards.contains(age.toString())) {
            return awards.getStringList(age.toString());
        }
        return null;
    }

    public Boolean updateAge(String playername, Integer newAge) {
        FileConfiguration config = plug.getConfig();
        if (checkAll(playername, newAge, config)) {
            Integer maxAge = config.getInt("max_age");
            if (!(newAge <= maxAge)) {
                return false;
            }
        } else {
            return false;
        }
        String uuid = Bukkit.getPlayer(playername).getUniqueId().toString();
        plug.ageData.set(uuid + ".age", newAge);
        plug.ageData.set(uuid + ".exp", getGrowthCostTo(config, newAge));
        plug.saveData();
        return true;
    }

    public Boolean updateExperience(String playername, Integer newExperience) {
        FileConfiguration config = plug.getConfig();
        if (checkAll(playername, newExperience, config)) {
            Integer maxExp = getGrowthCostTo(config, config.getInt("max_age"));
            if (!(newExperience <= maxExp)) {
                return false;
            }
        } else {
            return false;
        }
        String uuid = Bukkit.getPlayer(playername).getUniqueId().toString();
        Integer oldAge = plug.ageData.getInt(uuid + ".age");
        Integer newAge = oldAge;
        Integer oldExperience = plug.ageData.getInt(uuid + ".exp");
        Integer addExperience = Math.abs(newExperience - oldExperience);
        Integer ageGrowthExp = getGrowthCostTo(config, oldAge + 1) - oldExperience;
        Integer ageDecayExp = oldExperience - getGrowthCostTo(config, oldAge);

        if (newExperience > 0) {
            if (newExperience > oldExperience) {
                while (addExperience >= ageGrowthExp) {
                    newAge++;
                    addExperience -= ageGrowthExp;
                    oldExperience += ageGrowthExp;
                    ageGrowthExp = getGrowthCostTo(config, newAge + 1) - oldExperience;
                }
            } else if (newExperience < oldExperience) {
                while (addExperience >= ageDecayExp) {
                    newAge--;
                    addExperience -= ageDecayExp;
                    oldExperience -= ageDecayExp;
                    ageDecayExp = oldExperience - getGrowthCostTo(config, newAge);
                }
            }
        } else {
            newAge = 0;
        }
        plug.ageData.set(uuid + ".age", newAge);
        plug.ageData.set(uuid + ".exp", newExperience);
        if (newAge > oldAge) {
            List<String> award = getAgeAwardsAt(config, newAge);
            if (award != null) {
                Functions.dispatchCommands(award, playername, uuid);
            }
        }
        plug.saveData();
        return true;
    }
}
