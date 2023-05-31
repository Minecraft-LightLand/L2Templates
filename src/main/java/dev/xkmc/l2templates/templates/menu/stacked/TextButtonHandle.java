package dev.xkmc.l2templates.templates.menu.stacked;

import dev.xkmc.l2library.base.menu.SpriteManager;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;

public class TextButtonHandle {

	private final StackedRenderHandle parent;
	private final int y;

	private int x;


	protected TextButtonHandle(StackedRenderHandle parent, int x, int y) {
		this.parent = parent;
		this.x = x;
		this.y = y;
	}

	public CellEntry addButton(String btn) {
		SpriteManager.Rect r = parent.sm.getSide(btn);
		int y0 = y - (r.h + 1) / 2;
		GuiComponent.blit(parent.stack, x, y0, r.x, r.y, r.w, r.h);
		CellEntry c1 = new CellEntry(x, y0, r.w, r.h);
		x += r.w + StackedRenderHandle.BTN_X_OFFSET;
		return c1;
	}

	public void drawText(CellEntry cell, Component text) {
		int x0 = cell.x() + (cell.w() - parent.font.width(text) + 1) / 2;
		int y0 = cell.y() + (cell.h() + 1) / 2 - parent.font.lineHeight / 2;
		parent.textList.add(new TextEntry(parent.stack, text, x0, y0, parent.text_color));
	}

}
