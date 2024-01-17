package com.iogamegraalvmdemo.gameserverdemo.graal;
import java.util.HashSet;
import java.util.Set;
public class GameActionDocWrapper {
	public static final Set<Class<?>> classSet = new HashSet<>(){{
		add(com.iogamegraalvmdemo.gameserverdemo.action.GameActionController.class);
		add(com.iogamegraalvmdemo.gameserverdemo.BroadcastHelper.class);
	}};
}
