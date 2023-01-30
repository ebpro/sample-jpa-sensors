package fr.univtln.bruno.samples.utils.dao.exceptions;

import fr.univtln.bruno.samples.utils.AppConstants;
import fr.univtln.bruno.samples.utils.exceptions.BusinessException;

/**
 * Created by bruno on 04/12/14.
 */
public class DAOException extends BusinessException {
    public DAOException(Throwable e) {
        super(e);
    }

    public DAOException(AppConstants.ErrorCode code, String message, String developerMessage, String link) {
        super(code, message, developerMessage, link);
    }
}
