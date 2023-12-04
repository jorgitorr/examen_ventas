
fun main() {
    menu()
}

fun menu(){
    var conexion = Conexion("root","")
    conexion.conectarBD()
    while (true){
        println("MENU")
        println("1. crea las tablas")
        println("2. Añade compras del producto")
        println("3. Consulta productos stock 0")
        println("4. Compra stock 0")
        println("5. Saldo")
        println("Introduce la opcion")
        var opcion = readLine()?.toInt()
        if(opcion!=0){
            when(opcion){
                1 -> conexion.creaTablas()
                2 -> conexion.anadirCompraProducto()//en desarrollo
                3 -> conexion.numProductosStockCero()
                4 -> conexion.compraProducto()
                5 -> println("el saldo en la caja es: " + conexion.saldoCaja())
            }
        }else{
            conexion.cerrarBD()
            println("Base de datos cerrada")
            println("Saliendo...")
        }
    }
}








