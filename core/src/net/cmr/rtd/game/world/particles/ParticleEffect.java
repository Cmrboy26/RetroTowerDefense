package net.cmr.rtd.game.world.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Null;

import net.cmr.rtd.game.world.Entity;
import net.cmr.rtd.screen.GameScreen;

/**
 * A client-side particle effect.
 */
public abstract class ParticleEffect {
    
    @Null Entity attachedEntity;
    GameScreen screen;
    Vector2 position = new Vector2();
    float elapsedTime = 0;
    boolean particleFinished = false;

    public ParticleEffect(@Null Entity attachedEntity) {
        this.attachedEntity = attachedEntity;
    }

    public void update(GameScreen screen, float delta) {
        if (this.screen == null) {
            this.screen = screen;
        }
        if (attachedEntity != null) {
            if (screen.getEntity(attachedEntity.getID()) == null) {
                remove();
                return;
            }
            position.set(attachedEntity.getPosition());
        }
        elapsedTime += delta;
        if (getDuration() != Float.MAX_VALUE && elapsedTime >= getDuration()) {
            remove();
            return;
        }
        updateParticle(screen, delta);
    }

    public abstract void updateParticle(GameScreen screen, float delta);

    public void render(Batch batch) {
        
    }

    public void remove() {
        particleFinished = true;
    }
    
    /**
     * Returns the duration of the effect.
     * If the duration is equal to {@link Float#MAX_VALUE}, the effect will only be removed when the entity is removed.
     * @return
     */
    public float getDuration() {
        return Float.MAX_VALUE;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public Vector2 getPosition() {
        return position;
    }
    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public float getX() {
        return position.x;
    }
    public float getY() {
        return position.y;
    }

    public boolean isParticleFinished() {
        return particleFinished;
    }

}
