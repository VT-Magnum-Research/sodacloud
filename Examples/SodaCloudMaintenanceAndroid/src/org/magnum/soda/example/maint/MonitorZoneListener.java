package org.magnum.soda.example.maint;

import org.magnum.soda.proxy.SodaAsync;

public interface MonitorZoneListener {
	@SodaAsync
	public void monitorzoneAdded(MonitorZone m);
}
