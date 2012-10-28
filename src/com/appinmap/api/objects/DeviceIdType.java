package com.appinmap.api.objects;

public enum DeviceIdType {
	UDID, ODIN1, IdentifierForVendor, IdentifierForAdvertiser;
	
	public static DeviceIdType GetDeviceIdType(int identifier) {
		switch(identifier) {
			case 1:
				return ODIN1;
			case 2:
				return IdentifierForVendor;
			case 3:
				return IdentifierForAdvertiser;
			default:
				return UDID;
		}
	}
}
