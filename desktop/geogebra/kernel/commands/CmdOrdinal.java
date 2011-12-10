package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/*
 * Max[ <Number>, <Number> ]
 */
public class CmdOrdinal extends CommandProcessorDesktop {

	public CmdOrdinal(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoNumeric()) ) 
			{
				GeoElement[] ret = { 
						kernel.Ordinal(c.getLabel(),
						(GeoNumeric) arg[0] ) };
				return ret;
						
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
