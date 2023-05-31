package dev.xkmc.l2templates.templates.menu.scroller;

public interface ScrollerScreen {

	ScrollerMenu getMenu();

	int getGuiLeft();

	int getGuiTop();

	void scrollTo(int i);
}
