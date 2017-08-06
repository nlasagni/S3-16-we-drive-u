package com.wedriveu.shared.model;

/**
 * @author Nicola Lasagni on 04/08/2017.
 */
public class LoginResponse {

    private boolean success;
    private String errorMessage;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
