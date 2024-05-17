package net.cmr.rtd.game.world.entities;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.rtd.game.world.Entity;
import net.cmr.rtd.game.world.GameObject;
import net.cmr.rtd.game.world.UpdateData;
import net.cmr.rtd.game.world.tile.Tile;
import net.cmr.util.Audio;
import net.cmr.util.Audio.GameSFX;
import net.cmr.util.Sprites;
import net.cmr.util.Sprites.AnimationType;
import net.cmr.util.Sprites.SpriteType;

public abstract class TowerEntity extends Entity {

    public static boolean displayRange = false;
    public static TowerEntity displayRangeTower;

    float attackDelta = 0;
    int team;

    float levelUpDelta = -1; // -1 means not currently upgrading, 0 and lower means done upgrading, positive means time left 
    float upgradeTime = 1;
    float placementDelta = 0;
    int level = 1;
    // TODO: Add other tower upgrades

    public TowerEntity(GameType type, int team) {
        super(type);
        this.team = team;
        this.level = 1;
        this.levelUpDelta = -1;
        this.upgradeTime = 1;
    }
    
    public void update(float delta, UpdateData data) {
        if (levelUpDelta != -1) {
            levelUpDelta -= delta;
            if (levelUpDelta <= 0) {
                levelUpDelta = -1;
                upgradeTime = 1;
                level++;
            }
        } else if (placementDelta != -1) {
            placementDelta -= delta;
            if (placementDelta <= 0) {
                placementDelta = -1;
            }
        } else {
            // CANNOT attack while upgrading
            attackDelta += delta;
            if (attackDelta >= getAttackSpeed()) {
                boolean attacked = attack(data);
                if (attacked) {
                    attackDelta = 0;
                }
            }
        }
    }

    @Override
    public float getRenderOffset() {
        return - Tile.SIZE / 2;
    }

    @Override
    protected void serializeEntity(DataBuffer buffer) throws IOException {
        buffer.writeFloat(attackDelta);
        buffer.writeInt(team);
        buffer.writeInt(level);
        buffer.writeFloat(levelUpDelta);
        buffer.writeFloat(upgradeTime);
        buffer.writeFloat(placementDelta);
        serializeTower(buffer);
    }

    @Override
    protected void deserializeEntity(GameObject object, DataInputStream input) throws IOException {
        TowerEntity tower = (TowerEntity) object;
        tower.attackDelta = input.readFloat();
        tower.team = input.readInt();
        tower.level = input.readInt();
        tower.levelUpDelta = input.readFloat();
        tower.upgradeTime = input.readFloat();
        tower.placementDelta = input.readFloat();
        deserializeTower(tower, input);
    }

    public void preRender(Batch batch, float delta) {
        if (getRemainingUpgradeTime() != -1f) {
            batch.draw(Sprites.sprite(SpriteType.UPGRADE_BACK), getX() - Tile.SIZE / 2, getY() - Tile.SIZE / 2, Tile.SIZE, Tile.SIZE * 2);
        }
    }

    float lastProgress = -1;
    public void postRender(Batch batch, float delta) {
        float progress = 1 - getRemainingUpgradeTime() / getUpgradeTime();
        if (getRemainingUpgradeTime() != -1f || placementDelta != -1f) {
            batch.draw(Sprites.sprite(SpriteType.UPGRADE_FRONT), getX() - Tile.SIZE / 2, getY() - Tile.SIZE / 2, Tile.SIZE, Tile.SIZE * 2);
        }
        if (lastProgress != -1 && getRemainingUpgradeTime() == -1) {
            // Play sound
            Audio.getInstance().playSFX(GameSFX.UPGRADE_COMPLETE, 1);
        }
        lastProgress = getRemainingUpgradeTime() ;
        // draw a circle of radius getDisplayRange() centered at getPosition()
        if (displayRange && (displayRangeTower == null || displayRangeTower == this)) {
            SpriteType type = SpriteType.AREA;
            // set the color to a transparent yellow
            batch.setColor(new Color(Color.YELLOW).sub(0,0,0,.8f));
            batch.draw(Sprites.sprite(type), getPosition().x - getDisplayRange() * Tile.SIZE, getPosition().y - getDisplayRange() * Tile.SIZE, getDisplayRange() * 2 * Tile.SIZE, getDisplayRange() * 2 * Tile.SIZE);
            batch.setColor(Color.WHITE);
        }
    }

    protected abstract void serializeTower(DataBuffer buffer) throws IOException;
    protected abstract void deserializeTower(TowerEntity entity, DataInputStream input) throws IOException;

