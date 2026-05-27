-- Datos iniciales de TechStore.
-- Se ejecuta automaticamente tras crear el esquema (lo carga Hibernate).
-- Contrasenyas: prefijo {noop} de DelegatingPasswordEncoder (texto plano).

-- ======================================================================
-- USUARIOS (tabla `usuarios`)
-- IDs autogenerados por IDENTITY, en orden de insercion:
--   1=admin  2=cliente  3=sara  4=lucia  5=javier  6=marta  7=carlos
--   8=ana    9=david   10=laura  11=pablo  12=elena 13=alvaro 14=nuria
--   15=rafael 16=clara 17=miguel 18=irene 19=sergio 20=julia 21=hector
-- ======================================================================
INSERT INTO usuarios (user_type, username, password, email, fullname, telefono, superadmin) VALUES
  ('ADMIN',   'admin',            '{noop}admin123',   'admin@techstore.com',           'Administrador TechStore', '600000001', TRUE);

INSERT INTO usuarios (user_type, username, password, email, fullname, telefono, superadmin) VALUES
  ('CLIENTE', 'cliente',          '{noop}cliente123', 'cliente@techstore.com',         'Cliente de Prueba',  '600000002', FALSE),
  ('CLIENTE', 'sara.ruiz',        '{noop}sara123',    'sara.ruiz@techstore.com',       'Sara Ruiz',          '611000001', FALSE),
  ('CLIENTE', 'lucia.garcia',     '{noop}lucia123',   'lucia.garcia@example.com',      'Lucia Garcia',       '611000002', FALSE),
  ('CLIENTE', 'javier.romero',    '{noop}javier123',  'javier.romero@example.com',     'Javier Romero',      '611000003', FALSE),
  ('CLIENTE', 'marta.sanchez',    '{noop}marta123',   'marta.sanchez@example.com',     'Marta Sanchez',      '611000004', FALSE),
  ('CLIENTE', 'carlos.fernandez', '{noop}carlos123',  'carlos.fernandez@example.com',  'Carlos Fernandez',   '611000005', FALSE),
  ('CLIENTE', 'ana.lopez',        '{noop}ana123',     'ana.lopez@example.com',         'Ana Lopez',          '611000006', FALSE),
  ('CLIENTE', 'david.martin',     '{noop}david123',   'david.martin@example.com',      'David Martin',       '622000001', FALSE),
  ('CLIENTE', 'laura.gomez',      '{noop}laura123',   'laura.gomez@example.com',       'Laura Gomez',        '622000002', FALSE),
  ('CLIENTE', 'pablo.ruiz',       '{noop}pablo123',   'pablo.ruiz@example.com',        'Pablo Ruiz',         '622000003', FALSE),
  ('CLIENTE', 'elena.diaz',       '{noop}elena123',   'elena.diaz@example.com',        'Elena Diaz',         '622000004', FALSE),
  ('CLIENTE', 'alvaro.serrano',   '{noop}alvaro123',  'alvaro.serrano@example.com',    'Alvaro Serrano',     '622000005', FALSE),
  ('CLIENTE', 'nuria.castro',     '{noop}nuria123',   'nuria.castro@example.com',      'Nuria Castro',       '622000006', FALSE),
  ('CLIENTE', 'rafael.ortiz',     '{noop}rafael123',  'rafael.ortiz@example.com',      'Rafael Ortiz',       '633000001', FALSE),
  ('CLIENTE', 'clara.molina',     '{noop}clara123',   'clara.molina@example.com',      'Clara Molina',       '633000002', FALSE),
  ('CLIENTE', 'miguel.torres',    '{noop}miguel123',  'miguel.torres@example.com',     'Miguel Torres',      '633000003', FALSE),
  ('CLIENTE', 'irene.vargas',     '{noop}irene123',   'irene.vargas@example.com',      'Irene Vargas',       '633000004', FALSE),
  ('CLIENTE', 'sergio.navas',     '{noop}sergio123',  'sergio.navas@example.com',      'Sergio Navas',       '633000005', FALSE),
  ('CLIENTE', 'julia.alonso',     '{noop}julia123',   'julia.alonso@example.com',      'Julia Alonso',       '633000006', FALSE),
  ('CLIENTE', 'hector.mendez',    '{noop}hector123',  'hector.mendez@example.com',     'Hector Mendez',      '633000007', FALSE);

