/*
    Copyright 2017-2018 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.universalgcodesender.gcode.processors;

import com.willwinder.universalgcodesender.gcode.GcodePreprocessorUtils;
import com.willwinder.universalgcodesender.gcode.GcodeState;
import com.willwinder.universalgcodesender.gcode.util.GcodeParserException;
import com.willwinder.universalgcodesender.i18n.Localization;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Moves to location on a tool change, awaits you to change your tooling.. then moves to your probe measure location.
 *
 * @author gareth buckley - lolsifer@github
 */
public class ToolChange implements CommandProcessor {
	
    private final String toolChangeMoveToZ;
	private final String toolChangeMoveToXY;
	private final String toolChangePause;
	
	private final String toolMeasureMoveToProbe;
	private final String toolMeasureDoProbing;

	// Contains a Tool change T[#]M6. M6 is tool change, # is tool number, as designated by the postprocessor.
	// Example: T5M6
	
    private Pattern toolChangePattern = Pattern.compile(".*[tT][0-9]+[mM][0-9]");

    public ToolChange(double absoluteX, double absoluteY, double absoluteZ, String toolMeasureMoveToProbe, String toolMeasureDoProbing) {
        this.toolChangeMoveToZ = String.format(Locale.ROOT, "G53G0Z%.2f", absoluteZ);
        this.toolChangeMoveToXY = String.format(Locale.ROOT, "G53G0X%.2fY%.2f", absoluteX, absoluteY);
        this.toolChangePause = String.format(Locale.ROOT, "M0");
		
		this.toolMeasureMoveToProbe = toolMeasureMoveToProbe;
		this.toolMeasureDoProbing = toolMeasureDoProbing;
    }

    @Override
    public List<String> processCommand(String command, GcodeState state) throws GcodeParserException {
        String noComments = GcodePreprocessorUtils.removeComment(command);
		
        if (toolChangePattern.matcher(noComments).matches()) {
            return Arrays.asList(toolChangeMoveToZ, toolChangeMoveToXY, toolChangePause, toolMeasureMoveToProbe, toolChangePause, toolMeasureDoProbing, toolChangeMoveToZ);
        }
		
        return Collections.singletonList(command);
    }

    @Override
    public String getHelp() {
        return Localization.getString("sender.help.tool-change");
    }
}
