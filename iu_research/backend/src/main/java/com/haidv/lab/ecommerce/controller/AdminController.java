package com.haidv.lab.ecommerce.controller;

import com.haidv.lab.ecommerce.dto.GraphQLRequestDto;
import com.haidv.lab.ecommerce.dto.order.OrderResponseDto;
import com.haidv.lab.ecommerce.dto.perfume.PerfumeRequestDto;
import com.haidv.lab.ecommerce.dto.perfume.PerfumeResponseDto;
import com.haidv.lab.ecommerce.dto.user.UserRequestDto;
import com.haidv.lab.ecommerce.dto.user.UserResponseDto;
import com.haidv.lab.ecommerce.exception.InputFieldException;
import com.haidv.lab.ecommerce.mapper.OrderMapper;
import com.haidv.lab.ecommerce.mapper.PerfumeMapper;
import com.haidv.lab.ecommerce.mapper.UserMapper;
import com.haidv.lab.ecommerce.service.graphql.GraphQLProvider;
import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserMapper userMapper;
    private final PerfumeMapper perfumeMapper;
    private final OrderMapper orderMapper;
    private final GraphQLProvider graphQLProvider;

    @PostMapping("/add")
    public ResponseEntity<PerfumeResponseDto> addPerfume(@RequestPart(name = "file", required = false) MultipartFile file,
                                                         @RequestPart("perfume") @Valid PerfumeRequestDto perfume,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        } else {
            return ResponseEntity.ok(perfumeMapper.savePerfume(perfume, file));
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<PerfumeResponseDto> updatePerfume(@RequestPart(name = "file", required = false) MultipartFile file,
                                                            @RequestPart("perfume") @Valid PerfumeRequestDto perfume,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        } else {
            return ResponseEntity.ok(perfumeMapper.savePerfume(perfume, file));
        }
    }

    @DeleteMapping("/delete/{perfumeId}")
    public ResponseEntity<List<PerfumeResponseDto>> deletePerfume(@PathVariable(value = "perfumeId") Long perfumeId) {
        return ResponseEntity.ok(perfumeMapper.deleteOrder(perfumeId));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderMapper.findAllOrders());
    }

    @PostMapping("/order")
    public ResponseEntity<List<OrderResponseDto>> getUserOrdersByEmail(@RequestBody UserRequestDto user) {
        return ResponseEntity.ok(orderMapper.findOrderByEmail(user.getEmail()));
    }

    @DeleteMapping("/order/delete/{orderId}")
    public ResponseEntity<List<OrderResponseDto>> deleteOrder(@PathVariable(value = "orderId") Long orderId) {
        return ResponseEntity.ok(orderMapper.deleteOrder(orderId));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userMapper.findUserById(userId));
    }


    @GetMapping("/user/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userMapper.findAllUsers());
    }

    @PostMapping("/graphql/user")
    public ResponseEntity<ExecutionResult> getUserByQuery(@RequestBody GraphQLRequestDto request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping("/graphql/user/all")
    public ResponseEntity<ExecutionResult> getAllUsersByQuery(@RequestBody GraphQLRequestDto request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping("/graphql/orders")
    public ResponseEntity<ExecutionResult> getAllOrdersQuery(@RequestBody GraphQLRequestDto request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping("/graphql/order")
    public ResponseEntity<ExecutionResult> getUserOrdersByEmailQuery(@RequestBody GraphQLRequestDto request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }
}