    public enum SortType {
        /**
         * Returns a list in any order (fastest).
         */
        ANY {
            @Override
            public void sort(ArrayList<EnemyEntity> entities, TowerEntity tower, UpdateData data) {
                // Do nothing
            }
        },
        /**
         * Highest health to lowest health
         */
        HEALTH {
            @Override
            public void sort(ArrayList<EnemyEntity> entities, TowerEntity tower, UpdateData data) {
                entities.sort((a, b) -> (int) (b.getHealth() - a.getHealth()));
            }
        },
        /**
         * Lowest health to highest health
         */
        HEALTH_REVERSE {
            @Override
            public void sort(ArrayList<EnemyEntity> entities, TowerEntity tower, UpdateData data) {
                entities.sort((a, b) -> (int) (a.getHealth() - b.getHealth()));
            }
        },
        /**
         * Closest to farthest to the tower
         */
        TOWER_DISTANCE {
            @Override
            public void sort(ArrayList<EnemyEntity> entities, TowerEntity tower, UpdateData data) {
                entities.sort((a, b) -> (int) (a.getPosition().dst(tower.getPosition()) - b.getPosition().dst(tower.getPosition())));
            }
        },
        /**
         * Farthest to closest to the tower
         */
        TOWER_DISTANCE_REVERSE {
            @Override
            public void sort(ArrayList<EnemyEntity> entities, TowerEntity tower, UpdateData data) {
                entities.sort((a, b) -> (int) (b.getPosition().dst(tower.getPosition()) - a.getPosition().dst(tower.getPosition())));
            }
        },
        /**
         * Closest to the end of the path/structure
         */
        @Deprecated
        STRUCTURE_DISTANCE {
            @Override
            public void sort(ArrayList<EnemyEntity> entities, TowerEntity tower, UpdateData data) {
                // TODO: Implement
                // Cannot be implemented with current without syncing team point positions to the client
            }
        },
        /**
         * Farthest to the end of the path/structure
         */
        @Deprecated
        STRUCTURE_DISTANCE_REVERSE {
            @Override
            public void sort(ArrayList<EnemyEntity> entities, TowerEntity tower, UpdateData data) {
                // TODO: Implement
                // Cannot be implemented with current without syncing team point positions to the client
            }
        };

        public abstract void sort(ArrayList<EnemyEntity> entities, TowerEntity tower, UpdateData data);
    }

    public ArrayList<EnemyEntity> getEnemiesInRange(double tileRadius, UpdateData data) {
        return getEnemiesInRange(tileRadius, data, SortType.ANY);
    }

    public ArrayList<EnemyEntity> getEnemiesInRange(double tileRadius, UpdateData data, SortType sortType) {
        Objects.requireNonNull(sortType, "sortType cannot be null");
        
        double worldDistance = tileRadius = Tile.SIZE * tileRadius;
        ArrayList<EnemyEntity> entitiesInRange = new ArrayList<EnemyEntity>();
        double threshold = Tile.SIZE / 16;
        for (Entity entity : data.getWorld().getEntities()) {
            if (entity instanceof EnemyEntity) {
                EnemyEntity enemy = (EnemyEntity) entity;
                if (enemy.getTeam() != team) continue;
                //double distance = Math.abs(Math.max(entity.getX() - enemy.getX(), entity.getY() - enemy.getY()));
                double distance = entity.getPosition().dst(getPosition());
                if (distance <= worldDistance + threshold) {
                    entitiesInRange.add(enemy);
                }
            }
        }
        sortType.sort(entitiesInRange, this, data);
        return entitiesInRange;
    }

    /**
     * @return seconds between each {@link #attack(UpdateData)} call
     */
    public float getAttackSpeed() {
        return 1;
    }

    public abstract float getDisplayDamage();
    public abstract float getDisplayRange();
    public abstract String getDescription();

    public SortType getPreferedSortType() {
        return SortType.HEALTH;
    }

    public float getUpgradeTime() {
        return upgradeTime;
    }

    public float getRemainingUpgradeTime() {
        return levelUpDelta;
    }

    public int getLevel() {
        return level;
    }

    /**
     * @param upgradeTime time in seconds to upgrade the tower
     * @return if the action was successful
     */
    public boolean levelUp(float upgradeTime) {
        if (levelUpDelta != -1) {
            return false;
        }
        this.upgradeTime = upgradeTime;
        levelUpDelta = upgradeTime;
        return true;
    }

    /**
     * Sets the time to initially build the tower (when placed down on the map).
     * @param delta
     */
    public void setBuildDelta(float delta) {
        placementDelta = delta;
    }

    public float getRemainingBuildTime() {
        return placementDelta;
    }

    /**
     * @return whether the tower has finished the initial build (when placed down on the map)
     */
    public boolean isBeingBuilt() {
        return placementDelta > 0;
    }

    /**
     * @see #getAttackSpeed()
     * @see #getEnemiesInRange(double, UpdateData)
     * @param data world data
     * @return if any action occured (this will reset the attack delta)
     */
    public boolean attack(UpdateData data) { return false; }

    public int getTeam() { return team; }

    public static void displayRangeAll() {
        displayRange = true;
        displayRangeTower = null;
    }
    public static void displayRange(TowerEntity target) {
        displayRange = true;
        displayRangeTower = target;
    }
    public static void hideRange() {
        displayRange = false;
        displayRangeTower = null;
    }

    // Upgrade Helper Methods

    /**
     * Every x levels, increase by y
     */
    public float calculateIncrementedValue(int levelsPerIncrement, float incrementAmount) {
        return calculateIncrementedValue(levelsPerIncrement, incrementAmount, 0);
    }

    /**
     * Every x levels, increase by y
     */
    public float calculateIncrementedValue(int levelsPerIncrement, float incrementAmount, float initial) {
        return (float) (initial + Math.floor(getLevel() / levelsPerIncrement) * incrementAmount);
    }

    public AnimationType getTowerAnimationLevelDependent(AnimationType[] levels, int[] levelThresholds) {
        for (int i = 0; i < levelThresholds.length; i++) {
            if (getLevel() <= levelThresholds[i]) {
                return levels[i];
            }
        }
        return levels[levels.length - 1];
    }

}
