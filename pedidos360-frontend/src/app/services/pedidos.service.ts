import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
}

export interface DetallePedido {
  id?: number;
  producto: Producto;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface Pedido {
  id?: number;
  usuarioEmail: string;
  fechaCreacion: string;
  estado: string;
  total: number;
  detalles: DetallePedido[];
}

export interface PedidoRequest {
  items: { productoId: number; cantidad: number }[];
}

@Injectable({
  providedIn: 'root'
})
export class PedidosService {
  private apiUrl = environment.apiGatewayUrl;

  constructor(private http: HttpClient) {}

  getProductos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}/productos`);
  }

  getMisPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/pedidos`);
  }

  crearPedido(pedido: PedidoRequest): Observable<Pedido> {
    return this.http.post<Pedido>(`${this.apiUrl}/pedidos`, pedido);
  }
}