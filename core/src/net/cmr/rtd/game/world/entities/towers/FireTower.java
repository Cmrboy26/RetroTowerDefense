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
import net.cmr.rtd.game.world.entities.TowerEntity;
import net.cmr.rtd.game.world.entities.effects.FireEffect;
import net.cmr.rtd.game.world.particles.SpreadEmitterEffect;
import net.cmr.rtd.game.world.tile.Tile;
import net.cmr.util.Sprites;
import net.cmr.util.Sprites.AnimationType;
import net.cmr.util.Sprites.SpriteType;

public class FireTower extends TowerEntity {

    float fireballDamage = 3;
    float range = 2;
    float targetDPS = 3;

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
    public void attack(UpdateData data) {
        super.attack(data);
        ArrayList<EnemyEntity> entitiesInRange = getEnemiesInRange(range, data, getPreferedSortType());
        fireballDelta += getAttackSpeed();
        boolean launchedFireball = false;
        if (fireballDelta < 5) {
            launchedFireball = true;
        } 

        for (Entity entity : entitiesInRange) {
            if (entity instanceof EnemyEntity) {
                //System.out.println("ATTACKING ENTITY "+data.isServer());
                EnemyEntity enemy = (EnemyEntity) entity;
                new FireEffect(enemy.getEffects(), 1, (int) Math.round(targetDPS));
                if (!launchedFireball) {
                    Projectile fireball = new Projectile(enemy, new Vector2(getPosition()), 3, 1, 1, 1);
                    fireball.setParticleOnHit(SpreadEmitterEffect.factory()
                        .setParticle(AnimationType.FIRE)
                        .setDuration(1)
                        .setEmissionRate(20)
                        .setScale(.45f)
                        .setParticleLife(.5f)
                        .setFollowEntity(true)
                        .setAnimationSpeed(2f)
                        .create());
                    if (fireball.getVelocity().len() > (range + 1)*Tile.SIZE) {
                        // dont launch it
                        continue;
                    }
                    //System.out.println("LAUNCHED FIREBALL");
                    data.getWorld().addEntity(fireball);
                    launchedFireball = true;
                    fireballDelta = 0;
                }
            }
        }
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
        return .25f;
    }

    @Override
    public void render(Batch batch, float delta) {
        if (attacking) {
            animationDelta += delta;
        } else {
            animationDelta = 0;
        }

        Color color = new Color(Color.RED);
        color.a = batch.getColor().a;
        batch.setColor(color);
        TextureRegion sprite = Sprites.animation(AnimationType.TESLA_TOWER, animationDelta); //Sprites.sprite(Sprites.SpriteType.CMRBOY26)
        batch.draw(sprite, getX() - Tile.SIZE / 2, getY() - Tile.SIZE / 2, Tile.SIZE, Tile.SIZE);
        batch.setColor(Color.WHITE);
        super.render(batch, delta);
    }

    @Override
    public float getDisplayRange() {
        return range;
    }
    
    
}
