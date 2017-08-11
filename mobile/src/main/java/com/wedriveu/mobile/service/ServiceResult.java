package com.wedriveu.mobile.service;

/**
 * The result of a service operation.
 *
 * @param <T> the type of the result object
 * @author Nicola Lasagni on 09/08/2017.
 */
public class ServiceResult<T> {

    private T result;
    private String errorMessage;

    public ServiceResult(T result, String errorMessage) {
        this.result = result;
        this.errorMessage = errorMessage;
    }

    /**
     * Indicates if the service operation succeeded or not.
     *
     * @return the {@code succeeded} value
     */
    public boolean succeeded() {
        return errorMessage != null && !errorMessage.isEmpty();
    }

    /**
     * Gets the service operation result.
     *
     * @return the service operation result
     */
    public T getResult() {
        return result;
    }

    /**
     * Gets service operation  error message.
     *
     * @return the service operation  error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ServiceResult)) {
            return false;
        }
        ServiceResult<?> otherResult = (ServiceResult<?>) other;
        return (result != null ? result.equals(otherResult.result) : otherResult.result == null) &&
                (errorMessage != null ? errorMessage.equals(otherResult.errorMessage) : otherResult.errorMessage == null);
    }

    @Override
    public int hashCode() {
        int result1 = result != null ? result.hashCode() : 0;
        result1 = 31 * result1 + (errorMessage != null ? errorMessage.hashCode() : 0);
        return result1;
    }

    @Override
    public String toString() {
        return "ServiceResult{" +
                "result=" + result +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
