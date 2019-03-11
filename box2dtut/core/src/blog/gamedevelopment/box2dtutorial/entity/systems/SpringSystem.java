package blog.gamedevelopment.box2dtutorial.entity.systems;

import blog.gamedevelopment.box2dtutorial.entity.components.SpringComponent;
import blog.gamedevelopment.box2dtutorial.entity.components.StateComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import static blog.gamedevelopment.box2dtutorial.LevelFactory.SPRING_CLOUD_DUR;

//Resets Bouncying Platform animation
public class SpringSystem extends IteratingSystem {
    ComponentMapper<StateComponent> sm;

    public SpringSystem() {
        super(Family.all(SpringComponent.class).get());
        sm = ComponentMapper.getFor(StateComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state=sm.get(entity);

        if(state.get()==StateComponent.STATE_JUMPING && state.time>SPRING_CLOUD_DUR) {
            state.set(StateComponent.STATE_NORMAL);
            System.out.println("Spring state: "+state.get());
        }
    }
}
