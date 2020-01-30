package com.narutocraft.util;

import java.util.ArrayList;
import java.util.List;

public class ListHelper<K extends Object> 
{
	public List<K> getList(K... k)
	{
		List<K> list = new ArrayList<K>();
		for(K j : k)
		{
			list.add(j);
		}
		
		return list;
	}
	
	public List<K> addKtoList(List<K> list, K... k)
	{
		List<K> list1 = list;
		for(K l : k)
		{
			list1.add(l);
		}
		
		return list1;
	}
}
