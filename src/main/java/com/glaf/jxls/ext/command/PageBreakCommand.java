package com.glaf.jxls.ext.command;

import org.jxls.command.AbstractCommand;
import org.jxls.common.CellRef;
import org.jxls.common.Context;
import org.jxls.common.Size;

public class PageBreakCommand extends AbstractCommand {

	@Override
	public Size applyAt(CellRef cellRef, Context context) {
		return null;
	}

	@Override
	public String getName() {
		return "pageBreak";
	}

}
