package project.wpl.exception;

public class StocksNotFoundException  extends  RuntimeException{

    public StocksNotFoundException(String message)
    {
        super(message);
    }

}
