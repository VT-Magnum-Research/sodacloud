package org.magnum.soda.example.maint;

import java.util.List;

import org.magnum.soda.Callback;
import org.magnum.soda.proxy.SodaAsync;

public interface MonitorZones {
public static final String SVC_NAME = "MonitorZone";
	
	public void addMonitorZone(MonitorZone r);
	public List<MonitorZone> getMonitorZones();
		
	@SodaAsync
	public void getMonitorZones(Callback<List<MonitorZone>> callback);	
	
	public void addListener(MonitorZoneListener l);
	public void removeListener(MonitorZoneListener l);
}
