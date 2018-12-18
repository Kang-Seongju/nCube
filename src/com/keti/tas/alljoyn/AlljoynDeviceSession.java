package com.keti.tas.alljoyn;

import org.alljoyn.bus.SessionListener;

public class AlljoynDeviceSession extends SessionListener {
	@Override
	public void sessionLost(int sessionId, int reason) {
		// TODO Auto-generated method stub
		super.sessionLost(sessionId, reason);
	}
}
