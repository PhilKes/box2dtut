package blog.gamedevelopment.box2dtutorial.entity.systems;

import blog.gamedevelopment.box2dtutorial.entity.components.*;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;

public class CollisionSystem  extends IteratingSystem {
	private final Sound soundBoard;
	private final Sound soundSwish;
	ComponentMapper<CollisionComponent> cm;
	 ComponentMapper<PlayerComponent> pm;
	 ComponentMapper<StateComponent> sm;

	@SuppressWarnings("unchecked")
	public CollisionSystem(Sound soundBoard,Sound soundSwish) {
		super(Family.all(CollisionComponent.class).get());
		this.soundBoard=soundBoard;
		this.soundSwish=soundSwish;
		 cm = ComponentMapper.getFor(CollisionComponent.class);
		 pm = ComponentMapper.getFor(PlayerComponent.class);
		 sm=ComponentMapper.getFor(StateComponent.class);
	}

	/** Handles Collision between two Entitys based on their types */
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// get collision for this entity
		CollisionComponent cc = cm.get(entity);
		//get collided entity
		Entity collidedEntity = cc.collisionEntity;
		TypeComponent thisType = entity.getComponent(TypeComponent.class);
		TransformComponent thisTrans=entity.getComponent(TransformComponent.class);
		
		/** Player Collisions */
		if(thisType.type == TypeComponent.PLAYER){
			PlayerComponent pl = pm.get(entity);
			if(collidedEntity != null){
				TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
				if(type != null){
					switch(type.type){
					case TypeComponent.ENEMY:
						//do player hit enemy thing
						System.out.println("player hit enemy");
						pl.isDead = true;
						int score = (int) pl.cam.position.y;
						System.out.println("Score = "+ score);
						break;
					case TypeComponent.SCENERY:
						//do player hit scenery thing
						pm.get(entity).onPlatform = true;
						System.out.println("player hit scenery");
						break;
					case TypeComponent.PLATFORM:
						//do player hit scenery thing
						TransformComponent platTrans=collidedEntity.getComponent(TransformComponent.class);
						if(thisTrans.position.y>platTrans.position.y) {
							pm.get(entity).onPlatform=true;
							System.out.println("player hit scenery");
						}
						break;
					case TypeComponent.SPRING:
						//do player hit other thing
						TransformComponent springTrans=collidedEntity.getComponent(TransformComponent.class);
						if(thisTrans.position.y>springTrans.position.y) {
							pm.get(entity).onSpring=true;
							StateComponent state=sm.get(collidedEntity);
							state.set(StateComponent.STATE_JUMPING);
							System.out.println("player hit spring: bounce up");
						}
						break;	
					case TypeComponent.OTHER:
						//do player hit other thing
						System.out.println("player hit other");
						break;
					case TypeComponent.BASKET:
						TransformComponent basketTrans=collidedEntity.getComponent(TransformComponent.class);
						if(thisTrans.position.y+1>basketTrans.position.y) {
							System.out.println("SCORE");
							pl.hasScored=true;
							(collidedEntity.getComponent(BasketComponent.class)).wasHit=true;
							soundSwish.play();
							StateComponent state2=sm.get(collidedEntity);
							state2.set(StateComponent.STATE_HIT);
						}
						break;
					case TypeComponent.BOARD:
						/*TransformComponent boardTrans=collidedEntity.getComponent(TransformComponent.class);
						if(thisTrans.position.y>boardTrans.position.y) {
							System.out.println("SCORE");
							StateComponent state2=sm.get(collidedEntity);
							state2.set(StateComponent.STATE_HIT);
						}*/
						pm.get(entity).onPlatform=true;
						soundBoard.play();
						break;
					case TypeComponent.BULLET:
						// TODO add mask so player can't hit themselves
						BulletComponent bullet = Mapper.bulletCom.get(collidedEntity);
						if(bullet.owner != BulletComponent.Owner.PLAYER){ // can't shoot own team
							pl.isDead = true;
						}
						System.out.println("Player just shot. bullet in player atm");
						break;
					default:
						System.out.println("No matching type found");
					}
					cc.collisionEntity = null; // collision handled reset component
				}else{
					System.out.println("Player: collidedEntity.type == null");
				}
			}
		}/** Enemy Collision */
		else if(thisType.type == TypeComponent.ENEMY){
			if(collidedEntity != null){
				TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
				if(type != null){
					switch(type.type){
					case TypeComponent.PLAYER:
						System.out.println("enemy hit player");
						break;
					case TypeComponent.ENEMY:
						System.out.println("enemy hit enemy");
						break;
					case TypeComponent.SCENERY:
						System.out.println("enemy hit scenery");
						break;
					case TypeComponent.SPRING:
						System.out.println("enemy hit spring");
						break;	
					case TypeComponent.OTHER:
						System.out.println("enemy hit other");
						break;

					case TypeComponent.BULLET:
						EnemyComponent enemy = Mapper.enemyCom.get(entity);
						BulletComponent bullet = Mapper.bulletCom.get(collidedEntity);
						if(bullet.owner != BulletComponent.Owner.ENEMY){ // can't shoot own team
							bullet.isDead = true;
							enemy.isDead = true;
							System.out.println("enemy got shot");
						}
						break;
					default:
						System.out.println("No matching type found");
					}
					cc.collisionEntity = null; // collision handled reset component
				}else{
					System.out.println("Enemy: collidedEntity.type == null");
				}
			}
		}else{
			cc.collisionEntity = null;
		}
	}
}
