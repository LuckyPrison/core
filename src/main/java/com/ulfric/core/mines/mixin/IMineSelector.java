package com.ulfric.core.mines.mixin;

import com.ulfric.lib.coffee.location.Vector;
import com.ulfric.lib.coffee.mixin.MixinLink;

public interface IMineSelector extends MixinLink {

	Vector cornerA();

	Vector cornerB();

	void setCornerA(Vector a);

	void setCornerB(Vector b);

	boolean isSelecting();

	void toggleSelecting();
}
