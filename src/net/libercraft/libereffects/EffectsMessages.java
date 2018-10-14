package net.libercraft.libereffects;

import net.libercraft.libercore.managers.MessageManager.PreparedMessage;

public enum EffectsMessages implements PreparedMessage {
	USAGE,
	SET_LOW,
	SET_MEDIUM,
	SET_HIGH;

	@Override
	public String getMessage() {
		switch (this) {
		case SET_LOW:
			return "Quality set to low";
		case SET_MEDIUM:
			return "Quality set to medium";
		case SET_HIGH:
			return "Quality set to high";
		case USAGE:
			return "Usage: /quality <option>. Available options: low, medium, high";
		default:
			return null;
		}
	}
}