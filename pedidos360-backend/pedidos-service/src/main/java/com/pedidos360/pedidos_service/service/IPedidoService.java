package com.pedidos360.pedidos_service.service;

import com.pedidos360.pedidos_service.dto.PedidoRequestDTO;
import com.pedidos360.pedidos_service.model.Pedido;

import java.util.List;

public interface IPedidoService {
    Pedido crearPedido(PedidoRequestDTO request, String usuarioEmail);
    List<Pedido> listarTodos();
    List<Pedido> listarPorUsuario(String usuarioEmail);
    Pedido buscarPorId(Long id);
}