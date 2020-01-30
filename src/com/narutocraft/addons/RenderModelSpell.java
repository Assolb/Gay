package com.narutocraft.addons;

import org.bukkit.entity.Player;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.network.S1RenderModelSpell;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.MagicConfig;

public class RenderModelSpell extends Spell{

	boolean stop = getConfigBoolean("stop", false);
	String model = getConfigString("model", null);
	
	public RenderModelSpell(MagicConfig config, String spellName) {
		super(config, spellName);
	}

	@Override
	public boolean canCastByCommand() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canCastWithItem() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PostCastAction castSpell(Player p, SpellCastState state, float arg2, String[] arg3) {
		if(state == SpellCastState.NORMAL)
		{
			NarutoCraft1.handler.sendPacketToPlayer(p, new S1RenderModelSpell(model, stop ? Integer.toString(1) : Integer.toString(0)));
			return PostCastAction.HANDLE_NORMALLY;
		}
		return PostCastAction.ALREADY_HANDLED;
	}
}
