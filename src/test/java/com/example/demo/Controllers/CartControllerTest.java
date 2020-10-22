package com.example.demo.Controllers;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.demo.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {
    @InjectMocks
    private CartController cartController;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setUp(){
        //id you called the method, then mock will do ... (thenReturn)
        when(userRepository.findByUsername("shannon")).thenReturn(CreateUser());
        when(itemRepository.findById(any())).thenReturn(Optional.of(CreateItem(1L)));
    }

    @Test
    public void AddToCart(){
        //add an item into the cart, input itemid and item quantity
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("shannon");
        cartRequest.setItemId(1L); //等等要建立item
        cartRequest.setQuantity(10);

        ResponseEntity<Cart> responseEntity = cartController.addTocart(cartRequest);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Cart cart = responseEntity.getBody();
        assertNotNull(cart);
        assertEquals("shannon", cart.getUser().getUsername());
        assertEquals(CreateItem(1L), cart.getItems().get(0));

        //建立一個新的Cart: Cart裡面屬性 id, List<item>, user, total
        Cart NewCart = CreateCart(CreateUser());
        assertEquals(NewCart.getItems().size() + cartRequest.getQuantity(), cart.getItems().size());

        //立刻創建一個cart
        Item item = CreateItem(cartRequest.getItemId());
        BigDecimal itemPrice = item.getPrice();
        assertEquals(item.getPrice().multiply(BigDecimal.valueOf(cartRequest.getQuantity())).add(NewCart.getTotal()), cart.getTotal());
    }

    @Test
    public void RemoveItemFromCart(){
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("shannon");
        cartRequest.setItemId(1);
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(cartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());

        Cart cart = cartResponseEntity.getBody();
        Cart CompareCart = CreateCart(CreateUser());

        assertNotNull(cart);
        Item item = CreateItem(cartRequest.getItemId());
        BigDecimal itemPrice =item.getPrice();
        BigDecimal expectTotal = CompareCart.getTotal().subtract(itemPrice.multiply(BigDecimal.valueOf(cartRequest.getQuantity())));

        assertEquals("shannon", cart.getUser().getUsername());
        assertEquals(CompareCart.getItems().size() - cartRequest.getQuantity(), cart.getItems().size());
        assertEquals(CreateItem(2), cart.getItems().get(0));
        assertEquals(expectTotal, cart.getTotal());

        //it only run for once time
        verify(cartRepository, times(1)).save(cart);
    }


}
