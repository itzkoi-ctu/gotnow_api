package com.itzkoictu.gotNow.service.order;


import com.itzkoictu.gotNow.dto.request.PaymentRequest;
import com.itzkoictu.gotNow.dto.response.AddressResponse;
import com.itzkoictu.gotNow.dto.response.ImageResponse;
import com.itzkoictu.gotNow.dto.response.OrderItemResponse;
import com.itzkoictu.gotNow.dto.response.OrderResponse;
import com.itzkoictu.gotNow.enums.OrderStatus;
import com.itzkoictu.gotNow.model.*;
import com.itzkoictu.gotNow.repository.OrderRepository;
import com.itzkoictu.gotNow.repository.ProductRepository;
import com.itzkoictu.gotNow.service.address.AddressService;
import com.itzkoictu.gotNow.service.cart.CartService;
import com.itzkoictu.gotNow.service.user.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    @Transactional
    public Order placeOrder(Long userId){
        Cart cart= cartService.getCartByUserId(userId);
        Order order= createOrder(cart);
        List<OrderItem> orderItemList= createOrderItems(order, cart);
        order.setItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalAmount(orderItemList));
        Order savedOrder= orderRepository.save(order);
        System.out.println("Order placed with id: "+ order.getId());
        cartService.clearCart(cart.getId());
        return savedOrder;
    }
    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems){
        return orderItems.stream()
                .map(item -> item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Order createOrder(Cart cart){
        return Order.builder()
                .user(cart.getUser())
                .orderDate(LocalDate.now())
                .orderStatus(OrderStatus.PENDING)
                .build();
    }
    private List<OrderItem> createOrderItems(Order order, Cart cart){
        return cart.getItems().stream().map(cartItem -> {
            Product product= cartItem.getProduct();
            product.setInventory(product.getInventory() - cartItem.getQuantity());
            productRepository.save(product);
            return new OrderItem(
                    order,
                    product,
                    cartItem.getUnitPrice(),
                    cartItem.getQuantity()
            );
        }).toList();



    }
    public List<OrderResponse> getUserOrders(Long userId ){
        List<Order> orderList= orderRepository.findByUserId(userId);
        System.out.println("Order List: " + orderList);

        return orderList.stream().map(this::convertToOrderResponse).toList();
    }


public OrderResponse convertToOrderResponse(Order order) {
    OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);


    Set<OrderItemResponse> responses = order.getItems().stream()
            .filter(orderItem -> {
                if (orderItem.getProduct() == null) {
                    System.out.println("⚠ Cảnh báo: OrderItem có Product = null!");
                    return false; // Bỏ qua OrderItem bị null product
                }
                return true;
            })
            .map(this::fromOrderItem)
            .collect(Collectors.toSet());
    orderResponse.setItems(responses);
    orderResponse.setUsername(order.getUser().getFirstName()+" "+ order.getUser().getLastName());

    return orderResponse;
}


    public String createPaymentIntent(PaymentRequest request) throws StripeException {
        long amountInSmallestUnit = Math.round(request.getAmount() *100);

        PaymentIntent intent= PaymentIntent.create(
                PaymentIntentCreateParams.builder()
                        .setAmount(amountInSmallestUnit)
                        .setCurrency(request.getCurrency())
                        .addPaymentMethodType("card")
                        .build());
        return intent.getClientSecret();
    }
    public List<OrderResponse> convertToOrderResponses(List<Order> orderList){

        return orderList.stream().map(this::convertToOrderResponse).toList();
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orderList = orderRepository.findAll();
        System.out.println("Order list size: " + orderList.size());

        if (orderList.isEmpty()) {
            System.out.println("⚠ Không có đơn hàng nào trong database!");
            return Collections.emptyList(); // Trả về danh sách rỗng, tránh lỗi null
        }

        try {
            List<OrderResponse> orderResponses = convertToOrderResponses(orderList);
            System.out.println("✅ Converted OrderResponses: " + orderResponses);
            return orderResponses;
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi chuyển đổi danh sách OrderResponses:");
            e.printStackTrace(); // In lỗi ra console
            return Collections.emptyList();
        }
    }



    public Order changeOrderStatus(Long orderId, OrderStatus orderStatus){

        Order order= orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));
        order.setOrderStatus(orderStatus);
        if(orderStatus==OrderStatus.DELIVERED){
            order.setDeliveredDay(LocalDateTime.now());
        }
        return orderRepository.save(order);


    }

    public OrderResponse getOrderById(Long orderId) {
        Order order= orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        OrderResponse orderResponse = convertToOrderResponse(order);
        orderResponse.setUsername(order.getUser().getFirstName()+" "+ order.getUser().getLastName());
        return  orderResponse;
    }


    public OrderItemResponse fromOrderItem(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setProductId(orderItem.getProduct().getId());
        response.setProductName(orderItem.getProduct().getName());
        response.setProductBrand(orderItem.getProduct().getBrand());
        response.setQuantity(orderItem.getQuantity());
        response.setPrice(orderItem.getPrice());

        // Lấy danh sách URL ảnh từ product
        List<ImageResponse> imageResponses = orderItem.getProduct().getImages().stream()
                .map(image -> new ImageResponse(image.getId(), image.getFileName(), image.getDownloadUrl())) // Giả sử Image có trường url
                .toList();
        response.setImages(imageResponses);

        return response;
    }

    public Set<OrderItemResponse> convertToOrderItemResponse(Set<OrderItem> orderItems){
        return orderItems.stream().map(this::fromOrderItem).collect(Collectors.toSet());
    }
}