-- ======================================================================
-- PRODUCTOS (30 articulos de 10 marcas distintas)
-- El stock representa lo disponible AHORA (despues de los pedidos seed).
-- ======================================================================
INSERT INTO producto (id, nombre, marca, precio, stock, garantia_meses, refurbished, imagen_url) VALUES
  (1,  'MacBook Air M3',         'Apple',     1299.0, 12, 24, FALSE, 'https://www.apple.com/newsroom/images/2024/03/apple-unveils-the-new-13-and-15-inch-macbook-air-with-the-powerful-m3-chip/tile/Apple-MacBook-Air-2-up-hero-240304-lp.jpg.landing-big_2x.jpg'),
  (2,  'Galaxy S24 Ultra',       'Samsung',   1199.0,  3, 24, FALSE, 'https://imageservice.asgoodasnew.com/540/21760/71/title-0000.jpg'),
  (3,  'ThinkPad X1 Carbon',     'Lenovo',    1599.0,  7, 36, FALSE, 'https://p4-ofp.static.pub//fes/cms/2025/02/21/bef0xmugvo4dyhtz4xek6six6a6809560758.png'),
  (4,  'iPad Pro 11',            'Apple',      899.0,  0, 24, TRUE,  'https://cdsassets.apple.com/live/SZLF0YNV/images/sp/111974_ipad-pro-11-2018.png'),
  (5,  'Pixel 8 Pro',            'Google',     999.0, 15, 24, FALSE, 'https://fixelmovil.com/cdn/shop/files/google-pixel-8pro-azul.png?v=1757608362&width=1280'),
  (6,  'Surface Laptop 5',       'Microsoft', 1099.0,  4, 24, FALSE, 'https://gfx3.senetic.com/akeneo-catalog/5/2/c/f/52cf853ca3513eb2ce0b3ea09106a6ce3eead698_1684040_R1T_00009_image2.jpg'),
  (7,  'AirPods Pro 2',          'Apple',      279.0, 40, 12, FALSE, 'https://cdsassets.apple.com/live/SZLF0YNV/images/sp/111851_sp880-airpods-Pro-2nd-gen.png'),
  (8,  'Monitor UltraSharp 27"', 'Dell',       529.0,  1, 36, TRUE,  'https://i.dell.com/is/image/DellContent/content/dam/ss2/product-images/peripherals/output-devices/dell/monitors/up2720qa/global-spi/ng/monitor-up2720qa-gray-campaign-hero-504x350-ng.png?hei=402&qtl=90,0&op_usm=1.75,0.3,2,0&resMode=sharp&pscan=auto');

INSERT INTO producto (id, nombre, marca, precio, stock, garantia_meses, refurbished, imagen_url) VALUES
  (9,  'MacBook Pro M4 14',      'Apple',     2499.0,  5, 24, FALSE, 'https://cdsassets.apple.com/live/7WUAS350/images/tech-specs/mbp14-m4-2024.png'),
  (10, 'iPad Air',               'Apple',      699.0, 18, 24, FALSE, 'https://www.apple.com/es/ipad-air/images/overview/two-sizes/gallery-toggle/spin_reverse_endframe__crvqft16efue_large.png'),
  (11, 'AirPods Max',            'Apple',      549.0,  9, 12, FALSE, 'https://www.orly.es/58275-large_default/apple-airpods-max-mgyj3tya-auriculares-de-diadema-anc-bluetooth-5-20h-color-plata-.jpg'),
  (12, 'iPhone 15 Pro',          'Apple',     1199.0,  6, 24, FALSE, 'https://99mobilebn.com/wp-content/uploads/2023/11/iPhone-15-Pro-Max-06.png'),
  (13, 'Apple Watch Ultra 2',    'Apple',      899.0, 10, 24, FALSE, 'https://cdsassets.apple.com/live/7WUAS350/images/tech-specs/111832-watch-ultra-2.png'),
  (14, 'Galaxy Tab S9',          'Samsung',    799.0, 14, 24, FALSE, 'https://m.media-amazon.com/images/I/515Bj67jfSL.jpg'),
  (15, 'Galaxy Buds Pro 2',      'Samsung',    199.0, 25, 12, FALSE, 'https://m.media-amazon.com/images/I/51zkBAWnjlL.jpg'),
  (16, 'Pixel Watch 2',          'Google',     349.0, 12, 24, FALSE, 'https://www.pakmobizone.pk/wp-content/uploads/2024/01/Google-Pixel-Watch-2-Matte-Black-1.png');

