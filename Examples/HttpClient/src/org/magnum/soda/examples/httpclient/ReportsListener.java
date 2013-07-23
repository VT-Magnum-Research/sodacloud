package org.magnum.soda.examples.httpclient;

import java.util.ArrayList;
import java.util.List;

public interface ReportsListener {

	public void reportAdded(Report r);

	public void reportchanged(Report r);
}
