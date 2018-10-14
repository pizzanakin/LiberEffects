package net.libercraft.libereffects.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.libercraft.libercore.interfaces.Loadable;

public class ParticleManager implements Listener, Loadable {
	public static List<String> low;
	public static List<String> medium;
	public static List<String> high;
	
	public ParticleManager() {
		registerLoadable();
	}
	
	public void load() {
		low = new ArrayList<String>();
		medium = new ArrayList<String>();
		high = new ArrayList<String>();
		
		Map<String, Integer> data = EffectDatabase.loadQuality();
		for (Map.Entry<String, Integer> entry:data.entrySet()) {
			String uuid = entry.getKey();
			int quality = entry.getValue();
			switch (quality) {
			case 0:
				low.add(uuid);
				break;
			case 1:
				medium.add(uuid);
				break;
			case 2:
				high.add(uuid);
				break;
			}
		}
	}
	
	public void close() {
		for (String u:low) 
			EffectDatabase.saveQuality(u, 0);
		for (String u:medium) 
			EffectDatabase.saveQuality(u, 1);
		for (String u:high) 
			EffectDatabase.saveQuality(u, 2);
	}
	
	@EventHandler
	public static void onPlayerJoin(PlayerJoinEvent e) {
		String uuid = e.getPlayer().getUniqueId().toString();
		if (low.contains(uuid) || medium.contains(uuid) || high.contains(uuid))
			return;
		
		medium.add(uuid);
	}
	
	public static void setLow(Player player) {
		low.add(player.getUniqueId().toString());
		medium.remove(player.getUniqueId().toString());
		high.remove(player.getUniqueId().toString());
	}
	
	public static void setMedium(Player player) {
		low.remove(player.getUniqueId().toString());
		medium.add(player.getUniqueId().toString());
		high.remove(player.getUniqueId().toString());
	}
	
	public static void setHigh(Player player) {
		low.remove(player.getUniqueId().toString());
		medium.remove(player.getUniqueId().toString());
		high.add(player.getUniqueId().toString());
	}
	
	public static void altarPillarParticles(Location loc) {
		for (Player player:loc.getWorld().getPlayers()) {
			double distance = loc.distance(player.getLocation());
			if (low.contains(player.getUniqueId().toString()) && distance < 12) {
				ParticleSprite.SINGLE_FIRE.summon(loc);
			}
			if (medium.contains(player.getUniqueId().toString()) && distance < 20) {
				ParticleSprite.SINGLE_FIRE.summon(loc);
			}
			if (high.contains(player.getUniqueId().toString()) && distance < 32) {
				ParticleSprite.SINGLE_FIRE.summon(loc);
			}
		}
	}
	
