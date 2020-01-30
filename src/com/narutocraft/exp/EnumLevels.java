package com.narutocraft.exp;

public enum EnumLevels {
	
	//0-10
	Zero(0),
	One(100),
	Two(400),
	Three(700),
	Four(1000),
	Five(1300),
	Six(1600),
	Seven(1900),
	Eight(2200),
	Nine(2500),
	OneZero(2800),
	
	//11-20
	OneOne(6800),
	OneTwo(7800),
	OneThree(8800),
	OneFour(9800),
	OneFive(10800),
	OneSix(11800),
	OneSeven(12800),
	OneEight(13800),
	OneNine(14800),
	TwoZero(15800),
	
	//21-30
	TwoOne(25000),
	TwoTwo(28000),
	TwoThree(31000),
	TwoFour(34000),
	TwoFive(37000),
	TwoSix(40000),
	TwoSeven(43000),
	TwoEight(47000),
	TwoNine(50000),
	ThreeZero(53000),
	
	//31-40
	ThreeOne(80000),
	ThreeTwo(90000),
	ThreeThree(100000),
	ThreeFour(110000),
	ThreeFive(120000),
	ThreeSix(130000),
	ThreeSeven(140000),
	ThreeEight(150000),
	ThreeNine(160000),
	FourZero(170000),
	
	//41-50
	FourOne(220000),
	FourTwo(250000),
	FourThree(280000),
	FourFour(310000),
	FourFive(340000),
	FourSix(370000),
	FourSeven(400000),
	FourEight(430000),
	FourNine(460000),
	FiveZero(490000),
	
	//51-60
	FiveOne(600000),
	FiveTwo(650000),
	FiveThree(700000),
	FiveFour(750000),
	FiveFive(800000),
	FiveSix(850000),
	FiveSeven(900000),
	FiveEight(950000),
	FiveNine(1000000),
	SixZero(1050000),
	
	//61-70
	SixOne(1350000),
	SixTwo(1450000),
	SixThree(1550000),
	SixFour(1650000),
	SixFive(1750000),
	SixSix(1850000),
	SixSeven(1950000),
	SixEight(2050000),
	SixNine(2150000),
	SevenZero(2250000),
	
	//71-80
	SevenOne(3000000),
	SevenTwo(3300000),
	SevenThree(3600000),
	SevenFour(3900000),
	SevenFive(4200000),
	SevenSix(4500000),
	SevenSeven(4800000),
	SevenEight(5100000),
	SevenNine(5400000),
	EightZero(5700000),
	
	//81-90
	EightOne(8700000),
	EightTwo(9500000),
	EightThree(10300000),
	EightFour(11100000),
	EightFive(11900000),
	EightSix(12700000),
	EightSeven(13500000),
	EightEight(14300000),
	EightNine(15100000),
	NineZero(15900000),
	
	//91-100
	NineOne(30000000),
	NineTwo(35000000),
	NineThree(40000000),
	NineFour(45000000),
	NineFive(50000000),
	NineSix(55000000),
	NineSeven(60000000),
	NineEight(65000000),
	NineNine(70000000),
	OneZeroZero(85000000),
	
	//ADMIN
	OneZeroOne(Integer.MAX_VALUE);
	
	private int exp;
	
	private EnumLevels(int exp)
	{
		this.exp = exp;
	}
	
	public int getExp()
	{
		return exp;
	}
}
