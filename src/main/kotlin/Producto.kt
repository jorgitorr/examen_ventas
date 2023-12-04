class Producto(val id:Int, val nombre:String, val stock:Int) {
    override fun toString(): String {
        return "$nombre"
    }
}