INSERT INTO producto (id, nombre, marca, precio, stock, garantia_meses, refurbished, imagen_url) VALUES
  (17, 'Surface Pro 9',          'Microsoft', 1399.0,  5, 24, FALSE, 'https://m.media-amazon.com/images/I/514+6C7zRyL.jpg'),
  (18, 'ThinkPad T14',           'Lenovo',    1399.0,  6, 36, FALSE, 'https://storage.googleapis.com/catalog-pictures-carrefour-es/catalog/pictures/hd_510x_/8435819519870_1.jpg'),
  (19, 'Legion 5 Pro',           'Lenovo',    1299.0,  8, 24, FALSE, 'https://p4-ofp.static.pub/fes/cms/2022/06/16/s444ye7sqwg6rv1b8on54vm77eopra543125.png'),
  (20, 'XPS 13',                 'Dell',      1299.0,  7, 36, FALSE, 'https://m.media-amazon.com/images/I/71ZQ2AIvBFL._AC_UF894,1000_QL80_.jpg'),
  (21, 'Latitude 7440',          'Dell',      1599.0,  4, 36, FALSE, 'https://files.refurbed.com/pi/dell-latitude-7440-i7-1365u-1700557641.jpg'),
  (22, 'Spectre x360',           'HP',        1499.0,  6, 24, FALSE, 'https://m.media-amazon.com/images/I/51k4j6kS6fL.jpg'),
  (23, 'EliteBook 840',          'HP',        1499.0,  5, 36, FALSE, 'https://m.media-amazon.com/images/I/81sJ0TW1ttL.jpg'),
  (24, 'ROG Zephyrus G14',       'Asus',      1899.0,  4, 24, FALSE, 'https://m.media-amazon.com/images/I/51syOrLqp1L.jpg');

INSERT INTO producto (id, nombre, marca, precio, stock, garantia_meses, refurbished, imagen_url) VALUES
  (25, 'ZenBook 14 OLED',        'Asus',      1099.0,  8, 24, FALSE, 'https://dlcdnwebimgs.asus.com/gain/ad84a84b-50a3-4d62-a6e3-9a3574692fc3/'),
  (26, 'WH-1000XM5',             'Sony',       399.0, 22, 12, FALSE, 'https://www.sony.es/image/5d02da5df552836db894cead8a68f5f3?fmt=png-alpha&wid=720'),
  (27, 'PlayStation 5',          'Sony',       549.0, 11, 24, FALSE, 'https://media.ldlc.com/r1600/ld/products/00/06/08/64/LD0006086466.jpg'),
  (28, 'QuietComfort Ultra',     'Bose',       449.0, 13, 24, FALSE, 'https://m.media-amazon.com/images/I/61z+9dMt9vL._AC_UF1000,1000_QL80_.jpg'),
  (29, 'UltraGear 32GP850',      'LG',         699.0,  9, 24, FALSE, 'https://www.lg.com/content/dam/channel/wcms/es/images/monitores/32gp850-b_aeu_eees_es_c/gallery/32GP850-LOL.jpg'),
  (30, 'Gram 16',                'LG',        1399.0,  6, 24, FALSE, 'https://m.media-amazon.com/images/I/81pKId7SSYS.jpg');

ALTER SEQUENCE producto_seq RESTART WITH 31;

-- ======================================================================
-- PEDIDOS (40 pedidos repartidos en los ultimos ~110 dias)
-- Cada total = suma(subtotal + coste_garantia) de sus lineas.
-- ======================================================================

