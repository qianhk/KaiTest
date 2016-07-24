package com.njnu.kai.test.menu.draglayout.util;

public interface Callback {
	void onBefore();

	boolean onRun();

	void onAfter(boolean b);
}
