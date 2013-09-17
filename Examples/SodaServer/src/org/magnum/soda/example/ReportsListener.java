package org.magnum.soda.example;

import java.util.ArrayList;
import java.util.List;

import org.magnum.soda.proxy.SodaAsync;

public interface ReportsListener {
	@SodaAsync
	public void reportAdded(Report r);
	@SodaAsync
	public void reportchanged(Report r);
}
//6