-- Javier (id=5) - top spender, 7 pedidos
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  ( 1, 'PED-0001', DATEADD('DAY', -100, CURRENT_DATE), 3027.9,  5),
  ( 2, 'PED-0002', DATEADD('DAY',  -75, CURRENT_DATE), 1278.0,  5),
  ( 3, 'PED-0003', DATEADD('DAY',  -60, CURRENT_DATE), 2677.0,  5),
  ( 4, 'PED-0004', DATEADD('DAY',  -45, CURRENT_DATE), 1298.0,  5),
  ( 5, 'PED-0005', DATEADD('DAY',  -25, CURRENT_DATE),  748.0,  5),
  ( 6, 'PED-0006', DATEADD('DAY',  -12, CURRENT_DATE), 1707.9,  5),
  ( 7, 'PED-0007', DATEADD('DAY',   -3, CURRENT_DATE),  549.0,  5);

-- Lucia (id=4) - 6 pedidos
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  ( 8, 'PED-0008', DATEADD('DAY', -110, CURRENT_DATE), 1758.9,  4),
  ( 9, 'PED-0009', DATEADD('DAY',  -90, CURRENT_DATE),  808.0,  4),
  (10, 'PED-0010', DATEADD('DAY',  -55, CURRENT_DATE),  699.0,  4),
  (11, 'PED-0011', DATEADD('DAY',  -35, CURRENT_DATE),  398.0,  4),
  (12, 'PED-0012', DATEADD('DAY',  -15, CURRENT_DATE), 1678.0,  4),
  (13, 'PED-0013', DATEADD('DAY',   -2, CURRENT_DATE),  279.0,  4);

-- Marta (id=6) - 5 pedidos
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  (14, 'PED-0014', DATEADD('DAY',  -95, CURRENT_DATE), 1398.0,  6),
  (15, 'PED-0015', DATEADD('DAY',  -70, CURRENT_DATE),  449.0,  6),
  (16, 'PED-0016', DATEADD('DAY',  -40, CURRENT_DATE), 2088.9,  6),
  (17, 'PED-0017', DATEADD('DAY',  -20, CURRENT_DATE),  628.0,  6),
  (18, 'PED-0018', DATEADD('DAY',   -5, CURRENT_DATE),  799.0,  6);

-- Carlos (id=7) - 4 pedidos
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  (19, 'PED-0019', DATEADD('DAY',  -85, CURRENT_DATE), 1578.0,  7),
  (20, 'PED-0020', DATEADD('DAY',  -50, CURRENT_DATE), 1395.0,  7),
  (21, 'PED-0021', DATEADD('DAY',  -22, CURRENT_DATE), 1499.0,  7),
  (22, 'PED-0022', DATEADD('DAY',   -8, CURRENT_DATE),  998.0,  7);

-- Ana (id=8) - 4 pedidos
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  (23, 'PED-0023', DATEADD('DAY', -105, CURRENT_DATE),  699.0,  8),
  (24, 'PED-0024', DATEADD('DAY',  -65, CURRENT_DATE), 1399.0,  8),
  (25, 'PED-0025', DATEADD('DAY',  -30, CURRENT_DATE),  899.0,  8),
  (26, 'PED-0026', DATEADD('DAY',  -10, CURRENT_DATE), 1178.0,  8);

-- Sara (id=3) - 3 pedidos
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  (27, 'PED-0027', DATEADD('DAY',  -80, CURRENT_DATE), 1298.0,  3),
  (28, 'PED-0028', DATEADD('DAY',  -35, CURRENT_DATE),  999.0,  3),
  (29, 'PED-0029', DATEADD('DAY',  -14, CURRENT_DATE),  279.0,  3);

-- David (id=9) - 3 pedidos
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  (30, 'PED-0030', DATEADD('DAY',  -88, CURRENT_DATE), 1378.0,  9),
  (31, 'PED-0031', DATEADD('DAY',  -28, CURRENT_DATE),  199.0,  9),
  (32, 'PED-0032', DATEADD('DAY',   -6, CURRENT_DATE),  999.0,  9);

