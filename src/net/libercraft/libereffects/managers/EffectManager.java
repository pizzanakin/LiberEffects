package net.libercraft.libereffects.managers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import net.libercraft.libercore.interfaces.Updatable;
import net.libercraft.libereffects.LiberEffects;

public class EffectManager implements Updatable {
	private static Map<Animation, Frame[]> effects;
	private static List<Effect> playing;
	
	public EffectManager() {
		playing = new ArrayList<Effect>();
		effects = new HashMap<Animation, Frame[]>();
		
		if (!LiberEffects.get().getDataFolder().exists())
			LiberEffects.get().getDataFolder().mkdirs();
		
		for (Animation animation:Animation.values()) {
			BufferedImage img;
			try {
				URL resource = getClass().getResource("/resources/" + animation.name() + ".png");
				System.out.println(resource);
				img = ImageIO.read(resource);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			Frame[] frames = new Frame[10];
			
			// read file to obtain pixels and load them into frames
			for (int frame = 0; frame < 10; frame++) {
				List<Pixel> pixels = new ArrayList<Pixel>();
				for (int zSpace = 0; zSpace < 10; zSpace++) {
					for (int ySpace = 0; ySpace < 10; ySpace++) {
						for (int xSpace = 0; xSpace < 10; xSpace++) {
							int p = img.getRGB(xSpace + (10 * zSpace), ySpace + (10 * frame));
							
							// skip pixels with an alpha value
							int a = (p >> 24) & 0xff;
							if (a < 255)
								continue;
							
							// take rgb values of pixel
							int r = (p >> 16) & 0xff;
							int g = (p >> 8) & 0xff;
							int b = p & 0xff;
							
							double x = (-0.2 * xSpace) + 0.9;
							double y = (-0.2 * ySpace) + 0.9;
							double z = (0.2 * zSpace) - 0.9;
							
							pixels.add(new Pixel(x, y, z, r, g, b));
						}
					}
				}
				frames[frame] = new Frame(pixels);
			}
			
			effects.put(animation, frames);
		}
		registerUpdatable();
	}
	
	public void update() {
		for (int i = 0; i < playing.size(); i++) {
			Effect e = playing.get(i);
			showNextFrame(e);
		}
	}
	
	public static void showNextFrame(Effect e) {
		Frame frame = e.getCurrentFrame();
		for (Pixel p:frame.pixels) {
			
			double[] dir = e.getDirection();
			Vector pixelVector = p.vector.clone();
			Location pixelLoc = e.getLocation().clone();

			rotateAroundAxisX(pixelVector, dir[0], dir[1]);
			rotateAroundAxisY(pixelVector, dir[2], dir[3]);
			rotateAroundAxisZ(pixelVector, dir[4], dir[5]);

			pixelLoc.add(pixelVector);
			ParticleManager.summonDust(pixelLoc, p.red, p.green, p.blue);
		}
		e.increaseFrame();
	}
	
	private static Vector rotateAroundAxisX(Vector v, double cos, double sin) {
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    private static Vector rotateAroundAxisY(Vector v, double cos, double sin) {
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    private static Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }
    
    public static class Rotation {
    	private double xradian;
    	private double yradian;
    	private double zradian;
    	public Rotation(double pitch, double yaw, double roll) {
			this.xradian = Math.toRadians(pitch);
			this.yradian = Math.toRadians(yaw);
			this.zradian = Math.toRadians(roll);
    	}
    	public double getPitch() {
    		return xradian;
    	}
    	public double getYaw() {
    		return yradian;
    	}
    	public double getRoll() {
    		return zradian;
    	}
    }
	
	public static class Effect {
		private Location location;
		private int frame;
		private double[] axes;
		private Frame[] frames;
		private boolean repeat;
		
		public Effect(Location location, Rotation rotation, Animation animation, boolean repeat) {
			this.location = location;
			this.frame = 0;
			this.frames = effects.get(animation);
			this.repeat = repeat;
			this.axes = new double[6];
			
			// the numbers are the angles on which you want to rotate your animation.
			axes[0] = Math.cos(rotation.getPitch()); // getting the cos value for the pitch.
			axes[1] = Math.sin(rotation.getPitch()); // getting the sin value for the pitch.

			// DON'T FORGET THE ' - ' IN FRONT OF 'yangle' HERE.
			axes[2] = Math.cos(-rotation.getYaw()); // getting the cos value for the yaw.
			axes[3] = Math.sin(-rotation.getYaw()); // getting the sin value for the yaw.
			
			axes[4] = Math.cos(rotation.getRoll());
			axes[5] = Math.sin(rotation.getRoll());
			
			playing.add(this);
		}
		
		public Location getLocation() {
			return location.clone();
		}
		
		public void setLocation(Location location) {
			this.location = location;
		}
		
		public double[] getDirection() {
			return axes;
		}
		
		public Frame getCurrentFrame() {
			int displayFrame = (int) Math.floor(frame / 2);
			return frames[displayFrame];
		}
		
		public void increaseFrame() {
			frame++;
			if (frame >= 20) {
				frame = 0;
				if (!repeat) 
					kill();
			}
		}
		
		public void kill() {
			playing.remove(this);
		}
	}
	
	public enum Animation {
		TEST,
		FIRE,
		WATER,
		ENERGY;
	}
	
	public static class Frame {
		protected List<Pixel> pixels;
		public Frame(List<Pixel> pixels) {
			this.pixels = pixels;
		}
	}
	
	public static class Pixel {
		protected Vector vector;
		protected int red;
		protected int green;
		protected int blue;
		public Pixel(double x, double y, double z, int r, int g, int b) {
			this.vector = new Vector(x, y, z);
			this.red = r;
			this.green = g;
			this.blue = b;
		}
	}
}
