package ec.edu.espe.micro_inventario.exception;

public class DuplicateInventoryException extends RuntimeException {

    public DuplicateInventoryException(String message) {
        super(message);
    }

    public DuplicateInventoryException(Long sucursalID, String productoID) {
        super(String.format("Ya existe un registro de inventario para el producto '%s' en la sucursal '%d'",
                productoID, sucursalID));
    }
}