-- Laura (id=10) - 3 pedidos
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  (33, 'PED-0033', DATEADD('DAY',  -78, CURRENT_DATE), 1299.0, 10),
  (34, 'PED-0034', DATEADD('DAY',  -45, CURRENT_DATE), 1399.0, 10),
  (35, 'PED-0035', DATEADD('DAY',  -18, CURRENT_DATE),  549.0, 10);

-- Elena (id=12) - 2 pedidos
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  (36, 'PED-0036', DATEADD('DAY',  -60, CURRENT_DATE), 1499.0, 12),
  (37, 'PED-0037', DATEADD('DAY',  -24, CURRENT_DATE), 1599.0, 12);

-- Pablo (id=11), Alvaro (id=13), Nuria (id=14) - 1 pedido cada uno
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  (38, 'PED-0038', DATEADD('DAY',  -50, CURRENT_DATE),  199.0, 11),
  (39, 'PED-0039', DATEADD('DAY',  -17, CURRENT_DATE),  399.0, 13),
  (40, 'PED-0040', DATEADD('DAY',   -4, CURRENT_DATE),  349.0, 14);

-- Pedidos extra: deliberadamente caen en fechas que YA tenian pedidos
-- (mismo dia) para que la grafica de pedidos por fecha muestre picos.
-- Tambien activan clientes que antes no compraban (rafael, clara, miguel,
-- irene, sergio, julia, hector).
INSERT INTO pedido (id, codigo, fecha, total, id_cliente) VALUES
  (41, 'PED-0041', DATEADD('DAY',   -3, CURRENT_DATE),  628.0,  8),  -- Ana (mismo dia que Javier ped 7)
  (42, 'PED-0042', DATEADD('DAY',   -3, CURRENT_DATE),  199.0, 15),  -- Rafael (mismo dia, 3 pedidos en total)
  (43, 'PED-0043', DATEADD('DAY',   -2, CURRENT_DATE), 1099.0,  7),  -- Carlos (mismo dia que Lucia ped 13)
  (44, 'PED-0044', DATEADD('DAY',   -2, CURRENT_DATE),  279.0, 17),  -- Miguel (mismo dia, 3 pedidos)
  (45, 'PED-0045', DATEADD('DAY',  -10, CURRENT_DATE), 1278.0, 10),  -- Laura (mismo dia que Ana ped 26)
  (46, 'PED-0046', DATEADD('DAY',  -10, CURRENT_DATE),  199.0, 16),  -- Clara (mismo dia, 3 pedidos)
  (47, 'PED-0047', DATEADD('DAY',  -15, CURRENT_DATE),  699.0,  9),  -- David (mismo dia que Lucia ped 12)
  (48, 'PED-0048', DATEADD('DAY',  -15, CURRENT_DATE),  279.0, 18),  -- Irene (mismo dia, 3 pedidos)
  (49, 'PED-0049', DATEADD('DAY',  -20, CURRENT_DATE),  799.0,  5),  -- Javier (mismo dia que Marta ped 17)
  (50, 'PED-0050', DATEADD('DAY',  -25, CURRENT_DATE), 1178.0,  3),  -- Sara (mismo dia que Javier ped 5)
  (51, 'PED-0051', DATEADD('DAY',  -25, CURRENT_DATE),  279.0, 11),  -- Pablo (mismo dia, 3 pedidos)
  (52, 'PED-0052', DATEADD('DAY',  -30, CURRENT_DATE), 1399.0, 20),  -- Julia (mismo dia que Ana ped 25)
  (53, 'PED-0053', DATEADD('DAY',  -45, CURRENT_DATE), 1428.9,  6),  -- Marta (mismo dia que Javier ped 4 y Laura ped 34, 3 pedidos)
  (54, 'PED-0054', DATEADD('DAY',  -50, CURRENT_DATE), 1199.0,  8),  -- Ana (mismo dia que Carlos ped 20 y Pablo ped 38, 3 pedidos)
  (55, 'PED-0055', DATEADD('DAY',   -8, CURRENT_DATE),  999.0,  5),  -- Javier (mismo dia que Carlos ped 22)
  (56, 'PED-0056', DATEADD('DAY',   -8, CURRENT_DATE),  279.0, 19),  -- Sergio (mismo dia, 3 pedidos)
  (57, 'PED-0057', DATEADD('DAY',   -5, CURRENT_DATE),  399.0, 21),  -- Hector (mismo dia que Marta ped 18)
  (58, 'PED-0058', DATEADD('DAY',   -5, CURRENT_DATE),  549.0,  9);  -- David (mismo dia, 3 pedidos)

