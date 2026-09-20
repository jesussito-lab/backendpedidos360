package com.pedidos360.pedidos_service.controller;

import com.pedidos360.pedidos_service.dto.PedidoRequestDTO;
import com.pedidos360.pedidos_service.model.Pedido;
import com.pedidos360.pedidos_service.service.IPedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final IPedidoService pedidoService;
    private static final String DEFAULT_USER = "usuario.demo@duocuc.cl";

    public PedidoController(IPedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    private String resolverEmailUsuario(Jwt jwt) {
        if (jwt == null) {
            return DEFAULT_USER;
        }
        String email = jwt.getClaimAsString("preferred_username");
        if (email == null || email.isBlank()) {
            email = jwt.getSubject();
        }
        return (email != null && !email.isBlank()) ? email : DEFAULT_USER;
    }

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(
            @Valid @RequestBody PedidoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String usuarioEmail = resolverEmailUsuario(jwt);
        Pedido nuevoPedido = pedidoService.crearPedido(request, usuarioEmail);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> misPedidos(@AuthenticationPrincipal Jwt jwt) {
        String usuarioEmail = resolverEmailUsuario(jwt);
        return ResponseEntity.ok(pedidoService.listarPorUsuario(usuarioEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }
}