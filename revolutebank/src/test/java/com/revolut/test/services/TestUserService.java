package com.revolut.test.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.revolut.fundtransfer.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;


/**
 * Integration testing
 */
public class TestUserService extends TestService {

     /*
     TC D2 Positive Category = UserService
     Scenario: test get all users
               return 200 OK
      */
    @Test
    public void testGetAllUsers() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/all").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        User[] users = mapper.readValue(jsonString, User[].class);
        assertTrue(users.length > 0);
    }

    /*
        TC D3 Positive Category = UserService
        Scenario: Create user using JSON
                  return 200 OK
     */
    @Test
    public void testCreateUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/create").build();
        User user = new User("george", "george@gmail.com");
        String jsonInString = mapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
    }

    /*
        TC D4 Negative Category = UserService
        Scenario: Create user already existed using JSON
                  return 400 BAD REQUEST
    */
    @Test
    public void testCreateExistingUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/create").build();
        User user = new User("test1", "test1@gmail.com");
        String jsonInString = mapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 400);

    }

    /*
     TC D5 Positive Category = UserService
     Scenario: Update Existing User using JSON provided from client
               return 200 OK
     */
    @Test
    public void testUpdateUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/2").build();
        User user = new User(2L, "test1", "test1123@gmail.com");
        String jsonInString = mapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
    }


    /*
    TC D6 Negative Category = UserService
    Scenario: Update non existed User using JSON provided from client
              return 404 NOT FOUND
    */
    @Test
    public void testUpdateNonExistingUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/100").build();
        User user = new User(2L, "test1", "test1123@gmail.com");
        String jsonInString = mapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 404);
    }

    /*
     TC D7 Positive Category = UserService
     Scenario: test delete user
                return 200 OK
    */
    @Test
    public void testDeleteUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/3").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
    }


    /*
    TC D8 Negative Category = UserService
    Scenario: test delete non-existed user
              return 404 NOT FOUND
   */
    @Test
    public void testDeleteNonExistingUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/300").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 404);
    }


}
