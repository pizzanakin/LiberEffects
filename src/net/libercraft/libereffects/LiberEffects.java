package net.libercraft.libereffects;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import net.libercraft.libercore.interfaces.LiberPlugin;
import net.libercraft.libereffects.managers.ArmorStandManager;
import net.libercraft.libereffects.managers.EffectDatabase;
import net.libercraft.libereffects.managers.EffectManager;
import net.libercraft.libereffects.managers.ParticleManager;

public class LiberEffects extends JavaPlugin implements LiberPlugin {
	public static LiberEffects main;
	
	public EffectDatabase ed;
	public EffectManager em;
	public ParticleManager pm;
	public ArmorStandManager am;

	@Override
	public ChatColor colour() {
		return ChatColor.AQUA;
	}
	
	@Override
	public void onEnable() {
		main = this;
		
		ed = new EffectDatabase(this);
		em = new EffectManager();
		pm = new ParticleManager();
		am = new ArmorStandManager();

		getServer().getPluginManager().registerEvents(pm, this);
		getServer().getPluginManager().registerEvents(am, this);
		
		EffectCommandExecutor ece = new EffectCommandExecutor();
		getCommand("eff").setExecutor(ece);
		getCommand("quality").setExecutor(ece);
	}
	
	@Override
	public void onDisable() {
		main = null;
	}
	
	public static LiberEffects get() {
		return main;
	}
	
	public static EffectManager getEM() {
		return main.em;
	}
	
	public static ParticleManager getPM() {
		return main.pm;
	}
	
	public static ArmorStandManager getAM() {
		return main.am;
	}
}
