package org.magnum.soda.sodaclient2;

import java.util.ArrayList;
import java.util.List;

import org.magnum.soda.proxy.SodaAsync;

public interface ReportsListener {
	@SodaAsync
	public void reportAdded(Report r);
	@SodaAsync
	public void reportchanged(Report r);
}
