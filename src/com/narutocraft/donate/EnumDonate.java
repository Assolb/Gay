package com.narutocraft.donate;

public enum EnumDonate {
	
	// Ultimate
	Rinnegan(1500),
	SageModeOfSnake(1000),
	
	// Jutsu
	Rasengan(150),
	LGBT(400),
	
	// Kanons of Gods
	Hazan(Integer.MAX_VALUE),
	Apple(Integer.MIN_VALUE),
	Circulations(Integer.MIN_VALUE + Integer.MAX_VALUE),
	
	// Clans
	Uchiha(400),
	Hyuga(300),
	Uzumaki(300),
	Hozuki(300),
	Chinoike(400),
	Kaguya(300),
	
	// Elements
	LavaElement(250),
	PaperElement(250),
	CrystalElement(200),
	IceElement(250),
	SteamElement(200),
	WoodElement(300),
	DustElement(1000),
	ExplosionElement(300),
	StormElement(400),
	AbsoluteExplosionElement(800),
	AbsoluteIronSandElement(650),
	AbsoluteGoldenSandElement(650),
	AbsoluteLavaElement(700),
	AbsoluteIceElement(650),
	AbsoluteCrystalElement(650),
	AbsoluteSteamElement(650),
	AbsoluteWoodElement(1000),
	AbsoluteStormElement(1500),
	AbsolutePaperElement(650),
	
	// Biju
	OneTail(300),
	TwoTails(350),
	ThreeTails(400),
	FourTails(450),
	FiveTais(500),
	SixTails(550),
	SevenTails(600),
	EightTails(650),
	NineTails(700),
	TenTails(1500),
	
	// Swords of Sworders
	Samehada(250),
	Kubikiriboch(220),
	Nuibari(220),
	Kabutowari(220),
	Kiba(250),
	Hiramikarei(250),
	Shibuki(250),
	Kusanagi(150),
	
	// Tests
	CBT(0);
	
	public int power;

	private EnumDonate(int power) 
	{
		this.power = power;
	}
	
	
}
