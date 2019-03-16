package blog.gamedevelopment.box2dtutorial.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class BasketComponent implements Component, Pool.Poolable {
    public boolean wasHit=false;
    public Array<Entity> entities=new Array<>();
    @Override
    public void reset() {
        wasHit=false;
        entities=new Array<>();

    }
}
