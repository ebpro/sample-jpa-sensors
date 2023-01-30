package fr.univtln.bruno.samples.utils.exceptions;

import fr.univtln.bruno.samples.utils.AppConstants;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;

/**
 * Created by bruno on 15/02/15.
 */
@Log
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusinessException extends Exception {

    /**
     * application specific error businessErrorCode
     */
    final AppConstants.ErrorCode businessErrorCode;

    /**
     * link documenting the exception
     */
    final String link;

    /**
     * detailed error description for developers
     */
    final String developerMessage;

    /**
     * @param businessErrorCode
     * @param message
     * @param developerMessage
     * @param link
     */
    public BusinessException(AppConstants.ErrorCode businessErrorCode, String message,
                             String developerMessage, String link) {
        super(message);
        this.businessErrorCode = businessErrorCode;
        this.developerMessage = developerMessage;
        this.link = link;
    }

    public BusinessException(Throwable e) {
        this(AppConstants.ErrorCode.GENERIC_EXCEPTION, e.getMessage(), null, null);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    @XmlTransient
    public synchronized Throwable getCause() {
        return super.getCause();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    @XmlTransient
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }

}
