package blog.gamedevelopment.box2dtutorial;

import blog.gamedevelopment.box2dtutorial.entity.components.CollisionComponent;

import blog.gamedevelopment.box2dtutorial.entity.components.TypeComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class B2dContactListener implements ContactListener {

	//private B2dModel parent;
	
	public B2dContactListener(){ //B2dModel parent){
		//this.parent = parent;
	}
	
	@Override
	public void beginContact(Contact contact) {
		if(!contact.isEnabled())
			return;
		System.out.println("Contact");
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		System.out.println(fa.getBody().getType()+" has hit "+ fb.getBody().getType());
		
		if(fa.getBody().getUserData() instanceof Entity){
			Entity ent = (Entity) fa.getBody().getUserData();
			entityCollision(ent,fb);
			return;
		}else if(fb.getBody().getUserData() instanceof Entity){
			Entity ent = (Entity) fb.getBody().getUserData();
			entityCollision(ent,fa);
			return;
		}
	}

	private void entityCollision(Entity ent, Fixture fb) {
		if(fb.getBody().getUserData() instanceof Entity){
			Entity colEnt = (Entity) fb.getBody().getUserData();
			
			CollisionComponent col = ent.getComponent(CollisionComponent.class);
			CollisionComponent colb = colEnt.getComponent(CollisionComponent.class);
			
			if(col != null){
				col.collisionEntity = colEnt;
			}else if(colb != null){
				colb.collisionEntity = ent;
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		System.out.println("Contact end");
	}

	/** Do not collide with platform/spring from below **/
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		System.out.println(fa.getBody().getType()+" has hit "+ fb.getBody().getType());

		if(fa.getBody().getUserData() instanceof Entity) {
			Entity ent=(Entity) fa.getBody().getUserData();
			TypeComponent type=ent.getComponent(TypeComponent.class);
			if(type==null)
				return;
			if(type.type== TypeComponent.PLATFORM ||type.type==TypeComponent.SPRING){
				if(fa.getBody().getTransform().getPosition().y+1>fb.getBody().getTransform().getPosition().y)
					contact.setEnabled(false);
			}
		}else if(fb.getBody().getUserData() instanceof Entity) {
			Entity ent=(Entity) fb.getBody().getUserData();
			TypeComponent type=ent.getComponent(TypeComponent.class);
			if(type==null)
				return;
			if(type.type== TypeComponent.PLATFORM ||type.type==TypeComponent.SPRING){
				if(fb.getBody().getTransform().getPosition().y+1>fa.getBody().getTransform().getPosition().y)
					contact.setEnabled(false);
			}
		}
	}
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {		
	}

}
