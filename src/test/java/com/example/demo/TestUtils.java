package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    public static void injectObject(Object target, String fieldName, Object toInject){
        boolean wasPrivate = false;
        try {
            //我想要取得那個物件，屬性名稱為fieldName的屬性(string? int? long?...)
            Field f = target.getClass().getDeclaredField(fieldName);
            //因為如果物件的屬性為private就要先把accessible改成true
            if(!f.isAccessible()){
                f.setAccessible(true);
                wasPrivate = true;
            }
            //如果確定Accessible可以改東西之後，我再來透過set來改寫object的內容
            f.set(target, toInject);
            //改寫完之後再把該屬性的accessible改回原本的false
            if(wasPrivate){
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static User CreateUser(){
        User user = new User();
        user.setId(1L);
        user.setUsername("shannon");
        user.setPassword("testPassword");
        user.setCart(CreateCart(user));
        return user;
    }
    public static Cart CreateCart(User user){
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);
        //建立list裡面有五個商品
        List<Item> items = CreateItems();
        //把商品加到items
        cart.setItems(CreateItems());
        //取得Item.class的方法getPrice進行加總
        cart.setTotal(items.stream().map(Item::getPrice).reduce(BigDecimal::add).get());
        return cart;
    }
    public static List<Item> CreateItems() {
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            //建立兩個新的商品, id分別為1,2,3,4,5
            items.add(CreateItem(i));
        }
        return items;
    }
    public static Item CreateItem(long id){
        Item item = new Item();
        item.setId(id);
        item.setPrice(BigDecimal.valueOf(id * 5));
        item.setName("Item" + item.getId());
        item.setDescription("Here is Description");
        return item;
    }
    public static List<UserOrder> CreateOrders(){
        List<UserOrder> orders = new ArrayList<>();
        for(int i = 0; i <=2; i++){
            UserOrder userOrder = new UserOrder();
            Cart cart = CreateCart(CreateUser());
            userOrder.setUser(CreateUser());
            userOrder.setId(Long.valueOf(i));
            userOrder.setItems(cart.getItems());
            userOrder.setTotal(cart.getTotal());
            orders.add(userOrder);
        }
        return orders;
    }
}
