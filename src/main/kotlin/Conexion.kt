import java.sql.*
import java.time.LocalDate


class Conexion(val usuario:String, val contrasenia:String){

    var conexion: Connection? = null

    init {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            throw SQLException("La base de datos no esta encendida")
        }
    }


    fun conectarBD(): Connection {
        val direccionBD = "jdbc:mysql://localhost:3306/ventas"
        val usuario = usuario
        val contrasenia = contrasenia
        try {
            if (conexion == null || conexion!!.isClosed) {
                conexion = DriverManager.getConnection(direccionBD, usuario, contrasenia)
                println("Bienvenido")
            }
            return conexion!!
        } catch (e: SQLException) {
            throw e
        }
    }


    fun cerrarBD() {
        try {
            conexion?.close()
        } catch (e: SQLException) {
            throw e
        }
    }

    fun crearTablaProductos(){
        try {
            val tabla1 = "CREATE TABLE Productos (\n" +
                    "    id INT PRIMARY KEY,\n" +
                    "    nombre VARCHAR(255) NOT NULL,\n" +
                    "    stock INT NOT NULL\n" +
                    ");"
            val creaTabla1: PreparedStatement = conexion!!.prepareStatement(tabla1)
            creaTabla1.executeUpdate()
        }catch (e: Exception){
            System.err.println("Error de creacion de Productos")
        }

    }

    fun crearTablaMovimientos(){
        try {
            val tabla2 = "CREATE TABLE Movimientos (\n" +
                    "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    tipo VARCHAR(10) NOT NULL,\n" +
                    "    monto DOUBLE NOT NULL,\n" +
                    "    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                    ");"
            val creaTabla2: PreparedStatement = conexion!!.prepareStatement(tabla2)
            creaTabla2.executeUpdate()
        }catch (e: Exception){
            System.err.println("Error de creacion de Movimientos")
        }
    }

    fun crearTablaCompras(){
        try {
            val tabla2 = "CREATE TABLE Compras (\n" +
                    "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    id_producto INT NOT NULL,\n" +
                    "    cantidad INT NOT NULL,\n" +
                    "    precio_unitario DOUBLE NOT NULL,\n" +
                    "    total DOUBLE NOT NULL,\n" +
                    "    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    FOREIGN KEY (id_producto) REFERENCES Productos(id)\n" +
                    ");"
            val creaTabla2: PreparedStatement = conexion!!.prepareStatement(tabla2)
            creaTabla2.executeUpdate()
        }catch (e: Exception){
            System.err.println("Error de creacion de Compras")
        }
    }

    fun crearTablaVentas(){
        try {
            val tabla2 = "CREATE TABLE Ventas (\n" +
                    "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    id_producto INT NOT NULL,\n" +
                    "    cantidad INT NOT NULL,\n" +
                    "    precio_unitario DOUBLE NOT NULL,\n" +
                    "    total DOUBLE NOT NULL,\n" +
                    "    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    FOREIGN KEY (id_producto) REFERENCES Productos(id)\n" +
                    ");\n"
            val creaTabla2: PreparedStatement = conexion!!.prepareStatement(tabla2)
            creaTabla2.executeUpdate()
        }catch (e: Exception){
            System.err.println("Error de creacion de Ventas")
        }
    }


    fun crearTablaSaldo(){
        try {
            val tabla2 = "CREATE TABLE Saldo (\n" +
                    "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    importe DOUBLE NOT NULL,\n" +
                    "    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                    ");"
            val creaTabla2: PreparedStatement = conexion!!.prepareStatement(tabla2)
            creaTabla2.executeUpdate()
        }catch (e: Exception){
            System.err.println("Error de creacion de Saldo")
        }
    }

    /**
     * inserta productos con id
     * @param id
     * @param nombre
     * @param stock
     */
    fun insertarProductos(id:Int, nombre:String, stock:Int){
        try {
            val insertar = "INSERT INTO Productos(id, nombre, stock) VALUES(?, ? ,?)"
            val insercion: PreparedStatement = conexion!!.prepareStatement(insertar)
            insercion.setInt(1,id)
            insercion.setString(2, nombre)
            insercion.setInt(3, stock)
            insercion.executeUpdate()
        }catch (e:Exception){
            System.err.println("Los productos ya estaban")
        }
    }


    /**
     * Num de productos
     */
    fun numProductosStockCero(){

        val productos = ArrayList<Producto>()

        try {
            val insert: PreparedStatement = conexion!!.prepareStatement("SELECT id, nombre, stock AS id, nombre, stock FROM productos WHERE stock = 0 for update;")
            val valor = insert.executeQuery()


            while (valor.next()) {
                //saco el id, nombre y stock dfe los productos para ir creando cada producto
                val idProductos = valor.getInt("id")
                val nombreProducto = valor.getString("nombre")
                val stockProducto = valor.getInt("stock")
                //agrego cada producto al arraylist de productos
                productos.add(Producto(idProductos,nombreProducto,stockProducto))
            }

            if (productos.isNotEmpty()) {
                println("Se encontraron ${productos.size} productos")
            } else {
                println("No se encontraron resultados")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            println("Error: ${e.message}")
        }
    }


    /**
     * saldo en caja
     */
    fun saldoCaja(): Int {
        var saldoFinal = 0
        try{
            val consulta = "SELECT SUM(importe) AS saldoFinal FROM saldo;"
            val insert: PreparedStatement = conexion!!.prepareStatement(consulta)
            val valor = insert.executeQuery()

            if(valor.next()){
                saldoFinal = valor.getInt("saldoFinal")
            }
        }catch (e:Exception){
            System.err.println("Error al realizar la consulta")
        }

        return saldoFinal;
    }

    /**
     * Inserta en la tabla compras los productos que tengan un stock de 0
     * y actualiza todos
     * @param id
     * @param idProducto
     * @param cantidad
     * @param precio por producto
     * @param total producto
     * @param fecha
     */
    /*fun insertaCompraStockCero(){
        try{
            val insertar = "INSERT INTO compras(id_producto, cantidad, precio_unitario, total, fecha)VALUES(?,?,?,?,?) FOR UPDATE;"

            val insercion: PreparedStatement = conexion!!.prepareStatement(insertar)

            //recorre los productos que tienen stock 0 que estan guardados en el arrayList
            for(producto in productos){
                insercion.setInt(1,producto.id)
                insercion.setInt(2, 1)
                println("Introduce el precio del producto $producto")//pide el precio
                val precio = readln().toInt()
                insercion.setInt(3,precio)
                insercion.setInt(4,precio)
                val datetime = LocalDate.now()
                insercion.setString(5, datetime.toString())
                insercion.executeUpdate()
            }
        }catch (e:Exception){
            System.err.println("Error al comprar producto de stock 0")
        }
    }*/



    fun actualizaProductosCompradosStockCero(){
        try {
            val actualizaStock = "UPDATE productos SET stock = 1 WHERE stock = 0;"
            val actualizacion: PreparedStatement = conexion!!.prepareStatement(actualizaStock)
            actualizacion.executeUpdate()
        }catch (e:Exception){
            System.err.println("Error al actualizar productos")
        }
    }


    fun compraProducto(){
        //insertaCompraStockCero()
        actualizaProductosCompradosStockCero()
    }


    /**
     * anade compra del producto
     */
    fun anadirCompraProducto(){
        try {
            //pide 3 veces el producto para despues introducirlo
            for(i in 0 until 3){
                println("Introduce el id del producto")
                val id_producto:Int = readln().toInt()
                println("Introduce el nombre del producto")
                val nombreProducto: String = readln()
                println("Introduce el stock del producto")
                val stock:Int = readln().toInt()
                println("Introduce la cantidad del producto")
                val cantidad:Int = readln().toInt()
                println("Introduce el total del producto")
                val total:Int = readln().toInt()

                //inserta el producto en la tabla compras
                val insertarCompra = "INSERT INTO(id, nombre, stock) compras VALUES(?,?,?)"
                val insertaC: PreparedStatement = conexion!!.prepareStatement(insertarCompra)
                insertaC.setInt(1,id_producto)
                insertaC.setString(2,nombreProducto)
                insertaC.setInt(3,stock)

                //insertaSaldo
                val datetime = LocalDate.now()
                val insertarSaldo = "INSERT INTO(importe, fecha) saldo VALUES(?, ?)"
                val insertaS: PreparedStatement = conexion!!.prepareStatement(insertarSaldo)
                val precioTotal = stock*cantidad//precio = stock * cantidad
                insertaS.setInt(1, -precioTotal)//ponemos el precio total en negativo, ya que es un importe que se debe restar
                insertaS.setString(2,datetime.toString())//inserta la fecha actual

                val insertarStock = "INSERT INTO(nombre, stock) productos VALUES(?,?)"
                val insertaS2: PreparedStatement = conexion!!.prepareStatement(insertarStock)
                insertaS2.setString(1,nombreProducto)
                insertaS.setInt(2,stock)
            }
        }catch (e:Exception){
            println("No se ha insertado el producto")
        }


    }

    /**
     * sobreescribe el metodo de insertar productos al crear las tablas y le quita el id
     * @nombre nombre producto
     * @stock stock producto
     */
    fun insertarProductos(nombre: String, stock:Int){
        try {
            val insertaProducto1 = "INSERT INTO productos(nombre,stock) VALUES(?,?)"
            val insercion: PreparedStatement = conexion!!.prepareStatement(insertaProducto1)
            insercion.setString(1,nombre)
            insercion.setInt(2,stock)
        }catch (e:Exception){
            System.err.println("Los productos ya estaban")
        }
    }

    /**
     * Llama a cada crear Tablas
     */
    fun creaTablas(){
        crearTablaProductos()
        crearTablaMovimientos()
        crearTablaCompras()
        crearTablaVentas()
        crearTablaSaldo()
        //inserta Productos
        insertarProductos("Papa frita",0)
        insertarProductos("Huevos",0)
        insertarProductos("Gazpacho",1)
        insertarProductos("Aceitunas",0)
        insertarProductos("Pollo frito",6)
    }




}