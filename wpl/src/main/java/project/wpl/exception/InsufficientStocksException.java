package project.wpl.exception;

public class InsufficientStocksException extends RuntimeException {
    public InsufficientStocksException(String  message){
        super(message);
    }

}