ALTER SEQUENCE pedido_seq RESTART WITH 59;

-- ======================================================================
-- LINEAS DE PEDIDO (~55 lineas)
-- ======================================================================

-- Pedidos de Javier (7 pedidos, 13 lineas)
INSERT INTO linea_pedido (id, cantidad, precio_unitario, subtotal, garantia_extendida, coste_garantia, id_producto, id_pedido) VALUES
  ( 1, 1, 2499.0, 2499.0, TRUE,  249.9,  9,  1),  -- MacBook Pro M4 + garantia
  ( 2, 1,  279.0,  279.0, FALSE,   0.0,  7,  1),  -- AirPods Pro 2
  ( 3, 1,  999.0,  999.0, FALSE,   0.0,  5,  2),  -- Pixel 8 Pro
  ( 4, 1,  279.0,  279.0, FALSE,   0.0,  7,  2),  -- AirPods Pro 2
  ( 5, 2, 1199.0, 2398.0, FALSE,   0.0, 12,  3),  -- 2x iPhone 15 Pro
  ( 6, 1,  279.0,  279.0, FALSE,   0.0,  7,  3),  -- AirPods Pro 2
  ( 7, 1,  399.0,  399.0, FALSE,   0.0, 26,  4),  -- Sony WH-1000XM5
  ( 8, 1,  899.0,  899.0, FALSE,   0.0, 13,  4),  -- Apple Watch Ultra 2
  ( 9, 1,  549.0,  549.0, FALSE,   0.0, 27,  5),  -- PlayStation 5
  (10, 1,  199.0,  199.0, FALSE,   0.0, 15,  5),  -- Galaxy Buds Pro 2
  (11, 1, 1299.0, 1299.0, TRUE,  129.9,  1,  6),  -- MacBook Air M3 + garantia
  (12, 1,  279.0,  279.0, FALSE,   0.0,  7,  6),  -- AirPods Pro 2
  (13, 1,  549.0,  549.0, FALSE,   0.0, 11,  7);  -- AirPods Max

-- Pedidos de Lucia (6 pedidos, 8 lineas)
INSERT INTO linea_pedido (id, cantidad, precio_unitario, subtotal, garantia_extendida, coste_garantia, id_producto, id_pedido) VALUES
  (14, 1, 1599.0, 1599.0, TRUE,  159.9,  3,  8),  -- ThinkPad X1 Carbon + garantia
  (15, 1,  529.0,  529.0, FALSE,   0.0,  8,  9),  -- Monitor UltraSharp 27"
  (16, 1,  279.0,  279.0, FALSE,   0.0,  7,  9),  -- AirPods Pro 2
  (17, 1,  699.0,  699.0, FALSE,   0.0, 10, 10),  -- iPad Air
  (18, 2,  199.0,  398.0, FALSE,   0.0, 15, 11),  -- 2x Galaxy Buds Pro 2
  (19, 1, 1399.0, 1399.0, FALSE,   0.0, 17, 12),  -- Surface Pro 9
  (20, 1,  279.0,  279.0, FALSE,   0.0,  7, 12),  -- AirPods Pro 2
  (21, 1,  279.0,  279.0, FALSE,   0.0,  7, 13);  -- AirPods Pro 2

