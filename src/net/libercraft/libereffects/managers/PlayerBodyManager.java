package net.libercraft.libereffects.managers;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.MainHand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.libercore.LiberCore;
import net.libercraft.libercore.interfaces.PlayerData;
import net.libercraft.libercore.interfaces.Updatable;
import net.libercraft.libercore.managers.PlayerManager;

public class PlayerBodyManager implements Updatable {
	
	public PlayerBodyManager() {
		registerUpdatable();
	}
	
	public void update() {
		for (Map.Entry<Player, List<PlayerData>> entry:PlayerManager.players.entrySet()) 
			for (PlayerData data:entry.getValue())
				if (data instanceof BodyData)
					((BodyData)data).update();
	}
	
	public static BodyData get(Player player) {
		for (Map.Entry<Player, List<PlayerData>> entry:PlayerManager.players.entrySet()) 
			for (PlayerData data:entry.getValue())
				if (data instanceof BodyData)
					return (BodyData) data;
		return new BodyData(player);
	}
	
	@EventHandler
	public static void updatePlayerRotation(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		BodyData data = get(player);
		
		if (e.getFrom().getX() != e.getTo().getX() | e.getFrom().getZ() != e.getTo().getZ()) {
			// Compare walking direction and player eye direction to see if the player is walking straight
			double x = e.getTo().getX() - e.getFrom().getX();
			double z = e.getTo().getZ() - e.getFrom().getZ();
			
			Vector walkDirection = new Vector(x, 0, z).normalize();
			Vector playerDirection = player.getLocation().getDirection().normalize();
			playerDirection.setY(0);
			
			// Compare the two angles of the vectors
			double angle1 = Math.atan2(playerDirection.getX(), playerDirection.getZ()) * (-180 / Math.PI);
			double angle2 = Math.atan2(walkDirection.getX(), walkDirection.getZ()) * (-180 / Math.PI);
			if ((angle2 - angle1) > 180) angle1 += 360;
			if ((angle2 - angle1) < -180) angle1 -= 360;
			
			// If the walk direction is not the same as the direction the player is looking, change the body accordingly
			if (((angle2 - angle1) > 20 && (angle2 - angle1) < 160)	|| ((angle2 - angle1) < -20 && (angle2 - angle1) > -160)) {
				double difference = angle2 - angle1;
				if (130 > difference && difference > 0) {
					//caster.walkLeft = true;
				}
				else if (difference > 130) {
					//caster.walkRight = true;
				}
				else if (-130 < difference && difference < 0) {
					//caster.walkRight = true;
				}
				else if (difference < -130) {
					//caster.walkLeft = true;
				}
			}
			// Slowly reset the body back to the center
			else new BukkitRunnable() {
				@Override public void run() {
					data.bodyYaw = e.getTo().getYaw();
					while (data.bodyYaw < 180) data.bodyYaw += 360;
					while (data.bodyYaw > 180) data.bodyYaw -= 360;
				}
			}.runTaskLater(LiberCore.get(), 2);
		}
		
		// Compare the player eye direction to the body rotation and update the body rotation if necessary
		float faceYaw = e.getTo().getYaw();
		while (faceYaw < 180) faceYaw += 360;
		while (faceYaw > 180) faceYaw -= 360;
		float difference = faceYaw - data.bodyYaw;
		if (difference < -250) difference +=360;
		if (difference > 250) difference -=360;
		if (difference > 50) data.bodyYaw = faceYaw - 50;
		if (difference < -50) data.bodyYaw = faceYaw + 50;
	}
	
	public static enum WalkDirection {
		FORWARD,
		FORWARD_LEFT,
		FORWARD_RIGHT,
		LEFT,
		RIGHT,
		BACKWARD_LEFT,
		BACKWARD_RIGHT,
		BACKWARD;
	}
	
	public static class BodyData implements PlayerData {
		public float bodyYaw;
		public Vector bodyVector;
		public WalkDirection direction;
		
		public BodyData(Player player) {
			initialisePlayerData(player);
		}
		
		public void update() {
			double radianBody = (bodyYaw + 90) * (Math.PI / 180);
			bodyVector = new Vector(Math.cos(radianBody), 0, Math.sin(radianBody)).normalize();
			/*switch (direction) {
			case BACKWARD:
				break;
			case BACKWARD_LEFT:
				break;
			case BACKWARD_RIGHT:
				break;
			case FORWARD:
				break;
			case FORWARD_LEFT:
				break;
			case FORWARD_RIGHT:
				break;
			case LEFT:
				break;
			case RIGHT:
				break;
			default:
				break;
			}
			/*if (walkRight) {
				Vector addVector = player.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(-0.25);
				bodyVector.add(addVector).normalize();
			}
			else if (walkLeft) {
				Vector addVector = player.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(0.25);
				bodyVector.add(addVector).normalize();
			}*/
		}
		
		public Location getMainHandLocation() {
			// Calculate vector at 90 degree angle of player's body
			Vector bodyOrientationAngle = bodyVector.clone().crossProduct(new Vector(0, 1, 0));
			
			// Make minor positioning adjustments
			bodyOrientationAngle.normalize().multiply(0.4); // set location distance from center to 0.4
			bodyOrientationAngle.add(bodyVector.clone().multiply(0.1)); // move location slightly forward
			bodyOrientationAngle.add(new Vector(0, 0.75, 0)); // move location up to hand location
			
			// Make adjustment based on player's selected main hand
			if (getPlayer().getMainHand() == MainHand.RIGHT)
				bodyOrientationAngle.multiply(-1);
			
			return getPlayer().getLocation().add(bodyOrientationAngle);
		}
		
		public Location getOffHandLocation() {
			return getMainHandLocation().multiply(-1);
		}
	}
}
