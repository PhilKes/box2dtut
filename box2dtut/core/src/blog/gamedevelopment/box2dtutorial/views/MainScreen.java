package blog.gamedevelopment.box2dtutorial.views;

import blog.gamedevelopment.box2dtutorial.Box2DTutorial;
import blog.gamedevelopment.box2dtutorial.DFUtils;
import blog.gamedevelopment.box2dtutorial.LevelFactory;
import blog.gamedevelopment.box2dtutorial.controller.KeyboardController;
import blog.gamedevelopment.box2dtutorial.entity.components.PlayerComponent;
import blog.gamedevelopment.box2dtutorial.entity.systems.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static blog.gamedevelopment.box2dtutorial.loader.B2dAssetManager.*;

public class MainScreen implements Screen {
	private final Sound soundBoard;
	private Sound soundSwish;
	private Box2DTutorial parent;
	private OrthographicCamera cam;
	private KeyboardController controller;
	private SpriteBatch sb;
	private PooledEngine engine;
	private LevelFactory lvlFactory;

	
	private Sound soundJump;
	//private Sound boing;
	private Entity player;	

	
	/**
	 * @param box2dTutorial
	 */
	public MainScreen(Box2DTutorial box2dTutorial) {
		parent = box2dTutorial;
		parent.assMan.queueAddSounds();
		parent.assMan.manager.finishLoading();
		//ping = parent.assMan.manager.get("sounds/ping.wav",Sound.class);
		soundJump= parent.assMan.manager.get(SOUND_JUMP,Sound.class);
		soundBoard = parent.assMan.manager.get(SOUND_BOARD,Sound.class);
		soundSwish = parent.assMan.manager.get(SOUND_SWISH,Sound.class);
		controller = new KeyboardController();
		engine = new PooledEngine();
		// next guide - changed this to atlas
		lvlFactory = new LevelFactory(engine,parent.assMan);

		sb = new SpriteBatch();


		RenderingSystem renderingSystem = new RenderingSystem(sb,parent.assMan.manager.get("images/loading.atlas", TextureAtlas.class));
		cam = renderingSystem.getCamera();
		sb.setProjectionMatrix(cam.combined);

        engine.addSystem(new PhysicsSystem(lvlFactory.world));
        engine.addSystem(renderingSystem);
		engine.addSystem(new SpringSystem());
        engine.addSystem(new CollisionSystem(soundBoard,soundSwish));
		engine.addSystem(new AnimationSystem());
        engine.addSystem(new SteeringSystem());
        engine.addSystem(new PlayerControlSystem(controller,lvlFactory, soundJump));
        player = lvlFactory.createPlayer(cam);
        //engine.addSystem(new EnemySystem(lvlFactory));
        engine.addSystem(new WallSystem(player));
        engine.addSystem(new WaterFloorSystem(player));
        engine.addSystem(new BulletSystem(player));
        engine.addSystem(new LevelGenerationSystem(lvlFactory));

        
        lvlFactory.createFloor();
        lvlFactory.createWaterFloor();
        
        int wallWidth = (int) (1*RenderingSystem.PPM);
        int wallHeight = (int) (60*RenderingSystem.PPM);
        TextureRegion wallRegion = DFUtils.makeTextureRegion(wallWidth, wallHeight, "222222FF");
        lvlFactory.createWalls(wallRegion); //TODO make some damn images for this stuff  
	}
	

	@Override
	public void show() {
		Gdx.input.setInputProcessor(controller);	
	}

	@Override
	public void render(float delta) {

		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		engine.update(delta);
		
		//check if player is dead. if so show end screen
		PlayerComponent pc = (player.getComponent(PlayerComponent.class));
		if(pc.hasScored){
			parent.baskets++;
			pc.hasScored=false;
		}
		if(pc.isDead){
			DFUtils.log("YOU DIED : back to menu you go!");
			parent.lastScore = (int) pc.cam.position.y;
			parent.changeScreen(Box2DTutorial.ENDGAME);	
		}

	}

	@Override
	public void resize(int width, int height) {		
	}

	@Override
	public void pause() {		
	}

	@Override
	public void resume() {	
		Gdx.input.setInputProcessor(controller);	
	}

	@Override
	public void hide() {		
	}

	@Override
	public void dispose() {
		sb.dispose();
		engine.clearPools();
	}

}
