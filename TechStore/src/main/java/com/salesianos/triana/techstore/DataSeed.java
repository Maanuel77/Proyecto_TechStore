package com.salesianos.triana.techstore;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.salesianos.triana.techstore.model.Cliente;
import com.salesianos.triana.techstore.model.LineaPedido;
import com.salesianos.triana.techstore.model.Pedido;
import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.repository.ClienteRepository;
import com.salesianos.triana.techstore.repository.PedidoRepository;
import com.salesianos.triana.techstore.repository.ProductoRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * Carga datos de ejemplo al arrancar la aplicación.
 * Como ddl-auto=create-drop, la base de datos se recrea cada vez,
 * por lo que estos datos se insertan en cada arranque.
 */
@Component
@RequiredArgsConstructor
public class DataSeed {

    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    @PostConstruct
    public void init() {

        // Si ya hay productos (otro perfil con ddl-auto=update), no duplicamos
        if (productoRepository.count() > 0) return;

        List<Producto> productos = seedProductos();
        seedClientesYPedidos(productos);
    }

    private List<Producto> seedProductos() {

        Producto p1 = Producto.builder()
                .nombre("MacBook Air M3")
                .marca("Apple")
                .precio(1299.0)
                .stock(12)
                .garantiaMeses(24)
                .refurbished(false)
                .imagenUrl("https://www.apple.com/newsroom/images/2024/03/apple-unveils-the-new-13-and-15-inch-macbook-air-with-the-powerful-m3-chip/tile/Apple-MacBook-Air-2-up-hero-240304-lp.jpg.landing-big_2x.jpg")
                .build();

        Producto p2 = Producto.builder()
                .nombre("Galaxy S24 Ultra")
                .marca("Samsung")
                .precio(1199.0)
                .stock(3)
                .garantiaMeses(24)
                .refurbished(false)
                .imagenUrl("https://imageservice.asgoodasnew.com/540/21760/71/title-0000.jpg")
                .build();

        Producto p3 = Producto.builder()
                .nombre("ThinkPad X1 Carbon")
                .marca("Lenovo")
                .precio(1599.0)
                .stock(7)
                .garantiaMeses(36)
                .refurbished(false)
                .imagenUrl("https://p4-ofp.static.pub//fes/cms/2025/02/21/bef0xmugvo4dyhtz4xek6six6a6809560758.png")
                .build();

        Producto p4 = Producto.builder()
                .nombre("iPad Pro 11\"")
                .marca("Apple")
                .precio(899.0)
                .stock(0)
                .garantiaMeses(24)
                .refurbished(true)
                .imagenUrl("https://cdsassets.apple.com/live/SZLF0YNV/images/sp/111974_ipad-pro-11-2018.png")
                .build();

        Producto p5 = Producto.builder()
                .nombre("Pixel 8 Pro")
                .marca("Google")
                .precio(999.0)
                .stock(15)
                .garantiaMeses(24)
                .refurbished(false)
                .imagenUrl("https://fixelmovil.com/cdn/shop/files/google-pixel-8pro-azul.png?v=1757608362&width=1280")
                .build();

        Producto p6 = Producto.builder()
                .nombre("Surface Laptop 5")
                .marca("Microsoft")
                .precio(1099.0)
                .stock(2)
                .garantiaMeses(24)
                .refurbished(false)
                .imagenUrl("https://gfx3.senetic.com/akeneo-catalog/5/2/c/f/52cf853ca3513eb2ce0b3ea09106a6ce3eead698_1684040_R1T_00009_image2.jpg")
                .build();

        Producto p7 = Producto.builder()
                .nombre("AirPods Pro 2")
                .marca("Apple")
                .precio(279.0)
                .stock(40)
                .garantiaMeses(12)
                .refurbished(false)
                .imagenUrl("https://cdsassets.apple.com/live/SZLF0YNV/images/sp/111851_sp880-airpods-Pro-2nd-gen.png")
                .build();

        Producto p8 = Producto.builder()
                .nombre("Monitor UltraSharp 27\"")
                .marca("Dell")
                .precio(529.0)
                .stock(1)
                .garantiaMeses(36)
                .refurbished(true)
                .imagenUrl("https://i.dell.com/is/image/DellContent/content/dam/ss2/product-images/peripherals/output-devices/dell/monitors/up2720qa/global-spi/ng/monitor-up2720qa-gray-campaign-hero-504x350-ng.png?hei=402&qtl=90,0&op_usm=1.75,0.3,2,0&resMode=sharp&pscan=auto")
                .build();

        return productoRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8));
    }

    private void seedClientesYPedidos(List<Producto> productos) {

        Cliente c1 = Cliente.builder()
                .nombre("Lucía García")
                .email("lucia.garcia@example.com")
                .telefono("600111222")
                .build();

        Cliente c2 = Cliente.builder()
                .nombre("Javier Romero")
                .email("javier.romero@example.com")
                .telefono("611222333")
                .build();

        Cliente c3 = Cliente.builder()
                .nombre("Marta Sánchez")
                .email("marta.sanchez@example.com")
                .telefono("622333444")
                .build();

        clienteRepository.saveAll(List.of(c1, c2, c3));

        // Pedido 1: Lucía compra MacBook Air + AirPods
        Pedido pedido1 = Pedido.builder()
                .codigo("PED-0001")
                .fecha(LocalDate.now().minusDays(10))
                .cliente(c1)
                .build();

        LineaPedido l1a = LineaPedido.builder()
                .producto(productos.get(0)) // MacBook Air
                .cantidad(1)
                .precioUnitario(productos.get(0).getPrecio())
                .subtotal(productos.get(0).getPrecio())
                .garantiaExtendida(true)
                .costeGarantia(99.0)
                .build();

        LineaPedido l1b = LineaPedido.builder()
                .producto(productos.get(6)) // AirPods
                .cantidad(1)
                .precioUnitario(productos.get(6).getPrecio())
                .subtotal(productos.get(6).getPrecio())
                .garantiaExtendida(false)
                .costeGarantia(0.0)
                .build();

        pedido1.addLineaPedido(l1a);
        pedido1.addLineaPedido(l1b);
        pedido1.setTotal(l1a.getSubtotal() + l1a.getCosteGarantia()
                + l1b.getSubtotal() + l1b.getCosteGarantia());

        // Pedido 2: Javier compra Pixel 8 Pro
        Pedido pedido2 = Pedido.builder()
                .codigo("PED-0002")
                .fecha(LocalDate.now().minusDays(3))
                .cliente(c2)
                .build();

        LineaPedido l2 = LineaPedido.builder()
                .producto(productos.get(4)) // Pixel 8 Pro
                .cantidad(2)
                .precioUnitario(productos.get(4).getPrecio())
                .subtotal(productos.get(4).getPrecio() * 2)
                .garantiaExtendida(false)
                .costeGarantia(0.0)
                .build();

        pedido2.addLineaPedido(l2);
        pedido2.setTotal(l2.getSubtotal());

        // Pedido 3: Marta compra ThinkPad
        Pedido pedido3 = Pedido.builder()
                .codigo("PED-0003")
                .fecha(LocalDate.now().minusDays(1))
                .cliente(c3)
                .build();

        LineaPedido l3 = LineaPedido.builder()
                .producto(productos.get(2)) // ThinkPad
                .cantidad(1)
                .precioUnitario(productos.get(2).getPrecio())
                .subtotal(productos.get(2).getPrecio())
                .garantiaExtendida(true)
                .costeGarantia(149.0)
                .build();

        pedido3.addLineaPedido(l3);
        pedido3.setTotal(l3.getSubtotal() + l3.getCosteGarantia());

        pedidoRepository.saveAll(List.of(pedido1, pedido2, pedido3));
    }
}
