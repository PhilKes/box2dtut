package blog.gamedevelopment.box2dtutorial.entity.systems;

import blog.gamedevelopment.box2dtutorial.LevelFactory;
import blog.gamedevelopment.box2dtutorial.ParticleEffectManager;
import blog.gamedevelopment.box2dtutorial.entity.components.B2dBodyComponent;
import blog.gamedevelopment.box2dtutorial.entity.components.BasketComponent;
import blog.gamedevelopment.box2dtutorial.entity.components.StateComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class BasketSystem extends IteratingSystem {
    private final LevelFactory lvlfactory;

    public BasketSystem(LevelFactory lvlFactory) {
        super(Family.all(BasketComponent.class).get());
        this.lvlfactory=lvlFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent stateC=entity.getComponent(StateComponent.class);
        if((entity.getComponent(BasketComponent.class)).wasHit && stateC.get()==StateComponent.STATE_HIT && stateC.time>1f) {
            lvlfactory.makeParticleEffect(ParticleEffectManager.STAR, (entity.getComponent(B2dBodyComponent.class)));
            for(Entity e :(entity.getComponent(BasketComponent.class)).entities )
                lvlfactory.removeEntity(e);
            lvlfactory.removeEntity(entity);

        }
    }
}
