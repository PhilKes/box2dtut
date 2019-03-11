package blog.gamedevelopment.box2dtutorial.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool.Poolable;

import java.util.ArrayList;

public class TextureComponent implements Component, Poolable {
    public TextureRegion region = null;

	@Override
	public void reset() {
		region = null;
	}
}
