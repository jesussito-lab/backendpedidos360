package com.pedidos360.pedidos_service.repository;

import com.pedidos360.pedidos_service.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioEmail(String usuarioEmail);
    List<Pedido> findByUsuarioEmailOrderByFechaCreacionDesc(String usuarioEmail);
}