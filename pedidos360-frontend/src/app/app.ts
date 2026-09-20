import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MSAL_GUARD_CONFIG, MsalGuardConfiguration, MsalService, MsalBroadcastService } from '@azure/msal-angular';
import { InteractionStatus, RedirectRequest } from '@azure/msal-browser';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { FormsModule } from '@angular/forms';
import { PedidosService, Producto, Pedido } from './services/pedidos.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit, OnDestroy {
  isIframe = false;
  loginDisplay = true; // Activo por defecto para omitir bloqueo
  usuario: string = 'Usuario Demo / EP2';

  productos: Producto[] = [];
  misPedidos: Pedido[] = [];
  cantidades: { [key: number]: number } = {};

  private readonly _destroying$ = new Subject<void>();

  constructor(
    @Inject(MSAL_GUARD_CONFIG) private msalGuardConfig: MsalGuardConfiguration,
    private authService: MsalService,
    private msalBroadcastService: MsalBroadcastService,
    private pedidosService: PedidosService
  ) {}

  ngOnInit(): void {
    this.isIframe = window !== window.parent && !window.opener;
    
    // Carga directa de datos sin depender de Azure Entra ID
    this.cargarDatos();
  }

  setLoginDisplay() {
    this.loginDisplay = true;
    this.cargarDatos();
  }

  login() {
    if (this.msalGuardConfig.authRequest) {
      this.authService.loginRedirect({ ...this.msalGuardConfig.authRequest } as RedirectRequest);
    } else {
      this.authService.loginRedirect();
    }
  }

  logout() {
    this.authService.logoutRedirect({
      postLogoutRedirectUri: window.location.origin
    });
  }

  cargarDatos() {
    this.pedidosService.getProductos().subscribe({
      next: (data) => {
        this.productos = data;
        data.forEach(p => this.cantidades[p.id] = 1);
      },
      error: (err) => console.error('Error cargando productos', err)
    });

    this.pedidosService.getMisPedidos().subscribe({
      next: (data) => this.misPedidos = data,
      error: (err) => console.error('Error cargando pedidos', err)
    });
  }

  comprar(producto: Producto) {
    const cantidad = this.cantidades[producto.id] || 1;
    this.pedidosService.crearPedido({
      items: [{ productoId: producto.id, cantidad: cantidad }]
    }).subscribe({
      next: () => {
        alert('¡Pedido realizado con éxito!');
        this.cargarDatos();
      },
      error: (err) => alert('Error al crear pedido: ' + (err.error?.message || err.message))
    });
  }

  ngOnDestroy(): void {
    this._destroying$.next(undefined);
    this._destroying$.complete();
  }
}