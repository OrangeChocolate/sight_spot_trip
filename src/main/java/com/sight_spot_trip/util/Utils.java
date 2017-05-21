package com.sight_spot_trip.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class Utils {

	
	@SuppressWarnings("unchecked")
	public static <T> T mergeObjects(T exist, T patch) {
		Class<?> clazz = exist.getClass();
		try {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (!field.getName().equals("serialVersionUID")) {
					field.setAccessible(true);
					Object valueMerge = field.get(patch);
					if(valueMerge != null && 
							(!(valueMerge instanceof Collection || valueMerge instanceof Map) 
									|| ((valueMerge instanceof Collection) && ((Collection)valueMerge).size() > 0)
									|| ((valueMerge instanceof Map) && ((Map)valueMerge).size() > 0))) {
						field.set(exist, valueMerge);
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return (T) exist;
	}
}
