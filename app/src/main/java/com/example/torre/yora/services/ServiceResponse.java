package com.example.torre.yora.services;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Every Response class will inherit from this class. It stores additional information about the response (errors, succeeded, etc.)
 */
public abstract class ServiceResponse
{
    private static final String TAG = "ServiceResponse";

    @SerializedName("operationError")
    private String operationError; //Error that occurs on an operation wide scale. Eg. image failing to upload due to it not being in the proper format.

    @SerializedName("propertyErrors")
    private HashMap<String, String> propertyErrors;  //Validation errors. Eg. display name too long, email wasn't correctly formatted.

    private boolean isCritical; //The operation failed due to an invalid state or a bug in the code. Eg. not connected to the network, the API is down.
                                //Things that you should fix yourself or wait to try again.
                                //Critical: we messed up. Non-critical: you messed up
    private TreeMap<String, String> propertyErrorsCaseInsensitive;

    public ServiceResponse()
    {
        propertyErrors = new HashMap<>();
    }

    public ServiceResponse(String operationError)
    {
        this.operationError = operationError;
    }

    public ServiceResponse(String operationError, boolean isCritical)
    {
        this.operationError = operationError;
        this.isCritical = isCritical;
    }

    public String getOperationError()
    {
        return this.operationError;
    }

    public void setOperationError(String operationError)
    {
        this.operationError = operationError;
    }

    public boolean isCritical()
    {
        return isCritical;
    }

    public void setCritical(boolean isCritical)
    {
        this.isCritical = isCritical;
    }

    public void setCriticalError(String criticalError)
    {
        isCritical = true;
        operationError = criticalError;
    }

    public void setPropertyError(String property, String error)
    {
        propertyErrors.put(property, error);
    }

    public String getPropertyError(String property)
    {
        if (propertyErrorsCaseInsensitive == null || propertyErrorsCaseInsensitive.size() != propertyErrors.size())
        {
            propertyErrorsCaseInsensitive = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            propertyErrorsCaseInsensitive.putAll(propertyErrors);
        }
        return propertyErrorsCaseInsensitive.get(property);
    }

    public boolean succeeded()
    {
        return (operationError == null || operationError.isEmpty()) && (propertyErrors.size() == 0);
    }

    public void showErrorToast(Context context)
    {
        if (context == null || operationError == null || operationError.isEmpty())
            return;

        try
        {
            Toast.makeText(context, operationError, Toast.LENGTH_LONG).show();;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Can't create error toast", e);
        }
    }
}
