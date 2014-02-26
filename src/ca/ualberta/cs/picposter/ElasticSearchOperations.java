package ca.ualberta.cs.picposter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.renderscript.Type;
import android.util.Log;
import ca.ualberta.cs.picposter.model.PicPostModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class ElasticSearchOperations
{
	public static void pushPicPostModel(final PicPostModel model){
		Thread thread = new Thread(){
			@Override
			public void run(){
				Gson gson = new Gson();
				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost("http://cmput301.softwareprocess.es:8080/testing/chunhan/");
				try{
					String jsonString = gson.toJson(model);
					request.setEntity(new StringEntity(jsonString));
					
					
					HttpResponse response = client.execute(request);
					Log.w("ElasticSearch", response.getStatusLine().toString());
					
					
					response.getStatusLine().toString();
					HttpEntity entity= response.getEntity();
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					
					String output = reader.readLine();
					while (output != null){
						Log.w("ElasticSearch", output);
						output = reader.readLine();
					}
				} catch (Exception e){
					e.printStackTrace();
				}	
			}
		};
		
		thread.start();
	}
	// Got the code from this website 
	// https://github.com/rayzhangcl/ESDemo/blob/master/ESDemo/src/ca/ualberta/cs/CMPUT301/chenlei/ElasticSearchResponse.java
	public static void searchPicPostModel(final String str) throws Exception{
		Thread thread = new Thread(){
			@Override
			public void run(){
				try{
				HttpGet searchRequest = new HttpGet("http://cmput301.softwareprocess.es:8080/testing/chunhan/_search?q=" +
						java.net.URLEncoder.encode(str,"UTF-8"));
				searchRequest.setHeader("Accept","application/json");
				Gson gson = new Gson();
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(searchRequest);
				String status = response.getStatusLine().toString();
				System.out.println(status);
				String json = getEntityContent(response);
			
				Type elasticSearchSearchResponseType = (Type) new TypeToken<ElasticSearchSearchResponse<PicPostModel>>(){}.getType();
				ElasticSearchSearchResponse<PicPostModel> esResponse = gson.fromJson(json, (java.lang.reflect.Type) elasticSearchSearchResponseType);
				System.err.println(esResponse);
				for (ElasticSearchResponse<PicPostModel> r : esResponse.getHits()) {
					PicPostModel picPosts = r.getSource();
					System.err.println(picPosts);
				}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}	
	// Got the code from this website 
	// https://github.com/rayzhangcl/ESDemo/blob/master/ESDemo/src/ca/ualberta/cs/CMPUT301/chenlei/ElasticSearchResponse.java
	public static String getEntityContent(HttpResponse response) throws Exception, IOException{
		BufferedReader br = new BufferedReader(
				new InputStreamReader((response.getEntity().getContent())));
		String output;
		System.err.println("Output from Server -> ");
		String json = "";
		while ((output = br.readLine()) != null) {
			System.err.println(output);
			json += output;
		}
		System.err.println("JSON:"+json);
		return json;
	}
}
