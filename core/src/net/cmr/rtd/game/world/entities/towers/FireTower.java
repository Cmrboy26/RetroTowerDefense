package net.cmr.rtd.game.world.entities.towers;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.rtd.game.world.Entity;
import net.cmr.rtd.game.world.UpdateData;
import net.cmr.rtd.game.world.entities.EnemyEntity;
import net.cmr.rtd.game.world.entities.Projectile;
import net.cmr.rtd.game.world.entities.Projectile.ProjectileBuilder;
import net.cmr.rtd.game.world.entities.TowerEntity;
import net.cmr.rtd.game.world.entities.effects.Effect;
import net.cmr.rtd.game.world.entities.effects.FireEffect;
import net.cmr.rtd.game.world.particles.SpreadEmitterEffect;
import net.cmr.rtd.game.world.tile.Tile;
import net.cmr.util.Audio;
import net.cmr.util.Audio.GameSFX;
import net.cmr.util.Sprites;
import net.cmr.util.Sprites.AnimationType;

public class FireTower extends TowerEntity {

    float fireballDamage = 2;
    float range = 2;
    float targetDPS = 1;

    boolean attacking = false;
    float animationDelta = 0;
    float fireballDelta = 0;

    public FireTower() {
        super(GameType.FIRE_TOWER, 0);
    }

    public FireTower(int team) {
        super(GameType.FIRE_TOWER, team);
    }

    @Override
    public boolean attack(UpdateData data) {
        super.attack(data);
        ArrayList<EnemyEntity> entitiesInRange = getEnemiesInRange(range, data, getPreferedSortType());
        boolean launchedFireball = false;
        if (fireballDelta < Math.max(2f, (6f - ((getLevel() - 1) * .5f)))) {
            launchedFireball = true;
        } 

        boolean actionOccured = false;
        for (Entity entity : entitiesInRange) {
            if (entity instanceof EnemyEntity) {
                EnemyEntity enemy = (EnemyEntity) entity;
                int targetLevel = (int) Math.floor(targetDPS + (getLevel() - 1) * .5f);
                new FireEffect(data, enemy.getEffects(), getLevel(), targetLevel);
                actionOccured = true;
                if (!launchedFireball && data.isServer()) {
                    ProjectileBuilder builder = new ProjectileBuilder()
                        .setEntity(enemy)
                        .setAnimation(AnimationType.FIRE)
                        .setPosition(new Vector2(getPosition()))
                        .setScale(3f)
                        .setDamage(getLevel() * getLevel())
                        .setTimeToReachTarget(1)
                        .setAOE(1.15f)
                        .setPrecision(1)
                        .setOnHitSound(GameSFX.FIREBALL_HIT)
                        .setOnLaunchSound(GameSFX.FIREBALL_LAUNCH);
                    Projectile fireball = builder.build();
                    fireball.setParticleOnHit(SpreadEmitterEffect.factory()
                        .setParticle(AnimationType.FIRE)
                        .setDuration(1)
                        .setEmissionRate(20)
                        .setScale(.45f)
                        .setParticleLife(.5f)
                        .setFollowEntity(true)
                        .setAnimationSpeed(2f)
                        .setAreaSize(1.15f)
                        .create());
                    if (fireball.getVelocity().len() > (range + 1)*Tile.SIZE) {
                        // dont launch it
                        continue;
                    }
                    //data.getWorld().addEntity(fireball);
                    Projectile.launchProjectile(data, fireball);
                    launchedFireball = true;
                    fireballDelta = 0;
                }
            }
        }
        // Fireball can only be fired after a certain amount of time where enemies have been in range
        if (actionOccured) {
            fireballDelta += getAttackSpeed();
        }
        return actionOccured;
    }

    @Override
    protected void serializeTower(DataBuffer buffer) throws IOException {
        buffer.writeFloat(fireballDelta);
    }

    @Override
    protected void deserializeTower(TowerEntity entity, DataInputStream input) throws IOException {
        FireTower tower = (FireTower) entity;
        tower.fireballDelta = input.readFloat();
    }

    @Override
    public float getAttackSpeed() {
        return .1f/getLubricantSpeedBoost();
    }

    @Override
    public void render(UpdateData data, Batch batch, float delta) {
        preRender(data, batch, delta);

        animationDelta += delta;
        /*if (attacking) {
            animationDelta += delta;
        } else {
            animationDelta = 0;
        }*/

        Color color = new Color(Color.RED);
        color.a = batch.getColor().a;
        batch.setColor(color);
        TextureRegion sprite = Sprites.animation(AnimationType.FIRE_TOWER, animationDelta); //Sprites.sprite(Sprites.SpriteType.CMRBOY26)
        batch.draw(sprite, getX() - Tile.SIZE / 2, getY() - Tile.SIZE / 2, Tile.SIZE, Tile.SIZE * 1.5f);
        batch.setColor(Color.WHITE);

        postRender(data, batch, delta);
        super.render(data, batch, delta);
    }

    @Override
    public float getDisplayRange() {
        return (range + getLevel() / 6f)*getScopeRangeBoost();
    }

    @Override
    public float getDisplayDamage() {
        return (targetDPS + (getLevel() - 1) * .5f)*getScrapMetalDamageBoost();
    }

    @Override
    public String getDescription() {
        return "This tower sets enemies ablaze, dealing\ndamage over time. It also has a chance to launch\na fireball at enemies in range.";
    }
    
}
