package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.exception;

/**
 * Se lanza cuando el tipo de lista solicitado no corresponde a ninguna implementación disponible.
 * Se traduce a un {@code 400 Bad Request}.
 */
public class InvalidListTypeException extends RuntimeException {

    /** El texto que no se pudo interpretar como tipo de lista. */
    private final String requestedType;

    /**
     * Crea la excepción con el tipo no reconocido y los tipos admitidos.
     *
     * @param requestedType el texto que no se pudo interpretar
     * @param supported la lista de tipos admitidos, para la ayuda del mensaje
     */
    public InvalidListTypeException(String requestedType, String supported) {
        super("Unknown list type '" + requestedType + "'. Supported types: " + supported);
        this.requestedType = requestedType;
    }

    /**
     * Devuelve el texto no reconocido.
     *
     * @return el texto que no se pudo interpretar como tipo de lista
     */
    public String getRequestedType() {
        return requestedType;
    }
}
