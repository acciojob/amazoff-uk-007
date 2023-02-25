package com.driver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired

    private DeliveryPartnerRepository deliveryPartnerRepository;
    private Map<String,String> assignedOrders;

    public OrderController(OrderRepository orderRepository, DeliveryPartnerRepository deliveryPartnerRepository) {
        this.orderRepository = orderRepository;
        this.deliveryPartnerRepository = deliveryPartnerRepository;
        assignedOrders = new HashMap<>();
    }


    @PostMapping("/add-order")
    public ResponseEntity<String> addOrder(@RequestBody Order order){
        orderRepository.save(order);

        return new ResponseEntity<>("New order added successfully", HttpStatus.CREATED);
    }

    @PostMapping("/add-partner/{partnerId}")
    public ResponseEntity<String> addPartner(@PathVariable String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        deliveryPartnerRepository.save(deliveryPartner);

        return new ResponseEntity<>("New delivery partner added successfully", HttpStatus.CREATED);
    }

    @PutMapping("/add-order-partner-pair")
    public ResponseEntity<String> addOrderPartnerPair(@RequestParam String orderId, @RequestParam String partnerId){
        assignedOrders.put(orderId, partnerId);
        DeliveryPartner partner = deliveryPartnerRepository.findById(partnerId).orElse(null);
        if (partner != null) {
            partner.setNumberOfOrders(partner.getNumberOfOrders() + 1);
            deliveryPartnerRepository.save(partner);
        }

        //This is basically assigning that order to that partnerId
        return new ResponseEntity<>("New order-partner pair added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/get-order-by-id/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId){

        Order order= null;
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //order should be returned with an orderId.

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/get-partner-by-id/{partnerId}")
    public ResponseEntity<DeliveryPartner> getPartnerById(@PathVariable String partnerId){

        DeliveryPartner deliveryPartner = null;
        if (deliveryPartner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //deliveryPartner should contain the value given by partnerId

        return new ResponseEntity<>(deliveryPartner, HttpStatus.CREATED);
    }

    @GetMapping("/get-order-count-by-partner-id/{partnerId}")
    public ResponseEntity<Integer> getOrderCountByPartnerId(@PathVariable String partnerId){

        //Integer orderCount = 0;
        Integer orderCount = OrderService.getOrderCountByPartnerId(partnerId);

        //orderCount should denote the orders given by a partner-id

        return new ResponseEntity<>(orderCount, HttpStatus.CREATED);
    }

    @GetMapping("/get-orders-by-partner-id/{partnerId}")
    public ResponseEntity<List<String>> getOrdersByPartnerId(@PathVariable String partnerId){
        //List<String> orders = null;
        List<Order> orders = orderRepository.findByPartnerId(partnerId);
        List<String> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());

        //orders should contain a list of orders by PartnerId
        ResponseEntity<T> tResponseEntity = new ResponseEntity<>(orders, HttpStatus.CREATED);

        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    @GetMapping("/get-all-orders")
    public ResponseEntity<List<String>> getAllOrders(){
        //List<String> orders = null;
        List<String> orders = new ArrayList<>();
        List<Order> orderEntities = orderRepository.findAll();
        for (Order orderEntity : orderEntities) {
            orders.add(orderEntity.getName());
        }

        //Get all orders
        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    @GetMapping("/get-count-of-unassigned-orders")
    public ResponseEntity<Integer> getCountOfUnassignedOrders(){
        Integer countOfOrders = 0;

        //Count of orders that have not been assigned to any DeliveryPartner
        countOfOrders = orderRepository.countByDeliveryPartnerIdIsNull();

        return new ResponseEntity<>(countOfOrders, HttpStatus.CREATED);
    }

    @GetMapping("/get-count-of-orders-left-after-given-time/{partnerId}")
    public ResponseEntity<Integer> getOrdersLeftAfterGivenTimeByPartnerId(@PathVariable String time, @PathVariable String partnerId){

        Integer countOfOrders = 0;
        Integer orderCount = orderService.getOrderCountByPartnerId(partnerId);

        //countOfOrders that are left after a particular time of a DeliveryPartner

        return new ResponseEntity<>(countOfOrders, HttpStatus.CREATED);
    }

    @GetMapping("/get-last-delivery-time/{partnerId}")
    public ResponseEntity<String> getLastDeliveryTimeByPartnerId(@PathVariable String partnerId){
        //String time = null;
        String time = DeliveryService.getLastDeliveryTime(partnerId);

        //Return the time when that partnerId will deliver his last delivery order.

        return new ResponseEntity<>(time, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-partner-by-id/{partnerId}")
    public ResponseEntity<String> deletePartnerById(@PathVariable String partnerId){
        orderService.deletePartnerById(partnerId);

        //Delete the partnerId
        //And push all his assigned orders to unassigned orders.

        return new ResponseEntity<>(partnerId + " removed successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-order-by-id/{orderId}")
    public ResponseEntity<String> deleteOrderById(@PathVariable String orderId){
        orderService.deleteOrderById(orderId);

        //Delete an order and also
        // remove it from the assigned order of that partnerId

        return new ResponseEntity<>(orderId + " removed successfully", HttpStatus.CREATED);
    }
}