-- Pedidos de Marta (5 pedidos, 7 lineas)
INSERT INTO linea_pedido (id, cantidad, precio_unitario, subtotal, garantia_extendida, coste_garantia, id_producto, id_pedido) VALUES
  (22, 1, 1199.0, 1199.0, FALSE,   0.0,  2, 14),  -- Galaxy S24 Ultra
  (23, 1,  199.0,  199.0, FALSE,   0.0, 15, 14),  -- Galaxy Buds Pro 2
  (24, 1,  449.0,  449.0, FALSE,   0.0, 28, 15),  -- Bose QuietComfort Ultra
  (25, 1, 1899.0, 1899.0, TRUE,  189.9, 24, 16),  -- ROG Zephyrus G14 + garantia
  (26, 1,  349.0,  349.0, FALSE,   0.0, 16, 17),  -- Pixel Watch 2
  (27, 1,  279.0,  279.0, FALSE,   0.0,  7, 17),  -- AirPods Pro 2
  (28, 1,  799.0,  799.0, FALSE,   0.0, 14, 18);  -- Galaxy Tab S9

-- Pedidos de Carlos (4 pedidos, 6 lineas)
INSERT INTO linea_pedido (id, cantidad, precio_unitario, subtotal, garantia_extendida, coste_garantia, id_producto, id_pedido) VALUES
  (29, 1, 1299.0, 1299.0, FALSE,   0.0, 20, 19),  -- XPS 13
  (30, 1,  279.0,  279.0, FALSE,   0.0,  7, 19),  -- AirPods Pro 2
  (31, 5,  279.0, 1395.0, FALSE,   0.0,  7, 20),  -- 5x AirPods Pro 2 (regalo masivo)
  (32, 1, 1499.0, 1499.0, FALSE,   0.0, 22, 21),  -- Spectre x360
  (33, 1,  199.0,  199.0, FALSE,   0.0, 15, 22),  -- Galaxy Buds Pro 2
  (34, 1,  799.0,  799.0, FALSE,   0.0, 14, 22);  -- Galaxy Tab S9

-- Pedidos de Ana (4 pedidos, 5 lineas)
INSERT INTO linea_pedido (id, cantidad, precio_unitario, subtotal, garantia_extendida, coste_garantia, id_producto, id_pedido) VALUES
  (35, 1,  699.0,  699.0, FALSE,   0.0, 29, 23),  -- UltraGear 32GP850
  (36, 1, 1399.0, 1399.0, FALSE,   0.0, 18, 24),  -- ThinkPad T14
  (37, 1,  899.0,  899.0, FALSE,   0.0,  4, 25),  -- iPad Pro 11" (refurbished)
  (38, 1,  899.0,  899.0, FALSE,   0.0, 13, 26),  -- Apple Watch Ultra 2
  (39, 1,  279.0,  279.0, FALSE,   0.0,  7, 26);  -- AirPods Pro 2

-- Pedidos de Sara (3 pedidos, 4 lineas)
INSERT INTO linea_pedido (id, cantidad, precio_unitario, subtotal, garantia_extendida, coste_garantia, id_producto, id_pedido) VALUES
  (40, 1, 1099.0, 1099.0, FALSE,   0.0, 25, 27),  -- ZenBook 14 OLED
  (41, 1,  199.0,  199.0, FALSE,   0.0, 15, 27),  -- Galaxy Buds Pro 2
  (42, 1,  999.0,  999.0, FALSE,   0.0,  5, 28),  -- Pixel 8 Pro
  (43, 1,  279.0,  279.0, FALSE,   0.0,  7, 29);  -- AirPods Pro 2

-- Pedidos de David (3 pedidos, 4 lineas)
INSERT INTO linea_pedido (id, cantidad, precio_unitario, subtotal, garantia_extendida, coste_garantia, id_producto, id_pedido) VALUES
  (44, 1, 1099.0, 1099.0, FALSE,   0.0,  6, 30),  -- Surface Laptop 5
  (45, 1,  279.0,  279.0, FALSE,   0.0,  7, 30),  -- AirPods Pro 2
  (46, 1,  199.0,  199.0, FALSE,   0.0, 15, 31),  -- Galaxy Buds Pro 2
  (47, 1,  999.0,  999.0, FALSE,   0.0,  5, 32);  -- Pixel 8 Pro

