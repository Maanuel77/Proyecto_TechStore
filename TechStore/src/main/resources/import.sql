-- Datos iniciales de TechStore.
-- Se ejecuta automáticamente tras crear el esquema (spring.sql.init.mode=always
-- y spring.jpa.defer-datasource-initialization=true).
--
-- Contraseñas: usamos el prefijo {noop} de DelegatingPasswordEncoder
-- (texto plano) para no tener que precalcular hashes BCrypt en el SQL.

-- ----------------------------------------------------------------------
-- Usuarios (tabla `usuarios`, SINGLE_TABLE con discriminador user_type)
-- ----------------------------------------------------------------------
INSERT INTO usuarios (user_type, username, password, email, fullname, telefono, superadmin) VALUES
  ('ADMIN', 'admin', '{noop}admin123', 'admin@techstore.com', 'Administrador TechStore', '600000001', TRUE);

INSERT INTO usuarios (user_type, username, password, email, fullname, telefono, superadmin) VALUES
  ('CLIENTE', 'cliente',          '{noop}cliente123', 'cliente@techstore.com',         'Cliente de Prueba', '600000002', FALSE),
  ('CLIENTE', 'sara.ruiz',        '{noop}sara123',    'sara.ruiz@techstore.com',       'Sara Ruiz',         '611000001', FALSE),
  ('CLIENTE', 'lucia.garcia',     '{noop}lucia123',   'lucia.garcia@example.com',      'Lucía García',      '611000002', FALSE),
  ('CLIENTE', 'javier.romero',    '{noop}javier123',  'javier.romero@example.com',     'Javier Romero',     '611000003', FALSE),
  ('CLIENTE', 'marta.sanchez',    '{noop}marta123',   'marta.sanchez@example.com',     'Marta Sánchez',     '611000004', FALSE),
  ('CLIENTE', 'carlos.fernandez', '{noop}carlos123',  'carlos.fernandez@example.com',  'Carlos Fernández',  '611000005', FALSE),
  ('CLIENTE', 'ana.lopez',        '{noop}ana123',     'ana.lopez@example.com',         'Ana López',         '611000006', FALSE);

-- ----------------------------------------------------------------------
-- Productos
-- ----------------------------------------------------------------------
INSERT INTO producto (id, nombre, marca, precio, stock, garantia_meses, refurbished, imagen_url) VALUES
  (1, 'MacBook Air M3',         'Apple',     1299.0, 12, 24, FALSE, 'https://www.apple.com/newsroom/images/2024/03/apple-unveils-the-new-13-and-15-inch-macbook-air-with-the-powerful-m3-chip/tile/Apple-MacBook-Air-2-up-hero-240304-lp.jpg.landing-big_2x.jpg'),
  (2, 'Galaxy S24 Ultra',       'Samsung',   1199.0,  3, 24, FALSE, 'https://imageservice.asgoodasnew.com/540/21760/71/title-0000.jpg'),
  (3, 'ThinkPad X1 Carbon',     'Lenovo',    1599.0,  7, 36, FALSE, 'https://p4-ofp.static.pub//fes/cms/2025/02/21/bef0xmugvo4dyhtz4xek6six6a6809560758.png'),
  (4, 'iPad Pro 11"',           'Apple',      899.0,  0, 24, TRUE,  'https://cdsassets.apple.com/live/SZLF0YNV/images/sp/111974_ipad-pro-11-2018.png'),
  (5, 'Pixel 8 Pro',            'Google',     999.0, 15, 24, FALSE, 'https://fixelmovil.com/cdn/shop/files/google-pixel-8pro-azul.png?v=1757608362&width=1280'),
  (6, 'Surface Laptop 5',       'Microsoft', 1099.0,  2, 24, FALSE, 'https://gfx3.senetic.com/akeneo-catalog/5/2/c/f/52cf853ca3513eb2ce0b3ea09106a6ce3eead698_1684040_R1T_00009_image2.jpg'),
  (7, 'AirPods Pro 2',          'Apple',      279.0, 40, 12, FALSE, 'https://cdsassets.apple.com/live/SZLF0YNV/images/sp/111851_sp880-airpods-Pro-2nd-gen.png'),
  (8, 'Monitor UltraSharp 27"', 'Dell',       529.0,  1, 36, TRUE,  'https://i.dell.com/is/image/DellContent/content/dam/ss2/product-images/peripherals/output-devices/dell/monitors/up2720qa/global-spi/ng/monitor-up2720qa-gray-campaign-hero-504x350-ng.png?hei=402&qtl=90,0&op_usm=1.75,0.3,2,0&resMode=sharp&pscan=auto');

ALTER SEQUENCE producto_seq RESTART WITH 9;

-- ----------------------------------------------------------------------
-- Pedidos (uno por cliente con datos demo). Los cliente_id se corresponden
-- con la tabla `usuarios` (IDENTITY, así que: lucia=4, javier=5, marta=6).
-- ----------------------------------------------------------------------
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  (1, 'PED-0001', DATEADD('DAY', -10, CURRENT_DATE), 1677.0, 4),
  (2, 'PED-0002', DATEADD('DAY', -3,  CURRENT_DATE), 1998.0, 5),
  (3, 'PED-0003', DATEADD('DAY', -1,  CURRENT_DATE), 1748.0, 6);

ALTER SEQUENCE pedido_seq RESTART WITH 4;

-- ----------------------------------------------------------------------
-- Líneas de pedido
--   Pedido 1 (Lucía):  MacBook con garantía extendida + AirPods
--   Pedido 2 (Javier): 2 unidades de Pixel 8 Pro
--   Pedido 3 (Marta):  ThinkPad con garantía extendida
-- ----------------------------------------------------------------------
INSERT INTO linea_pedido (id, cantidad, precio_unitario, subtotal, garantia_extendida, coste_garantia, id_producto, id_pedido) VALUES
  (1, 1, 1299.0, 1299.0, TRUE,   99.0, 1, 1),
  (2, 1,  279.0,  279.0, FALSE,   0.0, 7, 1),
  (3, 2,  999.0, 1998.0, FALSE,   0.0, 5, 2),
  (4, 1, 1599.0, 1599.0, TRUE,  149.0, 3, 3);

ALTER SEQUENCE linea_pedido_seq RESTART WITH 5;
