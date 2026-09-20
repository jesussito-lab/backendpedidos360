package com.pedidos360.pedidos_service.service;

import com.pedidos360.pedidos_service.dto.ItemPedidoDTO;
import com.pedidos360.pedidos_service.dto.PedidoRequestDTO;
import com.pedidos360.pedidos_service.model.DetallePedido;
import com.pedidos360.pedidos_service.model.Pedido;
import com.pedidos360.pedidos_service.model.Producto;
import com.pedidos360.pedidos_service.repository.PedidoRepository;
import com.pedidos360.pedidos_service.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoServiceImpl implements IPedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public Pedido crearPedido(PedidoRequestDTO request, String usuarioEmail) {
        Pedido pedido = Pedido.builder()
                .usuarioEmail(usuarioEmail)
                .fechaCreacion(LocalDateTime.now())
                .estado("PENDIENTE")
                .total(BigDecimal.ZERO)
                .detalles(new ArrayList<>())
                .build();

        BigDecimal acumuladorTotal = BigDecimal.ZERO;

        for (ItemPedidoDTO item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto con ID " + item.getProductoId() + " no encontrado"));

            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            acumuladorTotal = acumuladorTotal.add(subtotal);

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotal(subtotal)
                    .build();

            pedido.getDetalles().add(detalle);
        }

        pedido.setTotal(acumuladorTotal);
        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorUsuario(String usuarioEmail) {
        return pedidoRepository.findByUsuarioEmail(usuarioEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }
}