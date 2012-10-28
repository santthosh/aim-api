package com.appinmap.api.objects;

public enum Platform {
	iOS,Android;
	
	public static Platform GetPlatform(int identifier) {
		switch(identifier) {
			case 1:
				return Android;
			default:
				return iOS;
		}
	}
	
	public static int GetIntValue(Platform platform) {
		switch(platform) {
			case Android:
				return 1;
			default:
				return 0;
		}
	}
}
