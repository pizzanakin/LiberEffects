package net.libercraft.libereffects.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import net.libercraft.libercore.LiberCore;
import net.libercraft.libercore.interfaces.Updatable;

public class ArmorStandManager implements Updatable, Listener {

	public static List<ArmorStand> stands;
	public static Map<ArmorStand, Integer> armorAnimation;
	
	public ArmorStandManager() {
		stands = new ArrayList<ArmorStand>();
		for (World world:LiberCore.get().getServer().getWorlds()) {
			for (Entity e:world.getEntities()) {
				if (e.getScoreboardTags().contains("STATUE") && e.getType().equals(EntityType.ARMOR_STAND))
					stands.add((ArmorStand)e);
			}
		}
		armorAnimation = new HashMap<ArmorStand, Integer>();
		registerUpdatable();
	}
	
	public void update() {
		for (int i = 0; i < stands.size(); i++) {
			ArmorStand s = stands.get(i);
			s.setFireTicks(0);
			if (s.isDead()) {
				stands.remove(i);
				i--;
			}
		}
		
		// Animate stands part of the magic armor animation
		for (Map.Entry<ArmorStand, Integer> entry:armorAnimation.entrySet()) {
			ArmorStand stand = entry.getKey();
			int frame = entry.getValue();
			if (stand.isDead()) {
				armorAnimation.remove(stand);
			}
			
			if (frame == 0) {
				frame = 1;
			} else {
				if (frame < 32)
					frame++;
				else
					frame = 1;
			}
			
			Location oldloc = stand.getLocation();
			if (frame <= 16)
				oldloc.add(0, 0.05, 0);
			else
				oldloc.add(0, -0.05, 0);
			oldloc.setYaw(oldloc.getYaw()+1);
			stand.teleport(oldloc);
			entry.setValue(frame);
		}
	}
	
	public static ArmorStand create(Location location, boolean small, ItemStack helmet, double x, double y, double z, double pitch, double yaw, double roll, String tag) {
		Location loc = location.clone();
		loc.add(x, y, z);
		ArmorStand armorstand = loc.getWorld().spawn(loc, ArmorStand.class);
		armorstand.setGravity(false);
		armorstand.setSmall(small);
		armorstand.setInvulnerable(true);
		armorstand.setVisible(false);
		armorstand.setHelmet(helmet);
		
		armorstand.addScoreboardTag(tag);
		armorstand.addScoreboardTag("STATUE");
		
		armorstand.setHeadPose(new EulerAngle(pitch * (Math.PI/180), yaw * (Math.PI/180), roll * (Math.PI/180)));
		stands.add(armorstand);
		return armorstand;
	}
	
	@EventHandler
	public void preventTake(PlayerArmorStandManipulateEvent e) {
		if (e.getRightClicked().getScoreboardTags().contains("STATUE"))
			e.setCancelled(true);
	}
	
	@EventHandler
	public static void onPlayerMove(PlayerMoveEvent e) {
		for (ArmorStand s:stands) {
			if (!e.getPlayer().getWorld().equals(s.getWorld()))
				continue;
			
			if (s.getLocation().distance(e.getTo()) > 3)
				continue;
			
			Location first;
			Location second;
			if (s.isSmall()) {
				first = s.getLocation().add(-0.4, 0.5, -0.4);
				second = s.getLocation().add(0.4, 1.0, 0.4);
			} else {
				first = s.getLocation().add(-0.6, 1.3, -0.6);
				second = s.getLocation().add(0.6, 2.1, 0.6);
			}
			
			// Check for only x and z
			if (	e.getTo().getX() >= first.getX()&&
					e.getTo().getZ() >= first.getZ()&&
					e.getTo().getX() < second.getX()&&
					e.getTo().getZ() < second.getZ()) {
				
				// Check if player body is on the same height as the stand
				double feet = e.getTo().getY();
				double head = feet + 1.6;
				if (feet > second.getY() || head < first.getY())
					continue;
				
				double x = e.getFrom().getX() - first.getX();
				double z = e.getFrom().getZ() - first.getZ();
				
				// If player is on west side
				if (x < z && x + z < 1) {
					// Keep adjusting location till the player is outside the collision zone
					while (e.getTo().getX() > first.getX()) {
						e.getTo().add(-0.005, 0, 0);
					}
				}
				
				// Check if player is on south side
				if (x < z && x + z > 1) {
					// Keep adjusting location till the player is outside the collision zone
					while (e.getTo().getZ() < second.getZ()) {
						e.getTo().add(0, 0, 0.005);
					}
				}
				
				// Check if player is on east side
				if (x > z && x + z > 1) {
					// Keep adjusting location till the player is outside the collision zone
					while (e.getTo().getX() < second.getX()) {
						e.getTo().add(0.005, 0, 0);
					}
				}
				
				// Check if player is on north side
				if (x > z && x + z < 1) {
					// Keep adjusting location till the player is outside the collision zone
					while (e.getTo().getZ() >= first.getZ()) {
						e.getTo().add(0, 0, -0.005);
					}
				}
			}
		}
	}
}
