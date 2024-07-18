package net.cmr.rtd.game.achievements;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.cmr.rtd.ProjectTetraTD;
import net.cmr.util.Log;

public class AchievementManager {
    
    private static AchievementManager instance;
    private static Object lock = new Object();

    static {
        // Load achievements
        synchronized (lock) {
            instance = new AchievementManager();
            instance.loadAchievements();
        }
    }

    public static AchievementManager getInstance() {
        synchronized (lock) {
            return instance;
        }
    }

    final ProjectTetraTD game;
    private HashMap<Class<? extends Achievement<?>>, Achievement<?>> achievements;
    private boolean dirty = false; // Whether achievements need to be saved to file

    private AchievementManager() {
        game = ProjectTetraTD.getInstance(ProjectTetraTD.class);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void loadAchievements() {
        // Load achievements from file
        achievements = new HashMap<>();
        // Fill achievements with default state of all achievements
        for (Class<? extends Achievement<?>> clazz : Achievement.getAchievementRegistry().values()) {
            Achievement<?> achievement = Achievement.createAchievementInstance(clazz);
            achievements.put(clazz, achievement);
        }

        Map<String, Object> achievementMap = ProjectTetraTD.readUserData("achievements", Map.class, new HashMap<>());
        if (achievementMap == null) {
            Log.info("No achievement data found.");
            saveAchievements();
            return;
        }
        for (Map.Entry<String, Object> entry : achievementMap.entrySet()) {
            String id = entry.getKey();
            Object value = entry.getValue();
            Class<? extends Achievement<?>> clazz = Achievement.getAchievement(id);
            if (clazz != null) {
                Achievement achievement = Achievement.createAchievementInstance(clazz);
                if (achievement.getValueType().isInstance(value)) {
                    achievement.setValue(value);
                    achievements.put((Class<? extends Achievement<?>>) achievement.getClass(), achievement);
                }
            }
        }

        Log.info("Loaded " + achievements.size() + " achievements");
        for (Entry<Class<? extends Achievement<?>>, Achievement<?>> entry : achievements.entrySet()) {
            Log.info(entry.getValue().getID() + ": " + entry.getValue().getValue());
        }
    }

    public static void save() {
        getInstance().saveAchievements();
    }

    private void saveAchievements() {
        // Save achievements to file
        if (dirty) {
            Map<String, Object> achievementsMap = new HashMap<>();
            for (Achievement<?> achievement : achievements.values()) {
                achievementsMap.put(achievement.getID(), achievement.getValue());
            }
            ProjectTetraTD.writeUserData("achievements", achievementsMap);
            dirty = false;
        }
    }

    public Map<Class<? extends Achievement<?>>, Achievement<?>> getAchievements() {
        return achievements;
    }

}
