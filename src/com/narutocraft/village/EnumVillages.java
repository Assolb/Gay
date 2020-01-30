package com.narutocraft.village;

public enum EnumVillages {

	KONOHA(new Konoha());
	
	private EnumVillages(Village village)
	{
		this.village = village;
	}
	
	public Village village;
}
