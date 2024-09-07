package utils;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;

public class JSONUtils 
{
	/*
	 *  String
	 *  int
	 *  boolean
	 *  Map<String,String>, Map<String,Object>, Map<String,Integer>
	 *  List,  List<String> , List<Map> 
	 *  Object
	*/
    private static final ObjectMapper objectMapper = new ObjectMapper();

	 // Deserialization: Convert JSON Response to Java Object
    public static <T> T deserialize(Response response, Class<T> clazz) {
        try {
            return objectMapper.readValue(response.asString(), clazz);
        } catch (JsonProcessingException e) 
        {
            throw new RuntimeException("Failed to deserialize JSON response to " + clazz.getSimpleName(), e);
        }
    }
	
	
	public static <K,V> Map<K, V> getMap(Response resp,String jsonPath)
	{
		return resp.jsonPath().getMap(jsonPath);
	}
	
	public static <T> List<T> getList(Response resp,String jsonPath)
	{
		return resp.jsonPath().getList(jsonPath);
		
	}
	
	public static String getString(Response resp,String jsonPath)
	{
		String value=resp.jsonPath().getString(jsonPath);
		
		return value;
	}
	
	public static int getInt(Response resp,String jsonPath)
	{
		int value=resp.jsonPath().getInt(jsonPath);
		
		return value;
	}
	
	public static boolean getBoolean(Response resp,String jsonPath)
	{
		boolean value=resp.jsonPath().getBoolean(jsonPath);
		
		return value;
	}

}
