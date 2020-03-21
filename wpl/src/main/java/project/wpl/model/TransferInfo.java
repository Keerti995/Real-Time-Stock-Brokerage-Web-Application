package project.wpl.model;

import java.util.SplittableRandom;

public class TransferInfo {


    private int fromAccountNumber;
    private String fromRoutingNumber;
    private double amountToTransfer;
    private int toAccounNumber;
    private int toRoutingNumber;



    public int getToRoutingNumber() {
        return toRoutingNumber;
    }

    public void setToRoutingNumber(int toRoutingNumber) {
        this.toRoutingNumber = toRoutingNumber;
    }

    public int getToAccounNumber() {
        return toAccounNumber;
    }

    public void setToAccounNumber(int toAccounNumber) {
        this.toAccounNumber = toAccounNumber;
    }

    public double getAmountToTransfer() {
        return amountToTransfer;
    }

    public void setAmountToTransfer(double amountToTransfer) {
        this.amountToTransfer = amountToTransfer;
    }

    public String getFromRoutingNumber() {
        return fromRoutingNumber;
    }

    public void setFromRoutingNumber(String fromRoutingNumber) {
        this.fromRoutingNumber = fromRoutingNumber;
    }

    public int getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(int fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

}