-- Pedidos de Laura, Elena, Pablo, Alvaro, Nuria (8 pedidos, 8 lineas)
INSERT INTO linea_pedido (id, cantidad, precio_unitario, subtotal, garantia_extendida, coste_garantia, id_producto, id_pedido) VALUES
  (48, 1, 1299.0, 1299.0, FALSE,   0.0, 19, 33),  -- Legion 5 Pro (Laura)
  (49, 1, 1399.0, 1399.0, FALSE,   0.0, 30, 34),  -- Gram 16 (Laura)
  (50, 1,  549.0,  549.0, FALSE,   0.0, 27, 35),  -- PlayStation 5 (Laura)
  (51, 1, 1499.0, 1499.0, FALSE,   0.0, 23, 36),  -- EliteBook 840 (Elena)
  (52, 1, 1599.0, 1599.0, FALSE,   0.0, 21, 37),  -- Latitude 7440 (Elena)
  (53, 1,  199.0,  199.0, FALSE,   0.0, 15, 38),  -- Galaxy Buds Pro 2 (Pablo)
  (54, 1,  399.0,  399.0, FALSE,   0.0, 26, 39),  -- Sony WH-1000XM5 (Alvaro)
  (55, 1,  349.0,  349.0, FALSE,   0.0, 16, 40);  -- Pixel Watch 2 (Nuria)

-- Lineas de los pedidos 41-58 (los que comparten dia con otros).
INSERT INTO linea_pedido (id, cantidad, precio_unitario, subtotal, garantia_extendida, coste_garantia, id_producto, id_pedido) VALUES
  (56, 1,  279.0,  279.0, FALSE,   0.0,  7, 41),  -- Ana: AirPods Pro 2
  (57, 1,  349.0,  349.0, FALSE,   0.0, 16, 41),  -- Ana: Pixel Watch 2
  (58, 1,  199.0,  199.0, FALSE,   0.0, 15, 42),  -- Rafael: Galaxy Buds Pro 2
  (59, 1, 1099.0, 1099.0, FALSE,   0.0, 25, 43),  -- Carlos: ZenBook 14 OLED
  (60, 1,  279.0,  279.0, FALSE,   0.0,  7, 44),  -- Miguel: AirPods Pro 2
  (61, 1,  999.0,  999.0, FALSE,   0.0,  5, 45),  -- Laura: Pixel 8 Pro
  (62, 1,  279.0,  279.0, FALSE,   0.0,  7, 45),  -- Laura: AirPods Pro 2
  (63, 1,  199.0,  199.0, FALSE,   0.0, 15, 46),  -- Clara: Galaxy Buds Pro 2
  (64, 1,  699.0,  699.0, FALSE,   0.0, 10, 47),  -- David: iPad Air
  (65, 1,  279.0,  279.0, FALSE,   0.0,  7, 48),  -- Irene: AirPods Pro 2
  (66, 1,  799.0,  799.0, FALSE,   0.0, 14, 49),  -- Javier: Galaxy Tab S9
  (67, 1,  899.0,  899.0, FALSE,   0.0, 13, 50),  -- Sara: Apple Watch Ultra 2
  (68, 1,  279.0,  279.0, FALSE,   0.0,  7, 50),  -- Sara: AirPods Pro 2
  (69, 1,  279.0,  279.0, FALSE,   0.0,  7, 51),  -- Pablo: AirPods Pro 2
  (70, 1, 1399.0, 1399.0, FALSE,   0.0, 17, 52),  -- Julia: Surface Pro 9
  (71, 1, 1299.0, 1299.0, TRUE,  129.9,  1, 53),  -- Marta: MacBook Air M3 + garantia
  (72, 1, 1199.0, 1199.0, FALSE,   0.0,  2, 54),  -- Ana: Galaxy S24 Ultra
  (73, 1,  999.0,  999.0, FALSE,   0.0,  5, 55),  -- Javier: Pixel 8 Pro
  (74, 1,  279.0,  279.0, FALSE,   0.0,  7, 56),  -- Sergio: AirPods Pro 2
  (75, 1,  399.0,  399.0, FALSE,   0.0, 26, 57),  -- Hector: Sony WH-1000XM5
  (76, 1,  549.0,  549.0, FALSE,   0.0, 11, 58);  -- David: AirPods Max

ALTER SEQUENCE linea_pedido_seq RESTART WITH 77;
