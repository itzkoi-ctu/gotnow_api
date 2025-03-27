package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.request.PaymentRequest;
import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.dto.response.OrderResponse;
import com.itzkoictu.gotNow.enums.OrderStatus;
import com.itzkoictu.gotNow.model.Order;
import com.itzkoictu.gotNow.service.order.OrderService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/user/{userId}/place-order")
    public ResponseEntity<ApiResponse> placeOrder(@PathVariable Long userId) {
        Order order = orderService.placeOrder(userId);
        OrderResponse orderResponse = orderService.convertToOrderResponse(order);
        return ResponseEntity.accepted().body(new ApiResponse<>(HttpStatus.ACCEPTED.value(), "Order placed successfully!", orderResponse));

    }

    @GetMapping("/user/{userId}/order")
    private ResponseEntity<ApiResponse> getUserOrder(@PathVariable Long userId) {
        List<OrderResponse> orderList = orderService.getUserOrders(userId);
        return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "all orders of user " + userId, orderList));


    }

    @GetMapping("/order/{orderId}/detail")
    private ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "Order details" , order));


    }



    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequest request) throws StripeException {
        String clientSecret = orderService.createPaymentIntent(request);
        return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
    }

    @GetMapping("/all/order")
    public ResponseEntity<ApiResponse> getAllOrders() {
        List<OrderResponse> orderList = orderService.getAllOrders();
        return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "all orders", orderList));


    }

    @PutMapping("/update/{orderId}/order")
    public ResponseEntity<ApiResponse> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus orderStatus) {
        Order order= orderService.changeOrderStatus(orderId,orderStatus);
        OrderResponse orderResponse= orderService.convertToOrderResponse(order);
        return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "Order updated successfully", orderResponse));

    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportToExcel() throws IOException {
        // Tạo workbook và sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");

        // Tạo hàng tiêu đề
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Username", "Order Day","Delivered Day", "Total Amount", "Order Status", "City", "Street"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Dữ liệu mẫu từ API (giả lập)
        String apiUrl = "http://localhost:8080/api/v1/orders/all/order";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
        List<Map<String, Object>> orders = (List<Map<String, Object>>) response.getBody().get("data");

        // Duyệt qua từng order và ghi vào file Excel
        int rowNum = 1;
        for (Map<String, Object> order : orders) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((Integer) order.get("id"));
            row.createCell(1).setCellValue((String) order.get("username"));
            row.createCell(2).setCellValue((String) order.get("orderDate"));
            row.createCell(3).setCellValue((String) order.get("deliveredDay"));

            row.createCell(4).setCellValue((Double) order.get("totalAmount"));
            row.createCell(5).setCellValue((String) order.get("orderStatus"));

            Map<String, Object> address = (Map<String, Object>) order.get("addressResponse");
            row.createCell(6).setCellValue((String) address.get("city"));
            row.createCell(7).setCellValue((String) address.get("street"));
        }

        // Tạo file tạm và ghi dữ liệu vào
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        // Trả về file Excel
        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
