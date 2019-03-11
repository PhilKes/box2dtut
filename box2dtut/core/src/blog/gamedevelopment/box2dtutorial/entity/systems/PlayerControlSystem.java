package blog.gamedevelopment.box2dtutorial.entity.systems;

import blog.gamedevelopment.box2dtutorial.DFUtils;
import blog.gamedevelopment.box2dtutorial.LevelFactory;
import blog.gamedevelopment.box2dtutorial.ParticleEffectManager;
import blog.gamedevelopment.box2dtutorial.controller.KeyboardController;
import blog.gamedevelopment.box2dtutorial.entity.components.B2dBodyComponent;
import blog.gamedevelopment.box2dtutorial.entity.components.BulletComponent;
import blog.gamedevelopment.box2dtutorial.entity.components.PlayerComponent;
import blog.gamedevelopment.box2dtutorial.entity.components.StateComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PlayerControlSystem extends IteratingSystem{

	private final Sound soundJump;
	private LevelFactory lvlFactory;
	ComponentMapper<PlayerComponent> pm;
	ComponentMapper<B2dBodyComponent> bodm;
	ComponentMapper<StateComponent> sm;
	KeyboardController controller;
	public static final float MAX_ANGULAR=5,MAX_SPEED=10f;
	
	@SuppressWarnings("unchecked")
	public PlayerControlSystem(KeyboardController keyCon, LevelFactory lvlf,Sound soundJump) {
		super(Family.all(PlayerComponent.class).get());
		this.soundJump=soundJump;
		controller = keyCon;
		lvlFactory = lvlf;
		pm = ComponentMapper.getFor(PlayerComponent.class);
		bodm = ComponentMapper.getFor(B2dBodyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		B2dBodyComponent b2body = bodm.get(entity);
		StateComponent state = sm.get(entity);
		PlayerComponent player = pm.get(entity);

		player.cam.position.y = b2body.body.getPosition().y;
		
		if(b2body.body.getLinearVelocity().y > 0f && state.get() != StateComponent.STATE_FALLING){
			state.set(StateComponent.STATE_FALLING);
		}
		
		if(b2body.body.getLinearVelocity().y <.2f && player.onPlatform){
			if(state.get() == StateComponent.STATE_FALLING){
				state.set(StateComponent.STATE_NORMAL);
			}
			if(b2body.body.getLinearVelocity().x != 0 && state.get() != StateComponent.STATE_MOVING){
				state.set(StateComponent.STATE_MOVING);
			}
		}
		
		// make player teleport higher
		if(player.onSpring){
			b2body.body.setLinearVelocity(b2body.body.getLinearVelocity().x,0);
			b2body.body.applyLinearImpulse(0, 100f, b2body.body.getWorldCenter().x,b2body.body.getWorldCenter().y, true);
			//add particle effect at feet
			//lvlFactory.makeParticleEffect(ParticleEffectManager.SMOKE, b2body.body.getPosition().x, b2body.body.getPosition().y);
			// move player
			//b2body.body.setTransform(b2body.body.getPosition().x, b2body.body.getPosition().y+ 10, b2body.body.getAngle());
			//state.set(StateComponent.STATE_JUMPING);
			soundJump.play();
			player.onSpring = false;
		}
		
		
		if(controller.left){
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, -7f, 0.2f),b2body.body.getLinearVelocity().y);
/*			if(b2body.body.getLinearVelocity().x-.5f>=MAX_SPEED)
				b2body.body.applyLinearImpulse(-.5f,0f,-1f,0f,true);*/
/*			if(b2body.body.getAngularVelocity()+.25f<=MAX_ANGULAR);
				b2body.body.applyAngularImpulse(+.25f,true);*/
		}
		if(controller.right){
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, 7f, 0.2f),b2body.body.getLinearVelocity().y);
/*			if(b2body.body.getLinearVelocity().x+.5f<=MAX_SPEED)
				b2body.body.applyLinearImpulse(+.5f,0f,+1f,0f,true);*/
/*			if(b2body.body.getAngularVelocity()-.25f>=-1*MAX_ANGULAR);
				b2body.body.applyAngularImpulse(-.25f,true);*/
		}
		
		if(!controller.left && ! controller.right){
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, 0, 0.1f),b2body.body.getLinearVelocity().y);
			b2body.body.applyAngularImpulse(-1*b2body.body.getAngularVelocity()/50000,true);
		}
		
		if(controller.up && 
				(state.get() == StateComponent.STATE_NORMAL || state.get() == StateComponent.STATE_MOVING)){
			b2body.body.applyLinearImpulse(0, 16f * b2body.body.getMass() , b2body.body.getWorldCenter().x,b2body.body.getWorldCenter().y, true);
			state.set(StateComponent.STATE_JUMPING);
			player.onPlatform = false;
			player.onSpring = false;
		}
		
		if(controller.down){
			b2body.body.applyLinearImpulse(0, -5f, b2body.body.getWorldCenter().x,b2body.body.getWorldCenter().y, true);
		}
		
		if(player.timeSinceLastShot > 0){
			player.timeSinceLastShot -= deltaTime;
		}
		
		/*if(controller.isMouse1Down){ // if mouse button is pressed
			//System.out.println(player.timeSinceLastShot+" ls:sd "+player.shootDelay);
			// user wants to fire
			if(player.timeSinceLastShot <=0){ // check the player hasn't just shot
				//player can shoot so do player shoot
				Vector3 mousePos = new Vector3(controller.mouseLocation.x,controller.mouseLocation.y,0); // get mouse position
				player.cam.unproject(mousePos); // convert position from screen to box2d world position
								
				Vector2 aim = DFUtils.aimTo(b2body.body.getPosition(), mousePos);
				aim.scl(7);
				// create a bullet
				lvlFactory.createBullet(b2body.body.getPosition().x,
						b2body.body.getPosition().y, 
						aim.x,
						aim.y, 
						BulletComponent.Owner.PLAYER);
				//reset timeSinceLastShot
				player.timeSinceLastShot = player.shootDelay;
			}	
		}*/
	}
}
