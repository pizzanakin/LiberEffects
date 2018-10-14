package net.libercraft.libereffects;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.libercraft.libereffects.managers.EffectManager.Effect;
import net.libercraft.libereffects.managers.EffectManager.Animation;
import net.libercraft.libereffects.managers.EffectManager.Rotation;
import net.libercraft.libercore.managers.MessageManager;
import net.libercraft.libereffects.managers.ParticleManager;

public class EffectCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		Player player = (Player) sender;

		switch (label) {
		case "eff":
			if (args.length == 0)
				return false;
			
			Animation animation = null;
			for (Animation a:Animation.values())
				if (a.name().equalsIgnoreCase(args[0]))
					animation = a;
			if (animation == null) 
				return false;
			
			Location location = player.getLocation().add(0, 1, 0);
			Rotation rot = new Rotation(player.getEyeLocation().getPitch(), player.getEyeLocation().getYaw(), 0);
			new Effect(location, rot, animation, true);
			return true;
		case "quality":
			if (args.length == 0) {
				MessageManager.sendPreparedMessage(LiberEffects.get(), player, EffectsMessages.USAGE);
				return true;
			}
			
			switch (args[0]) {
			case "low":
				ParticleManager.setLow((Player) sender);
				MessageManager.sendPreparedMessage(LiberEffects.get(), player, EffectsMessages.SET_LOW);
				return true;
			case "medium":
				ParticleManager.setMedium((Player) sender);
				MessageManager.sendPreparedMessage(LiberEffects.get(), player, EffectsMessages.SET_MEDIUM);
				return true;
			case "high":
				ParticleManager.setHigh((Player) sender);
				MessageManager.sendPreparedMessage(LiberEffects.get(), player, EffectsMessages.SET_HIGH);
				return true;
			default:
				MessageManager.sendPreparedMessage(LiberEffects.get(), player, EffectsMessages.USAGE);
				return true;
			}
		default:
			return false;
		}
	}
}