	public static void teleportParticles(Location loc) {
		for (Player player:loc.getWorld().getPlayers()) {
			double distance = loc.distance(player.getLocation());
			if (low.contains(player.getUniqueId().toString()) && distance < 12) {
				player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 2, 0.2, 0.2, 0.2, 0.05);
			}
			if (medium.contains(player.getUniqueId().toString()) && distance < 20) {
				player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 10, 0.2, 0.2, 0.2, 0.05);
			}
			if (high.contains(player.getUniqueId().toString()) && distance < 32) {
				player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 20, 0.2, 0.2, 0.2, 0.05);
			}
		}
	}
	
	public static void summonDust(Location location, int red, int green, int blue) {
		for (Player player:location.getWorld().getPlayers()) 
			summonDust(player, location, red, green, blue);
	}
	
	public static void summonDust(Player player, Location loc, int red, int green, int blue) {
		double r = (double) red/255;
		double g = (double) green/255;
		double b = (double) blue/255;
		if (r == 0)
			r = 0.001;

		double distance = loc.distance(player.getLocation());
		if (low.contains(player.getUniqueId().toString()) && distance < 12) {
			player.spawnParticle(Particle.REDSTONE, loc, 0, r, g, b, 1);
		}
		if (medium.contains(player.getUniqueId().toString()) && distance < 20) {
			player.spawnParticle(Particle.REDSTONE, loc, 0, r, g, b, 1);
		}
		if (high.contains(player.getUniqueId().toString()) && distance < 32) {
			player.spawnParticle(Particle.REDSTONE, loc, 0, r, g, b, 1);
		}
	}
	
	
	public static enum ParticleSprite {
		FIRE_BALL,
		ORANGE_BALL,
		CYAN_BALL,
		CLOUD_BALL,
		SPARK_BALL,
		SINGLE_FIRE,
		SINGLE_WATER,
		SINGLE_ENERGY,
		SINGLE_RED,
		SINGLE_YELLOW,
		SINGLE_BLUE,
		SINGLE_LIGHTBLUE,
		SINGLE_ORANGE,
		SINGLE_CYAN,
		SINGLE_GRAY,
		SINGLE_WHITE,
		SINGLE_CLOUD,
		SINGLE_FLAME,
		SINGLE_SMOKE,
		SINGLE_SPARK,
		SINGLE_WAKE;
		
		public void summon(Location location) {
			for (Player player:location.getWorld().getPlayers()) {
				if (location.distance(player.getLocation()) > 80) continue;
				switch (this) {
				case ORANGE_BALL:
					for (int i = 0; i < 5; i++) {
						double x = location.getX();
						double y = location.getY();
						double z = location.getZ();
						player.spawnParticle(Particle.REDSTONE, randomise(x, 0.3), randomise(y, 0.3), randomise(z, 0.5), 0, 1, 0.32, 0.001, 1);
					}
					break;
				case CYAN_BALL:
					for (int i = 0; i < 5; i++) {
						double x = location.getX();
						double y = location.getY();
						double z = location.getZ();
						player.spawnParticle(Particle.REDSTONE, randomise(x, 0.3), randomise(y, 0.3), randomise(z, 0.5), 0, 0.001, 1.0, 1.0, 1);
					}
					break;
				case CLOUD_BALL:
					player.spawnParticle(Particle.CLOUD, location, 5, 0.05, 0.05, 0.05, 0.0);
					break;
				case FIRE_BALL:
					player.spawnParticle(Particle.FLAME, location, 5, 0.05, 0.05, 0.05, 0.0);
					break;
				case SPARK_BALL:
					player.spawnParticle(Particle.FIREWORKS_SPARK, location, 5, 0.05, 0.05, 0.05, 0.0);
					break;
				case SINGLE_FIRE:
					player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 0.001, 0.001, 1);
					player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 1.0, 0.001, 1);
					player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 0.32, 0.001, 1);
					break;
				case SINGLE_WATER:
					player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 0.5, 1.0, 1);
					player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 1.0, 1.0, 1);
					player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 0.5, 1.0, 1);
					break;
				case SINGLE_ENERGY:
					player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 1.0, 1.0, 1);
					player.spawnParticle(Particle.REDSTONE, location, 0, 0.34, 0.76, 1.0, 1.0);
					player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 1.0, 1.0, 1.0);
					break;
				case SINGLE_RED:
					player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 0.001, 0.001, 1);
					break;
				case SINGLE_YELLOW:
					player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 1.0, 0.001, 1);
					break;
				case SINGLE_BLUE:
					player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 0.5, 1.0, 1);
					break;
				case SINGLE_LIGHTBLUE:
					player.spawnParticle(Particle.REDSTONE, location, 0, 0.34, 0.76, 1.0, 1.0);
					break;
				case SINGLE_ORANGE:
					player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 0.32, 0.001, 1);
					break;
				case SINGLE_CYAN:
					player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 1.0, 1.0, 1);
					break;
				case SINGLE_GRAY:
					player.spawnParticle(Particle.REDSTONE, location, 0, 0.45, 0.45, 0.45, 1);
					break;
				case SINGLE_WHITE:
					player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 1.0, 1.0, 1.0);
					break;
				case SINGLE_CLOUD:
					player.spawnParticle(Particle.CLOUD, location, 1, 0.0, 0.0, 0.0, 0.0);
					break;
				case SINGLE_FLAME:
					player.spawnParticle(Particle.FLAME, location, 1, 0.0, 0.0, 0.0, 0.0);
					break;
				case SINGLE_SMOKE:
					player.spawnParticle(Particle.SMOKE_NORMAL, location, 1, 0.0, 0.0, 0.0, 0.0);
					break;
				case SINGLE_SPARK:
					player.spawnParticle(Particle.FIREWORKS_SPARK, location, 1, 0.0, 0.0, 0.0, 0.0);
					break;
				case SINGLE_WAKE:
					player.spawnParticle(Particle.WATER_WAKE, location.getX(), location.getY() + 0.15, location.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
					break;
				default:
					break;
				
				}
			}
		}
		private double randomise(double input, double factor) {
			return (Math.random() * factor) + input - (factor / 2);
		}
	}
}
