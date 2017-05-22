package com.sight_spot_trip.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

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
	

	
	public static Stream<String> getResourceContentStream(Resource resources) {
		BufferedReader reader = null;
		try {
			List<String> lines = new ArrayList<String>();
			reader = new BufferedReader(new InputStreamReader(resources.getInputStream(), StandardCharsets.UTF_8));
			String line = reader.readLine();
			while(!StringUtils.isEmpty(line)){
				lines.add(line);
			    line = reader.readLine();
			}
			return lines.stream();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
