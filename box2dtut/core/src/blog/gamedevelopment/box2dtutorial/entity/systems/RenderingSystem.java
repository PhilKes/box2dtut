package blog.gamedevelopment.box2dtutorial.entity.systems;

import blog.gamedevelopment.box2dtutorial.entity.components.TextureComponent;
import blog.gamedevelopment.box2dtutorial.entity.components.TransformComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

public class RenderingSystem extends SortedIteratingSystem {
	// debug stuff
	private boolean shouldRender = true;

    public static final float PPM = 16.0f;
    static final float FRUSTUM_WIDTH = Gdx.graphics.getWidth()/PPM;//37.5f;
    static final float FRUSTUM_HEIGHT = Gdx.graphics.getHeight()/PPM;//.0f;
    TextureRegion background;

    public static final float PIXELS_TO_METRES = 1.0f / PPM;

    private static Vector2 meterDimensions = new Vector2();
    private static Vector2 pixelDimensions = new Vector2();
    public static Vector2 getScreenSizeInMeters(){
        meterDimensions.set(Gdx.graphics.getWidth()*PIXELS_TO_METRES,
                            Gdx.graphics.getHeight()*PIXELS_TO_METRES);
        return meterDimensions;
    }

    public static Vector2 getScreenSizeInPixesl(){
        pixelDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return pixelDimensions;
    }

    public static float PixelsToMeters(float pixelValue){
        return pixelValue * PIXELS_TO_METRES;
    }

    private SpriteBatch batch;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;
    private OrthographicCamera cam;

    private ComponentMapper<TextureComponent> textureM;
    private ComponentMapper<TransformComponent> transformM;

    @SuppressWarnings("unchecked")
	public RenderingSystem(SpriteBatch batch, TextureAtlas loading) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

        textureM = ComponentMapper.getFor(TextureComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);

        background=loading.findRegion("background");
        renderQueue = new Array<Entity>();

        this.batch = batch;
        
        comparator = new ZComparator();

        cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        cam.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);
    }

    /** Renders all entities (textures) in renderQueue (sorted by Z Position) */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        renderQueue.sort(comparator);
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.enableBlending();

        if(shouldRender){
	        batch.begin();
            batch.draw(background, cam.position.x -Gdx.graphics.getWidth()/2 , cam.position.y-Gdx.graphics.getHeight()/2,
                    Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                for(Entity entity : renderQueue) {
                    TextureComponent tex=textureM.get(entity);
                    TransformComponent t=transformM.get(entity);
                    TextureRegion tR=tex.region;
                        if(tR==null || t.isHidden) {
                            continue;
                        }

                        float width=t.width;
                        float height=t.height;

                        float originX=width / 2f;
                        float originY=height / 2f;

                        batch.draw(tR,
                                t.position.x - originX, t.position.y - originY,
                                originX, originY,
                                t.width, t.height,
                                t.scale.x, t.scale.y,
                                t.rotation);
                        /** OLD
                         * batch.draw(tex.region,
                         *                     t.position.x - originX, t.position.y - originY,
                         *                     originX, originY,
                         *                     width, height,
                         *                     PixelsToMeters(t.scale.x), PixelsToMeters(t.scale.y),
                         *                     t.rotation);
                         */
                    }

	        batch.end();
        }
        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    public OrthographicCamera getCamera() {
        return cam;
    }
}