package com.example.demo.Controllers;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private static Logger log = LoggerFactory.getLogger("UserController.class");

    private UserController userController;
    //他會產生mock object
    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
    }
    @Test
    public void create_user_happy_path() throws Exception{
        when(encoder.encode("testpassword")).thenReturn("hashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("shannon");
        r.setPassword("testpassword");
        r.setConfirmPassword("testpassword");

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        log.debug("hi");

        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("shannon", user.getUsername());
        assertEquals("hashed", user.getPassword());
    }

    @Test
    public void find_user_by_id(){
        Long id = 1L;

        User user = new User();
        user.setUsername("shannon");
        user.setPassword("testPassword");
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        ResponseEntity<User> responseEntity = userController.findById(id);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User user2 = responseEntity.getBody();
        assertNotNull(user2);
        assertEquals(id, Long.valueOf(user2.getId()));
        assertEquals("shannon", user2.getUsername());
        assertEquals("testPassword", user2.getPassword());
    }
    @Test
    public void find_user_by_name(){

        User user = new User();
        user.setUsername("shannon");
        user.setPassword("testPassword");
        user.setId(1L);

        when(userRepository.findByUsername("shannon")).thenReturn(user);
        ResponseEntity<User> responseEntity = userController.findByUserName("shannon");

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User user2 = responseEntity.getBody();
        assertNotNull(user2);
        assertEquals(1L, user2.getId());
        assertEquals("shannon", user2.getUsername());
        assertEquals("testPassword", user2.getPassword());
    }
}
